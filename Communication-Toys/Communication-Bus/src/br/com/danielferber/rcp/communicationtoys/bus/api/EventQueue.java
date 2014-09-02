/*
 */
package br.com.danielferber.rcp.communicationtoys.bus.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fila de mensagens do barramento, com os respectivos listeners que consomem as
 * mensagens.
 *
 * @author x7ws - Daniel Felix Ferber
 */
class EventQueue implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(EventQueue.class.getName());
    private final Queue<Event<?>> mensagens = new ConcurrentLinkedQueue<Event<?>>();
    private final List<EventListener> listeners = new ArrayList<EventListener>();

    EventQueue() {
        /* Proibe instâncias fora do package. */
    }

    final synchronized void adicionarListener(final EventListener listener) {
        listeners.add(listener);
    }

    final synchronized void removerListener(final EventListener listener) {
        listeners.remove(listener);
    }

    final void adicionarMensagem(final Event<?> mensagem) {
        mensagens.add(mensagem);
    }

    @Override
    public void run() {
        logger.info("Iniciar BarramentoRunnable.");
        if (logger.isDebugEnabled()) {
            logger.debug("Mensagens: #consumidas={}, #restantes={}.", 0, mensagens.size());
        }

        final List<EventListener> listeners;
        synchronized (this) {
            listeners = new ArrayList<EventListener>(this.listeners);
        }

        int numeroMensagensTratadas = 0;
        Event<?> mensagem = null;
        while ((mensagem = mensagens.poll()) != null) {

            numeroMensagensTratadas++;

            logger.info("Tratar mensagem. mensagem={}", mensagem);
            if (logger.isDebugEnabled()) {
                logger.debug("Mensagens: #consumidas={}, #restantes={}.", numeroMensagensTratadas, mensagens.size());
            }

            final Iterator<? extends EventListener> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                final EventListener listener = iterator.next();
                if (listener == null) {
                    iterator.remove();
                    continue;
                }
                try {
                    if (mensagem.isCompatible(listener)) {
                        logger.debug("Executar mensagem. listener={}", listener);
                        mensagem.executarCaller(listener);
                    }
                } catch (Exception e) {
                    logger.error("Falha ao executar mensagem no listener. mensagem={}, listener={}", mensagem, listener, e);
                }
            }
        }

        logger.info("Finalizar BarramentoRunnable.");
    }
}
