package br.com.danielferber.rcp.securitytoys.security.api;

import org.openide.util.NbBundle;

/**
 * The authentication credential were refused.
 */
@NbBundle.Messages({
    "AuthenticationException_IncorrectCredentials=Incorrect credentials.",
    "AuthenticationException_InactiveUser=User is not active.",
    "AuthenticationException_InexistingUser=User does not exist.",
    "AuthenticationException_UnavailableService=Authentication service unavailable.",})
public class AuthenticationException extends Exception {

    private final String login;

    protected AuthenticationException(final String message, final String login) {
        super(message);
        this.login = login;
    }

    protected AuthenticationException(final String message, final Exception e) {
        super(message, e);
        this.login = null;
    }

    /**
     * @return String used as user identifier when logging in. null if not
     * applicable.
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
            super(Bundle.AuthenticationException_IncorrectCredentials(), login);
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
            super(Bundle.AuthenticationException_InactiveUser(), login);
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
            super(Bundle.AuthenticationException_InexistingUser(), login);
        }
    }

    /**
     * Underlying service implementation is not available.
     */
    public static class UnavailableService extends AuthenticationException {

        /**
         * Constructor.
         *
         * @param e exception that describes unavailability.
         */
        public UnavailableService(Exception e) {
            super(Bundle.AuthenticationException_UnavailableService(), e);
        }
    }
}
