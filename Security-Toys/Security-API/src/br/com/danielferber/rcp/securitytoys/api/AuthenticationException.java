package br.com.danielferber.rcp.securitytoys.api;

/**
 * The credentials were no accepted for user authentication.
 */
public class AuthenticationException extends Exception {

    private final String login;

    public AuthenticationException(final String login) {
        super("Authentication refused.");
        this.login = login;
    }
    
    public AuthenticationException(final Exception e) {
        super("Authentication refused.", e);
        this.login = null;
    }

    public String getLogin() {
        return login;
    }
    
    public static class IncorrectCredentials extends AuthenticationException {

        public IncorrectCredentials(String login) {
            super(login);
        }
    }

    public static class InactiveUser extends AuthenticationException {

        public InactiveUser(String login) {
            super(login);
        }
    }

    public static class InexistingUser extends AuthenticationException {

        public InexistingUser(String login) {
            super(login);
        }
    }

    public static class UnavailableService extends AuthenticationException {

        public UnavailableService(Exception e) {
            super(e);
        }
    }
}
