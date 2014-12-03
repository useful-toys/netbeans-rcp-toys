package br.com.danielferber.rcp.securitytoys.api;

/**
 * Security service failured.
 */
public class SecurityServiceRuntimeException extends RuntimeException {

    public SecurityServiceRuntimeException(final String message) {
        super(message);
    }

    public SecurityServiceRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
}
