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
class Fila implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Fila.class.getName());
    private final Queue<Mensagem<?>> mensagens = new ConcurrentLinkedQueue<Mensagem<?>>();
    private final List<BarramentoListener> listeners = new ArrayList<BarramentoListener>();
    private final List<BarramentoListener> novosListeners = new ArrayList<BarramentoListener>();

    Fila() {
        /* Proibe instâncias fora do package. */
    }

    final synchronized void adicionarListener(final BarramentoListener listener) {
        novosListeners.add(listener);
    }

    final synchronized void removerListener(final BarramentoListener listener) {
        novosListeners.remove(listener);
    }

    final void adicionarMensagem(final Mensagem<?> mensagem) {
        mensagens.add(mensagem);
    }

    @Override
    public void run() {
        logger.info("Iniciar BarramentoRunnable.");
        if (logger.isDebugEnabled()) {
            logger.debug("Mensagens: #consumidas={}, #restantes={}.", 0, mensagens.size());
        }

        synchronized (this) {
            if (logger.isDebugEnabled()) {
                logger.debug("Listeners: #existentes={}, #novos={}.", listeners.size(), novosListeners.size());
            }
            listeners.addAll(novosListeners);
            novosListeners.clear();
        }

        int numeroMensagensTratadas = 0;
        Mensagem<?> mensagem = null;
        while ((mensagem = mensagens.poll()) != null) {

            numeroMensagensTratadas++;

            logger.info("Tratar mensagem. mensagem={}", mensagem);
            if (logger.isDebugEnabled()) {
                logger.debug("Mensagens: #consumidas={}, #restantes={}.", numeroMensagensTratadas, mensagens.size());
            }

            final Iterator<? extends BarramentoListener> iterator = listeners.iterator();
            while (iterator.hasNext()) {
                final BarramentoListener listener = iterator.next();
                if (listener == null) {
                    iterator.remove();
                    continue;
                }
                try {
                    if (mensagem.isCompativel(listener)) {
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
