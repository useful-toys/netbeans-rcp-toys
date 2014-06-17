/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.communicationtoys.bus.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @author X7WS
 */
public class Barramento {

    private static final Logger logger = LoggerFactory.getLogger(Barramento.class);
    private final Fila fila = new Fila();
    private final ReentrantLock semaforo = new ReentrantLock();

    Barramento(final String classificador) {
        /* Proibe instâncias fora do package. */

        logger.info("Criar barramento. classificador={}", classificador);

        /* Popula os listeners registrados no layer.xml. */
        if (classificador != null) {
            final Lookup lookup = Lookups.forPath("Barramento/" + classificador);
            final Collection<? extends BarramentoListener> listeners = lookup.lookupAll(BarramentoListener.class);
            logger.debug("Classificador informado. Registrar listeners globais. #listeners={}", listeners.size());
            for (BarramentoListener listener : listeners) {
                logger.debug("Registrar listener global. listener={}", listener);
                fila.adicionarListener(listener);
            }

            /* TODO: precisa registrar um listener no layer.xml para capturar novos listeners de módulos
             * adicionados após a criação do barramento. */
        } else {
            logger.debug("Classificador não informado. Não haverá listeners globais.");
        }
    }

    public final void register(final BarramentoListener listener) {
        logger.debug("Registrar listener. barramento={}, listener={}", this, listener);
        fila.adicionarListener(listener);
    }

    public final void unregister(final BarramentoListener listener) {
        logger.debug("Desregistrar listener. barramento={}, listener={}", this, listener);
        fila.removerListener(listener);
    }

    public final void disparar(final Mensagem<?> mensagem) {
        logger.debug("Disparar mensagem. barramento={}, mensagem={}", this, mensagem);

        fila.adicionarMensagem(mensagem);

        if (semaforo.tryLock()) {
            final int holdCount = semaforo.getHoldCount();
            if (holdCount > 1) {
                /* já está executando. */
                semaforo.unlock();
                return;
            }
            try {
                if (!SwingUtilities.isEventDispatchThread()) {
                    try {
                        SwingUtilities.invokeAndWait(fila);
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (InvocationTargetException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                } else {
                    fila.run();
                }
            } finally {
                semaforo.unlock();
            }
        }
    }
}
