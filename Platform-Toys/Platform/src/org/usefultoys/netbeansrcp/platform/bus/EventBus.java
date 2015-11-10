/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.bus;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
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

    private final EventQueue queue = new EventQueue();
    private final ReentrantLock lock = new ReentrantLock();
    private final String displayName;

    private static final Map<Object, Map<String, EventBus>> EVENT_BUS_BY_CONTEXT_CATEGORY = new WeakHashMap<Object, Map<String, EventBus>>();
    private static final Map<String, EventBus> EVENT_BUS_BY_CATEGORY = new HashMap<String, EventBus>();
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

    public static synchronized EventBus getInstance(final Object context, final String category) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        if (category == null) {
            throw new IllegalArgumentException("category == null");
        }

        Map<String, EventBus> eventBusByCategory = EVENT_BUS_BY_CONTEXT_CATEGORY.get(context);
        if (eventBusByCategory == null) {
            EVENT_BUS_BY_CONTEXT_CATEGORY.put(context, eventBusByCategory = new HashMap<String, EventBus>());
        }
        EventBus eventBus = eventBusByCategory.get(category);
        if (eventBus == null) {
            eventBusByCategory.put(category, eventBus = new EventBus(context, category));
            logger.log(Level.FINE, "getInstance creates new EventBus. context={0}, category={1}", new Object[]{context, category});
        } else {
            logger.log(Level.FINE, "getInstance reuses existing EventBus. context={0}, category={1}", new Object[]{context, category});
        }

        return eventBus;
    }

    EventBus(final Object context, final String category) {
        /* Non visible constructor. */

        this.displayName = category + (context == null ? "" : "/" + context.toString());
        logger.log(Level.INFO, "Constructor. category={0}, name={1}", new Object[]{category, displayName});

        if (category != null) {
            /* Popula os listeners registrados no layer.xml. */
            final Lookup lookup = Lookups.forPath("EventBus/" + category);
            final Collection<? extends EventListener> listeners = lookup.lookupAll(EventListener.class);
            for (EventListener listener : listeners) {
                logger.log(Level.FINE, "New EventBus. Register global listener. listener={0}", listener);
                queue.adicionarListener(listener);
            }
        } else {
            logger.log(Level.FINE, "New EventBus. Null category implies no global listeners.");
        }
    }

    public final void register(final EventListener listener) {
        logger.log(Level.FINE, "Register local listener. bus={0}, listener={1}", new Object[]{this, listener});
        queue.adicionarListener(listener);
    }

    public final void unregister(final EventListener listener) {
        logger.log(Level.FINE, "Unregister local listener. bus={0}, listener={1}", new Object[]{this, listener});
        queue.removerListener(listener);
    }

    public final void disparar(final Event<?> event) {
        logger.log(Level.FINE, "Dispatch event. bus={0}, event={1}", new Object[]{this, event});

        queue.adicionarMensagem(event);

        if (lock.tryLock()) {
            final int holdCount = lock.getHoldCount();
            if (holdCount > 1) {
                /* já está executando. */
                lock.unlock();
                return;
            }
            try {
                if (!SwingUtilities.isEventDispatchThread()) {
                    try {
                        SwingUtilities.invokeAndWait(queue);
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (InvocationTargetException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } else {
                    queue.run();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public String toString() {
        return displayName;
    }

}
