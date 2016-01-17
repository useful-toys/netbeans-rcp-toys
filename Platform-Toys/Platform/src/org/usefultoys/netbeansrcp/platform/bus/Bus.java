/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.bus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.openide.util.Lookup;
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

    public static abstract class Caller<ListenerType extends Bus.Listener> {

        private final Class<ListenerType> listenerType;

        public Caller(final Class<ListenerType> listenerType) {
            super();
            if (listenerType == null) {
                throw new IllegalArgumentException();
            }
            this.listenerType = listenerType;
        }

        protected abstract void callListener(ListenerType listener);

        private void callListenerImpl(Listener listener) {
            callListener((ListenerType) listener);
        }
    }

    private static final Logger logger = Logger.getLogger(Bus.class.getName());

    private final String category;
    private final String context;

    private final ReentrantLock listenerCollectionLock = new ReentrantLock();
    private final Set<Listener> safeGlobalListeners = new LinkedHashSet<>();
    private final Set<Listener> edtGlobalListeners = new LinkedHashSet<>();
    private final Set<Listener> newSafeLocalListeners = new LinkedHashSet<>();
    private final Set<Listener> newEdtLocalListeners = new LinkedHashSet<>();
    private List<Listener> currentSafeLocalListeners;
    private List<Listener> currentEdtLocalListeners;

    private static final ReentrantLock instanceLock = new ReentrantLock();
    private static final Map<String, Map<String, Bus>> BUS_BY_CATEGORY_CONTEXT = new HashMap<>();
    private static final Map<String, Bus> BUS_BY_CATEGORY = new HashMap<>();

    private static final Bus DEFAULT_BUS = new Bus(null, "default");

    public static synchronized Bus getIntance() {
        logFine("getInstance on default EventBus.");
        return DEFAULT_BUS;
    }

    public static synchronized Bus getIntance(String category) {
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
            logFine("getInstance creates new bus. category={0}", category);
        } else {
            logFine("getInstance reuses existing bus. category={0}", category);
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

        Bus bus = null;
        boolean created = false;
        try {
            instanceLock.lock();
            Map<String, Bus> busByCategory = BUS_BY_CATEGORY_CONTEXT.get(category);
            if (busByCategory == null) {
                BUS_BY_CATEGORY_CONTEXT.put(category, busByCategory = new HashMap<>());
            }
            bus = busByCategory.get(context);
            if (bus == null) {
                busByCategory.put(category, bus = new Bus(category, context));
                created = true;
            }
        } finally {
            instanceLock.unlock();
        }
        if (created) {
            logFine("getInstance creates new bus. category={0}, context={1}", category, context);
        } else {
            logFine("getInstance reuses existing bus. category={0}, context={1}", category, context);
        }
        return bus;
    }

    Bus(final String category, final String context) {
        /* Non visible constructor. */

        logInfo("Constructor. category={0}, name={1}", category, context);
        this.category = category;
        this.context = context;

        /* Popula os listeners registrados no layer.xml. */
        final Set<Listener> usedListener = new HashSet<>();
        final Lookup busLookup = Lookups.forPath(category == null ? "Safe" : "Safe/" + category);
        final Collection<? extends Listener> busListeners = busLookup.lookupAll(Listener.class);
        for (Listener listener : busListeners) {
            logFine("New EventBus. Register global listener. listener={0}", listener);
            safeGlobalListeners.add(listener);
            usedListener.add(listener);
        }
        final Lookup edtBusLookup = Lookups.forPath(category == null ? "EDT" : "EDT/" + category);
        final Collection<? extends Listener> edtBusListeners = edtBusLookup.lookupAll(Listener.class);
        for (Listener listener : edtBusListeners) {
            logFine("New EventBus. Register global EDT listener. listener={0}", listener);
            edtGlobalListeners.add(listener);
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
            logFine("New EventBus. Register legacy EDT listener. listener={0}", listener);
            edtGlobalListeners.add(listener);
        }
    }

    public final String getDisplayName() {
        return this.context + this.category;
    }

    /**
     * Support legacy EDT listener.
     */
    public final void register(final Listener listener) {
        edtRegister(listener);
    }

    public final void safeRegister(final Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }

        boolean added;
        listenerCollectionLock.lock();
        try {
            added = newSafeLocalListeners.add(listener);
            if (added) {
                currentSafeLocalListeners = null;
            }
        } finally {
            listenerCollectionLock.unlock();
        }

        if (added) {
            logFine("Register local listener. bus={0}, listener={1}", this.getDisplayName(), listener);
        } else {
            logWarn("Register local listener: already registered! bus={0}, listener={1}", this.getDisplayName(), listener);
        }
    }

    public final void edtRegister(final Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }

        boolean added;
        listenerCollectionLock.lock();
        try {
            added = newEdtLocalListeners.add(listener);
            if (added) {
                currentEdtLocalListeners = null;
            }
        } finally {
            listenerCollectionLock.unlock();
        }

        if (added) {
            logFine("Register local listener. bus={0}, listener={1}", this.getDisplayName(), listener);
        } else {
            logWarn("Register local listener: already registered! bus={0}, listener={1}", this.getDisplayName(), listener);
        }
    }

    public final void unregister(final Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }

        boolean removed;
        listenerCollectionLock.lock();
        try {
            removed = newSafeLocalListeners.remove(listener) || newEdtLocalListeners.remove(listener);
            if (removed) {
                currentSafeLocalListeners = null;
                currentEdtLocalListeners = null;
            }
        } finally {
            listenerCollectionLock.unlock();
        }

        if (removed) {
            logFine("Unregister local listener. bus={0}, listener={1}", this.getDisplayName(), listener);
        } else {
            logWarn("Unregister local listener: not yet registered! bus={0}, listener={1}", this.getDisplayName(), listener);
        }
    }

    public final void dispatch(final Caller<?> caller) {
        logFine("Dispatch caller. bus={0}, caller={1}", this.getDisplayName(), caller);

        try {
            listenerCollectionLock.lock();
            if (currentSafeLocalListeners == null) {
                currentSafeLocalListeners = new ArrayList<>(newSafeLocalListeners);
            }
        } finally {
            listenerCollectionLock.unlock();
        }

        callListeners(safeGlobalListeners, caller);
        callListeners(newSafeLocalListeners, caller);

        SwingUtilities.invokeLater(() -> {
            try {
                listenerCollectionLock.lock();
                if (currentEdtLocalListeners == null) {
                    currentEdtLocalListeners = new ArrayList<>(newEdtLocalListeners);
                }
            } finally {
                listenerCollectionLock.unlock();
            }

            callListeners(edtGlobalListeners, caller);
            callListeners(currentEdtLocalListeners, caller);
        });
    }

    private static void callListeners(Collection<Listener> listenersCollection, final Caller<?> caller) {
        for (Listener listener : listenersCollection) {
            try {
                if (caller.listenerType.isAssignableFrom(listener.getClass())) {
                    logFine("Event listener. listener={0}, caller={1}", listener, caller);
                    caller.callListenerImpl(listener);
                }
            } catch (Exception | Error e) {
                logSereve("Event listener failed. listener={0}, caller={1}", e, listener, caller);
            }
        }
    }

    private static void logFine(String message, Object... parameters) {
        if (logger.isLoggable(Level.FINE)) {
            LogRecord record = new LogRecord(Level.FINE, message);
            record.setParameters(parameters);
            logger.log(record);
        }
    }

    private static void logWarn(String message, Object... parameters) {
        if (logger.isLoggable(Level.WARNING)) {
            LogRecord record = new LogRecord(Level.WARNING, message);
            record.setParameters(parameters);
            logger.log(record);
        }
    }

    private static void logInfo(String message, Object... parameters) {
        if (logger.isLoggable(Level.INFO)) {
            LogRecord record = new LogRecord(Level.INFO, message);
            record.setParameters(parameters);
            logger.log(record);
        }
    }

    private static void logSereve(String message, Throwable throwable, Object... parameters) {
        if (logger.isLoggable(Level.SEVERE)) {
            LogRecord record = new LogRecord(Level.SEVERE, message);
            record.setParameters(parameters);
            record.setThrown(throwable);
            logger.log(record);
        }
    }

    @Override
    public String toString() {
        return "EventBus{" + this.getDisplayName() + '}';
    }
}
