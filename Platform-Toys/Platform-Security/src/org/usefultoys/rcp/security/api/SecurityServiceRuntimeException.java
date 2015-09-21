package org.usefultoys.rcp.security.api;

/**
 * Security service failure.
 */
public class SecurityServiceRuntimeException extends RuntimeException {

    public SecurityServiceRuntimeException(final String message) {
        super(message);
    }

    public SecurityServiceRuntimeException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
}
