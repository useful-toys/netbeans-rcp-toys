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
import org.openide.util.lookup.Lookups;

/**
 * An bus that propagates notifications to interested listeners.
 * <p>
 * There is on global singleton bus. Listeners may to narrow down to
 * notification they are interested of by registering to a specific bus given by
 * a category or by a dynamic context.
 * <p>
 * Listeners shall implement the {@link Listener} or {@link MultiListener}
 * interface. The former receives notifications from the bus. The later receives
 * and redirects notifications to a group of listeners added dynamically at runtime.
 * <p>
 * Listeners annotated with {@code Config(thread=CURRENT)} are called
 * immediately after a notification is dispatched, which is suitable as a
 * callback mechanism. Listeners annotated with {@code Config(thread=EDT)} are
 * called soon on the swing event thread, which is suitable for listeners that
 * update views in response to model changes.. Listeners annotated with
 * {@code Config(thread=POOL)} are called later in a thread pool, which is
 * suitable for notifications that start a background processing.
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

    public static interface MultiListener extends Listener {

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
        final Lookup listenerLookup;
        if (category == null) {
            listenerLookup = Lookups.forPath("Bus");
        } else {
            listenerLookup = Lookups.forPath("Bus/" + category);
        }
        final Collection<? extends Listener> busListeners = listenerLookup.lookupAll(Listener.class);
        for (Listener listener : busListeners) {
            Thread listenerThread = getThread(listener);
            boolean currentThreadAdded;
            boolean viewThreadAdded;

            switch (listenerThread) {
                case CURRENT:
                    currentThreadAdded = permanentCurrentThreadListeners.add(listener);

                    if (currentThreadAdded) {
                        debug("Constructor. Current thread: added. bus={0}, listener={1}", this, listener);
                    } else {
                        warn("Constructor. Current thread: exists. bus={0}, listener={1}", this, listener);
                    }

                    break;

                default:
                    viewThreadAdded = permanentViewThreadListeners.add(listener);

                    if (viewThreadAdded) {
                        debug("Constructor. View thread: added. bus={0}, listener={1}", this, listener);
                    } else {
                        warn("Constructor. View thread: exists. bus={0}, listener={1}", this, listener);
                    }
            }
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

    public final void addListener(final Listener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }
        Thread listenerThread = getThread(listener);

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
                    debug("Add listener. Current thread: added. bus={0}, listener={1}", this, listener);
                } else {
                    warn("Add listener. Current thread: exists. bus={0}, listener={1}", this, listener);
                }

                break;

            default:
                currentThraedListenersLock.lock();
                try {
                    viewThreadAdded = newNonPermanentViewThreadListeners.add(listener);
                    if (viewThreadAdded) {
                        nonPermanentViewThreadListeners = null;
                    }
                } finally {
                    currentThraedListenersLock.unlock();
                }

                if (viewThreadAdded) {
                    debug("Add listener. View thread: added. bus={0}, listener={1}", this, listener);
                } else {
                    warn("Add listener. View thread: exists. bus={0}, listener={1}", this, listener);
                }
        }
    }

    private Thread getThread(final Listener listener) {
        Bus.Config annotation = listener.getClass().getAnnotation(Bus.Config.class);
        Bus.Thread listenerThread;
        if (annotation == null || annotation.thread() == null) {
            listenerThread = Thread.EDT;
        } else {
            listenerThread = annotation.thread();
        }
        return listenerThread;
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
            viewThreadRemoved = newNonPermanentViewThreadListeners.remove(listener);
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
            debug("Remove listener. Current thread: removed. bus={0}, listener={1}", this, listener);
        }
        if (viewThreadRemoved) {
            debug("Remove listener. View thread: removed. bus={0}, listener={1}", this, listener);
        }
        if (!localThreadRemoved && !viewThreadRemoved) {
            warn("Remove listener: inexists. bus={0}, listener={1}", this, listener);
        }
    }

    public final void callListeners(final Caller caller) {
        info("Call listeners. bus={0}, caller={1}", caller);

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
                if (listener instanceof MultiListener && caller.isCompatible(listener)) {
                    trace("Call current thread multi-listener. listener={0}, caller={1}", listener, caller);
                    callCurrentThreadListenersImpl(((MultiListener) listener).getCurrentThreadListeners(), caller);
                    caller.callListener(listener);
                } else if (caller.isCompatible(listener)) {
                    trace("Call current thread listener. listener={0}, caller={1}", listener, caller);
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
                if (listener instanceof MultiListener && caller.isCompatible(listener)) {
                    trace("Call view thread multi-listener. listener={0}, caller={1}", listener, caller);
                    callViewThreadListenersImpl(((MultiListener) listener).getViewThreadListeners(), caller);
                    caller.callListener(listener);
                } else if (caller.isCompatible(listener)) {
                    trace("Call view thread listener. listener={0}, caller={1}", listener, caller);
                    caller.callListener(listener);
                }
            } catch (Exception | Error e) {
                error("Call listener failed. listener={0}, caller={1}", e, listener, caller);
            }
        }
    }

    private static void trace(String message, Object... parameters) {
        if (logger.isLoggable(Level.FINEST)) {
            LogRecord record = new LogRecord(Level.FINEST, message);
            record.setParameters(parameters);
            record.setLoggerName(logger.getName());
            logger.log(record);
        }
    }

    private static void debug(String message, Object... parameters) {
        if (logger.isLoggable(Level.FINE)) {
            LogRecord record = new LogRecord(Level.FINE, message);
            record.setParameters(parameters);
            record.setLoggerName(logger.getName());
            logger.log(record);
        }
    }

    private static void warn(String message, Object... parameters) {
        if (logger.isLoggable(Level.WARNING)) {
            LogRecord record = new LogRecord(Level.WARNING, message);
            record.setParameters(parameters);
            record.setLoggerName(logger.getName());
            logger.log(record);
        }
    }

    private static void info(String message, Object... parameters) {
        if (logger.isLoggable(Level.INFO)) {
            LogRecord record = new LogRecord(Level.INFO, message);
            record.setParameters(parameters);
            record.setLoggerName(logger.getName());
            logger.log(record);
        }
    }

    private static void error(String message, Throwable throwable, Object... parameters) {
        if (logger.isLoggable(Level.SEVERE)) {
            LogRecord record = new LogRecord(Level.SEVERE, message);
            record.setParameters(parameters);
            record.setLoggerName(logger.getName());
            record.setThrown(throwable);
            logger.log(record);
        }
    }

    @Override
    public String toString() {
        return "Bus{" + this.context + "/" + this.category + '}';
    }
}
