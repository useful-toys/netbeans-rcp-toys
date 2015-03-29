package br.com.danielferber.rcp.securitytoys.api;

/**
 * The authentication credential were refused.
 */
public class AuthenticationException extends Exception {

    private final String login;

    public AuthenticationException(final String message, final String login) {
        super(message);
        this.login = login;
    }

    public AuthenticationException(final String message, final Exception e) {
        super(message, e);
        this.login = null;
    }

    /**
     * @return String used as user identifier when logging in. <code>null</code> if not applicable.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Incorrect login or password.
     */
    public static class IncorrectCredentials extends AuthenticationException {

        /**
         * Constructor.
         *
         * @param login String used as user identifier when trying to log in.
         */
        public IncorrectCredentials(String login) {
            super("Incorrect credentials", login);
        }
    }

    /**
     * User is not active.
     */
    public static class InactiveUser extends AuthenticationException {

        /**
         * Constructor.
         *
         * @param login String used as user identifier when trying to log in.
         */
        public InactiveUser(String login) {
            super("User is not active.", login);
        }
    }

    /**
     * User does not exist.
     */
    public static class InexistingUser extends AuthenticationException {

        /**
         * Constructor.
         *
         * @param login String used as user identifier when trying to log in.
         */
        public InexistingUser(String login) {
            super("User does not exist.", login);
        }
    }

    /**
     * Authentication service is not available.
     */
    public static class UnavailableService extends AuthenticationException {

        /**
         * Constructor.
         *
         * @param e exception that describes unavailability.
         */
        public UnavailableService(Exception e) {
            super("Authentication service unavailable.", e);
        }
    }
}
