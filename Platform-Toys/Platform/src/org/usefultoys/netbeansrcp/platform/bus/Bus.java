/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.bus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;

/**
 * Mecanismo de comunicação entre módulos. Não requer que os módulos conheçam um
 * ao outro, desde que concordem com um conjunto de mensagens pré-estabelecido.
 * <p>
 * É possível obter um barramento para cada tema de mensagens. Os listeners
 * podem associar-se somente aos barramentos relevantes, que evita o recebimento
 * de mensagens que não são de seu interesse.
 * <p>
 * O barramento está associado a um objeto que identifica o tema das mensagens.
 * Quando este objeto for eliminado pelo garbage collector, o barramento deixará
 * de existir automaticamente, as mensagens pendentes serão descartadas e o
 * vínculo com os listeners é desfeito.
 * <p>
 * É usado somente uma única {@link RequestProcessor.Task} para tratar as
 * mensagens de todos os barramentos. A Task é re-agendada ou re-iniciada quando
 * novas mensagens são adicionadas a uma dos barramentos.
 *
 * @author Daniel Felix Ferber
 */
public class Bus {

    /**
     * Indicates that the class contains methods that handle notifications
     * published by the bus.
     *
     * @author Daniel Felix Ferber
     */
    public static interface Listener {
        // no methods expected
    }

    public static interface ProxyListener extends Listener {

        List<Listener> getCurrentThreadListeners();

        List<Listener> getViewThreadListeners();

        void addListener(Listener listener);

        void removeListener(Listener listener);
    }

    public static interface Caller {

        boolean isCompatible(Listener listener);

        void callListener(Listener listener);
    }

    /**
     * Enumerates threads used by the bus to execute listener calls.
     *
     * @author Daniel Felix Ferber
     */
    public static enum Thread {

        EDT, CURRENT, POOL
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Config {

        Thread thread();
    }

    private static final Logger logger = Logger.getLogger(Bus.class.getName());

    private final String category;
    private final String context;

    private final ReentrantLock currentThraedListenersLock = new ReentrantLock();
    private final List<Listener> permanentCurrentThreadListeners = new ArrayList<>();
    private List<Listener> nonPermanentCurrentThreadListeners;
    private final Set<Listener> newNonPermanentCurrentThreadListeners = new LinkedHashSet<>();

    private final ReentrantLock viewThreadListenersLock = new ReentrantLock();
    private final Set<Listener> permanentViewThreadListeners = new LinkedHashSet<>();
    private List<Listener> nonPermanentViewThreadListeners;
    private final Set<Listener> newNonPermanentViewThreadListeners = new LinkedHashSet<>();

    private static final ReentrantLock instanceLock = new ReentrantLock();
    private static final Map<String, Map<String, Bus>> BUS_BY_CATEGORY_CONTEXT = new HashMap<>();
    private static final Map<String, Bus> BUS_BY_CATEGORY = new HashMap<>();

    private static final Bus DEFAULT_BUS = new Bus(null, "default");

    public static synchronized Bus getIntance() {
        debug("getInstance on default EventBus.");
        return DEFAULT_BUS;
    }

    public static synchronized Bus getInstance(String category) {
        if (category == null) {
            throw new IllegalArgumentException("category == null");
        }

        Bus bus = null;
        boolean created = false;
        try {
            instanceLock.lock();
            bus = BUS_BY_CATEGORY.get(category);
            if (bus == null) {
                BUS_BY_CATEGORY.put(category, bus = new Bus(null, category));
                created = true;
            }
        } finally {
            instanceLock.unlock();
        }
        if (created) {
            debug("getInstance creates new bus. category={0}", category);
        } else {
            debug("getInstance reuses existing bus. category={0}", category);
        }
        return bus;
    }

    public static synchronized Bus getInstance(final String category, final String context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        if (category == null) {
            throw new IllegalArgumentException("category == null");
        }

        Map<String, Bus> busByCategory = BUS_BY_CATEGORY_CONTEXT.get(category);
        if (busByCategory == null) {
            BUS_BY_CATEGORY_CONTEXT.put(category, busByCategory = new HashMap<>());
        }
        Bus bus = busByCategory.get(context);
        if (bus == null) {
            busByCategory.put(category, bus = new Bus(category, context));
            debug("getInstance creates new EventBus. category={0}, context={1}", category, context);
        } else {
            debug("getInstance reuses existing EventBus. category={0}, context={1}", category, context);
        }

        return bus;
    }

    Bus(final String category, final String context) {
        /* Non visible constructor. */

        info("Constructor. category={0}, name={1}", category, context);
        this.category = category;
        this.context = context;

        /* Popula os listeners registrados no layer.xml. */
        final Set<Listener> usedListener = new HashSet<>();
        final Lookup busLookup = Lookups.forPath(category == null ? "Safe" : "Safe/" + category);
        final Collection<? extends Listener> busListeners = busLookup.lookupAll(Listener.class);
        for (Listener listener : busListeners) {
            debug("New EventBus. Register global listener. listener={0}", listener);
            permanentCurrentThreadListeners.add(listener);
            usedListener.add(listener);
        }
        final Lookup edtBusLookup = Lookups.forPath(category == null ? "EDT" : "EDT/" + category);
        final Collection<? extends Listener> edtBusListeners = edtBusLookup.lookupAll(Listener.class);
        for (Listener listener : edtBusListeners) {
            debug("New EventBus. Register global EDT listener. listener={0}", listener);
            permanentViewThreadListeners.add(listener);
            usedListener.add(listener);
        }

        /**
         * Support legacy registrarion via xml.
         */
        final Lookup edtBusLookup2 = Lookup.getDefault();
        final Collection<? extends Listener> edtBusListeners2 = edtBusLookup2.lookupAll(Listener.class);
        for (Listener listener : edtBusListeners2) {
            if (usedListener.contains(listener)) {
                continue;
            }
            debug("New EventBus. Register global EDT listener. listener={0}", listener);
            permanentViewThreadListeners.add(listener);
        }
    }

    public List<Listener> getPermanentCurrentThreadListeners() {
        return permanentCurrentThreadListeners;
    }

    private List<Listener> getNonPermanentCurrentThreadListeners() {
        currentThraedListenersLock.lock();
        try {
            if (nonPermanentCurrentThreadListeners == null) {
                nonPermanentCurrentThreadListeners = new ArrayList<>(newNonPermanentCurrentThreadListeners);
            }
            return nonPermanentCurrentThreadListeners;
        } finally {
            currentThraedListenersLock.unlock();
        }
    }

    private Set<Listener> getPermanentViewThreadListeners() {
        return permanentViewThreadListeners;
    }

    private List<Listener> getNonPermanentViewThreadListeners() {
        viewThreadListenersLock.lock();
        try {
            if (nonPermanentViewThreadListeners == null) {
                nonPermanentViewThreadListeners = new ArrayList<>(newNonPermanentViewThreadListeners);
            }
            return nonPermanentViewThreadListeners;
        } finally {
            viewThreadListenersLock.unlock();
        }
    }

    public final String getDisplayName() {
        return this.context + this.category;
    }

    public final void register(final Listener listener) {
        addListener(listener);
    }

    public final void addListener(final Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }

        Bus.Config annotation = listener.getClass().getAnnotation(Bus.Config.class);
        Bus.Thread listenerThread = annotation.thread();

        boolean currentThreadAdded;
        boolean viewThreadAdded;

        switch (listenerThread) {
            case CURRENT:
                currentThraedListenersLock.lock();
                try {
                    currentThreadAdded = newNonPermanentCurrentThreadListeners.add(listener);
                    if (currentThreadAdded) {
                        nonPermanentViewThreadListeners = null;
                    }
                } finally {
                    currentThraedListenersLock.unlock();
                }

                if (currentThreadAdded) {
                    debug("Register current thread listener: added. bus={0}, listener={1}", this.getDisplayName(), listener);
                } else {
                    warn("Register current thread listener: already registered. bus={0}, listener={1}", this.getDisplayName(), listener);
                }

                break;

            default:
                currentThraedListenersLock.lock();
                try {
                    viewThreadAdded = nonPermanentViewThreadListeners.add(listener);
                    if (viewThreadAdded) {
                        nonPermanentViewThreadListeners = null;
                    }
                } finally {
                    currentThraedListenersLock.unlock();
                }

                if (viewThreadAdded) {
                    debug("Register view thread listener: added. bus={0}, listener={1}", this.getDisplayName(), listener);
                } else {
                    warn("Register view thread listener: already registered. bus={0}, listener={1}", this.getDisplayName(), listener);
                }
        }
    }

    public final void removeListener(final Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }

        boolean localThreadRemoved;
        boolean viewThreadRemoved;

        currentThraedListenersLock.lock();
        try {
            localThreadRemoved = newNonPermanentCurrentThreadListeners.remove(listener);
            viewThreadRemoved = nonPermanentViewThreadListeners.remove(listener);
            if (localThreadRemoved) {
                nonPermanentCurrentThreadListeners = null;
            }
            if (viewThreadRemoved) {
                nonPermanentViewThreadListeners = null;
            }
        } finally {
            currentThraedListenersLock.unlock();
        }

        if (localThreadRemoved) {
            debug("Unregister local thread listener: removed. bus={0}, listener={1}", this.getDisplayName(), listener);
        }
        if (viewThreadRemoved) {
            debug("Unregister view thread listener: removed. bus={0}, listener={1}", this.getDisplayName(), listener);
        }
        if (!localThreadRemoved && !viewThreadRemoved) {
            warn("Unregister listener: not yet registered! bus={0}, listener={1}", this.getDisplayName(), listener);
        }
    }

    @Deprecated
    public final void dispatch(final Caller caller) {
        callListeners(caller);
    }

    public final void callListeners(final Caller caller) {
        debug("Call listeners. bus={0}, caller={1}", this.getDisplayName(), caller);

        callCurrentThreadListenersImpl(getPermanentCurrentThreadListeners(), caller);
        callCurrentThreadListenersImpl(getNonPermanentCurrentThreadListeners(), caller);

        SwingUtilities.invokeLater(() -> {

            callViewThreadListenersImpl(getPermanentViewThreadListeners(), caller);
            callViewThreadListenersImpl(getNonPermanentViewThreadListeners(), caller);
        });
    }

    private static void callCurrentThreadListenersImpl(Collection<Listener> listenersCollection, final Caller caller) {
        for (Listener listener : listenersCollection) {
            try {
                if (listener instanceof ProxyListener && caller.isCompatible(listener)) {
                    debug("Call proxy listener. listener={0}, caller={1}", listener, caller);
                    callCurrentThreadListenersImpl(((ProxyListener) listener).getCurrentThreadListeners(), caller);
                    caller.callListener(listener);
                } else if (caller.isCompatible(listener)) {
                    debug("Call listener. listener={0}, caller={1}", listener, caller);
                    caller.callListener(listener);
                }
            } catch (Exception | Error e) {
                error("Call listener failed. listener={0}, caller={1}", e, listener, caller);
            }
        }
    }

    private static void callViewThreadListenersImpl(Collection<Listener> listenersCollection, final Caller caller) {
        for (Listener listener : listenersCollection) {
            try {
                if (listener instanceof ProxyListener && caller.isCompatible(listener)) {
                    debug("Call proxy listener. listener={0}, caller={1}", listener, caller);
                    callViewThreadListenersImpl(((ProxyListener) listener).getViewThreadListeners(), caller);
                    caller.callListener(listener);
                } else if (caller.isCompatible(listener)) {
                    debug("Call listener. listener={0}, caller={1}", listener, caller);
                    caller.callListener(listener);
                }
            } catch (Exception | Error e) {
                error("Call listener failed. listener={0}, caller={1}", e, listener, caller);
            }
        }
    }

    private static void debug(String message, Object... parameters) {
        if (logger.isLoggable(Level.FINE)) {
            LogRecord record = new LogRecord(Level.FINE, message);
            record.setParameters(parameters);
            logger.log(record);
        }
    }

    private static void warn(String message, Object... parameters) {
        if (logger.isLoggable(Level.WARNING)) {
            LogRecord record = new LogRecord(Level.WARNING, message);
            record.setParameters(parameters);
            logger.log(record);
        }
    }

    private static void info(String message, Object... parameters) {
        if (logger.isLoggable(Level.INFO)) {
            LogRecord record = new LogRecord(Level.INFO, message);
            record.setParameters(parameters);
            logger.log(record);
        }
    }

    private static void error(String message, Throwable throwable, Object... parameters) {
        if (logger.isLoggable(Level.SEVERE)) {
            LogRecord record = new LogRecord(Level.SEVERE, message);
            record.setParameters(parameters);
            record.setThrown(throwable);
            logger.log(record);
        }
    }

    @Override
    public String toString() {
        return "Bus{" + this.getDisplayName() + '}';
    }
}
