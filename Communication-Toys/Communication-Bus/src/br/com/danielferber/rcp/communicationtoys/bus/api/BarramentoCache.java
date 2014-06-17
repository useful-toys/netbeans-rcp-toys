/*
 */
package br.com.danielferber.rcp.communicationtoys.bus.api;

import java.util.HashMap;
import java.util.WeakHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mantém uma cache dos barramentos.
 *
 * @author x7ws - Daniel Felix Ferber
 */
public final class BarramentoCache {

    private static final Logger logger = LoggerFactory.getLogger(BarramentoCache.class);

    private BarramentoCache() {
    }
    /**
     * Cache efêmera de singletons de Barramento.
     */
    private static final WeakHashMap<Object, HashMap<String, Barramento>> CACHE_BARRAMENTO = new WeakHashMap<Object, HashMap<String, Barramento>>();

    /**
     * Obtém o singleton de Barramento associado a um determinado objeto.
     */
    public static synchronized Barramento get(final Object contexto, String classificador) {
        if (contexto == null) {
            throw new IllegalArgumentException();
        }

        HashMap<String, Barramento> mapaBarramento = CACHE_BARRAMENTO.get(contexto);

        Barramento resultado;

        if (mapaBarramento == null) {
            mapaBarramento = new HashMap<String, Barramento>();
        }
        
        if (!mapaBarramento.containsKey(classificador)) {
            logger.debug("Criar barramento. contexto={}", contexto);
            resultado = new Barramento(classificador);
            mapaBarramento.put(classificador, resultado);
            CACHE_BARRAMENTO.put(contexto, mapaBarramento);
        } else {
            logger.debug("Reusar barramento. contexto={}", contexto);
            resultado = mapaBarramento.get(classificador);
        }

        return resultado;
    }
}
