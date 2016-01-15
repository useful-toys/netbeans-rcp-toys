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
public class EventBus {

    private static final Logger logger = Logger.getLogger(EventBus.class.getName());

    private final String category;
    private final String context;

    private final ReentrantLock lockListener = new ReentrantLock();
    private final Queue<Event<?>> edtEventQueue = new ConcurrentLinkedQueue<>();
    private final Set<EventListener> globalListeners = new LinkedHashSet<>();
    private final Set<EventListener> edtGlobalListeners = new LinkedHashSet<>();
    private final Set<EventListener> localListeners = new LinkedHashSet<>();
    private final Set<EventListener> edtLocalListeners = new LinkedHashSet<>();
    private List<EventListener> localListenersList;
    private List<EventListener> edtLocalListenersList;

    private static final Map<String, Map<String, EventBus>> EVENT_BUS_BY_CATEGORY_CONTEXT = new HashMap<>();
    private static final Map<String, EventBus> EVENT_BUS_BY_CATEGORY = new HashMap<>();
    private static final EventBus EVENT_BUS = new EventBus(null, "default");

    public static synchronized EventBus getIntance() {
        logger.log(Level.FINE, "getInstance on default EventBus.");
        return EVENT_BUS;
    }

    public static synchronized EventBus getIntance(String category) {
        if (category == null) {
            throw new IllegalArgumentException("category == null");
        }

        EventBus eventBus = EVENT_BUS_BY_CATEGORY.get(category);
        if (eventBus == null) {
            EVENT_BUS_BY_CATEGORY.put(category, eventBus = new EventBus(null, category));
            logger.log(Level.FINE, "getInstance creates new Create EventBus. category={0}", category);
        } else {
            logger.log(Level.FINE, "getInstance reuses existing EventBus. category={0}", category);
        }
        return eventBus;
    }

    public static synchronized EventBus getInstance(final String category, final String context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        if (category == null) {
            throw new IllegalArgumentException("category == null");
        }

        Map<String, EventBus> eventBusByCategory = EVENT_BUS_BY_CATEGORY_CONTEXT.get(category);
        if (eventBusByCategory == null) {
            EVENT_BUS_BY_CATEGORY_CONTEXT.put(category, eventBusByCategory = new HashMap<>());
        }
        EventBus eventBus = eventBusByCategory.get(context);
        if (eventBus == null) {
            eventBusByCategory.put(category, eventBus = new EventBus(category, context));
            logger.log(Level.FINE, "getInstance creates new EventBus. category={0}, context={1}", new Object[]{category, context});
        } else {
            logger.log(Level.FINE, "getInstance reuses existing EventBus. category={0}, context={1}", new Object[]{category, context});
        }

        return eventBus;
    }

    EventBus(final String category, final String context) {
        /* Non visible constructor. */

        logger.log(Level.INFO, "Constructor. category={0}, name={1}", new Object[]{category, context});
        this.category = category;
        this.context = context;

        /* Popula os listeners registrados no layer.xml. */
        final Set<EventListener> usedListener = new HashSet<>();
        final Lookup busLookup = Lookups.forPath(category == null ? "Safe" : "Safe/" + category);
        final Collection<? extends EventListener> busListeners = busLookup.lookupAll(EventListener.class);
        for (EventListener listener : busListeners) {
            logger.log(Level.FINE, "New EventBus. Register global listener. listener={0}", listener);
            globalListeners.add(listener);
            usedListener.add(listener);
        }
        final Lookup edtBusLookup = Lookups.forPath(category == null ? "EDT" : "EDT/" + category);
        final Collection<? extends EventListener> edtBusListeners = edtBusLookup.lookupAll(EventListener.class);
        for (EventListener listener : edtBusListeners) {
            logger.log(Level.FINE, "New EventBus. Register global EDT listener. listener={0}", listener);
            edtGlobalListeners.add(listener);
            usedListener.add(listener);
        }
        
        /** Support legacy registrarion via xml. */
        final Lookup edtBusLookup2 = Lookup.getDefault();
        final Collection<? extends EventListener> edtBusListeners2 = edtBusLookup2.lookupAll(EventListener.class);
        for (EventListener listener : edtBusListeners2) {
            if (usedListener.contains(listener)) {
                continue;
            }
            logger.log(Level.FINE, "New EventBus. Register global EDT listener. listener={0}", listener);
            edtGlobalListeners.add(listener);
        }
    }

    public final String getDisplayName() {
        return this.context + this.category;
    }

    public final void register(final EventListener listener) {
        edtRegister(listener);
    }
    
    public final void safeRegister(final EventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }

        boolean added;
        lockListener.lock();
        try {
            added = localListeners.add(listener);
            if (added) {
                localListenersList = null;
            }
        } finally {
            lockListener.unlock();
        }

        if (added) {
            logger.log(Level.FINE, "Register local listener. bus={0}, listener={1}", new Object[]{this.getDisplayName(), listener});
        } else {
            logger.log(Level.WARNING, "Register local listener: already registered! bus={0}, listener={1}", new Object[]{this.getDisplayName(), listener});
        }
    }

    public final void edtRegister(final EventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }

        boolean added;
        lockListener.lock();
        try {
            added = edtLocalListeners.add(listener);
            if (added) {
                edtLocalListenersList = null;
            }
        } finally {
            lockListener.unlock();
        }

        if (added) {
            logger.log(Level.FINE, "Register local listener. bus={0}, listener={1}", new Object[]{this.getDisplayName(), listener});
        } else {
            logger.log(Level.WARNING, "Register local listener: already registered! bus={0}, listener={1}", new Object[]{this.getDisplayName(), listener});
        }
    }

    public final void unregister(final EventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener == null");
        }

        boolean removed;
        lockListener.lock();
        try {
            removed = localListeners.remove(listener) || edtLocalListeners.remove(listener);
            if (removed) {
                localListenersList = null;
                edtLocalListenersList = null;
            }
        } finally {
            lockListener.unlock();
        }

        if (removed) {
            logger.log(Level.FINE, "Unregister local listener. bus={0}, listener={1}", new Object[]{this.getDisplayName(), listener});
        } else {
            logger.log(Level.WARNING, "Unregister local listener: not yet registered! bus={0}, listener={1}", new Object[]{this.getDisplayName(), listener});
        }
    }

    @Deprecated
    public final void disparar(final Event<?> event) {
        dispatch(event);
    }

    public final void dispatch(final Event<?> event) {
        logger.log(Level.FINE, "Dispatch event. bus={0}, event={1}", new Object[]{this.getDisplayName(), event});

        lockListener.lock();
        try {
            if (localListenersList == null) {
                localListenersList = new ArrayList<>(localListeners);
            }
        } finally {
            lockListener.unlock();
        }

        callListeners(globalListeners, event);
        callListeners(localListeners, event);

        edtEventQueue.add(event);

        if (!edtEventQueue.isEmpty()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    lockListener.lock();
                    try {
                        if (edtLocalListenersList == null) {
                            edtLocalListenersList = new ArrayList<>(edtLocalListeners);
                        }
                    } finally {
                        lockListener.unlock();
                    }

                    callListeners(edtGlobalListeners, event);
                    callListeners(edtLocalListenersList, event);
                }
            });
        }
    }

    protected static void callListeners(Collection<EventListener> listenersCollection, final Event<?> event) {
        for (EventListener listener : listenersCollection) {
            try {
                if (event.isCompatible(listener)) {
                    logger.log(Level.FINE, "Event listener. listener={0}, event={1}", new Object[]{listener, event});
                    event.executarCaller(listener);
                }
            } catch (Exception | Error e) {
                LogRecord record = new LogRecord(Level.SEVERE, "Event listener failed. listener={0}, event={1}");
                record.setParameters(new Object[]{listener, event});
                record.setThrown(e);
                logger.log(record);
            }
        }
    }

    @Override
    public String toString() {
        return "EventBus{" + this.getDisplayName() + '}';
    }

}
