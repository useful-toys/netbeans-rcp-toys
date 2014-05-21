package br.com.danielferber.rcp.securitytoys.api;

/**
 * Descreve uma recusa da API de segurança.
 */
public class SegurancaRuntimeException extends RuntimeException {

    public SegurancaRuntimeException(final String message) {
        super(message);
    }

    public SegurancaRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
}
