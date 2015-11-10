/*
 */
package org.usefultoys.netbeansrcp.platform.bus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 * Fila de mensagens do barramento, com os respectivos listeners que consomem as
 * mensagens.
 *
 * @author Daniel Felix Ferber
 */
class EventQueue implements Runnable {

    private static final Logger logger = Logger.getLogger(EventQueue.class.getName());

    EventQueue() {
        /* Proibe instâncias fora do package. */
    }

    final synchronized void adicionarListener(final EventListener listener) {
        localListeners.add(listener);
    }

    final synchronized void removerListener(final EventListener listener) {
        localListeners.remove(listener);
    }

    final void adicionarMensagem(final Event<?> mensagem) {
        mensagens.add(mensagem);
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Runnable started. threadId={0}, threadName={1}", 
                new Object[]{Thread.currentThread().getId(), Thread.currentThread().getName()});

        final List<EventListener> listeners;
        synchronized (this) {
            /* Iterate over a copy of listener list, to prevent concurrency on list access. */
            listeners = new ArrayList<EventListener>(this.localListeners);
        }

        int numeroMensagensTratadas = 0;
        Event<?> event;
        while ((event = mensagens.poll()) != null) {

            numeroMensagensTratadas++;

            if (logger.isLoggable(Level.FINE) ){
                logger.log(Level.FINE, "Handle event. #events={0}, #handled={1}, event={2}", new Object[]{mensagens.size(), numeroMensagensTratadas, numeroMensagensTratadas});
            } else {
                logger.log(Level.INFO, "Handle event. event={0}", event);
            }

            final Iterator<? extends EventListener> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                final EventListener listener = iterator.next();
                if (listener == null) {
                    iterator.remove();
                    continue;
                }
                try {
                    if (event.isCompatible(listener)) {
                        logger.log(Level.FINE, "Call event listener. listener={}", listener);
                        event.executarCaller(listener);
                    }
                } catch (Exception e) {
                    LogRecord record = new LogRecord(Level.SEVERE, "Event listener failed. event={}, listener={}");
                    record.setParameters(new Object[]{event, listener});
                    record.setThrown(e);
                    logger.log(record);
                }
            }
        }

        logger.info("Runnable completed.");
    }
}
