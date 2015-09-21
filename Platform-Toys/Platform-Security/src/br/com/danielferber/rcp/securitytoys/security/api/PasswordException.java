package br.com.danielferber.rcp.securitytoys.security.api;

/**
 * The password was refused.
 */
public class PasswordException extends Exception {

    protected PasswordException(final String message) {
        super(message);
    }

    protected PasswordException(final String message, final Exception e) {
        super(message, e);
    }

    public static class IncorrectCredentials extends PasswordException {

        public IncorrectCredentials() {
            super("Incorrect credentials");
        }
    }

    public static class TooWeak extends PasswordException {

        public TooWeak() {
            super("Password too weak");
        }
    }

    public static class NotAllowed extends PasswordException {

        public NotAllowed() {
            super("Password change not allowed");
        }
    }
}
