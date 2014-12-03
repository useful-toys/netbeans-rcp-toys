package br.com.danielferber.rcp.securitytoys.api;

/**
 * Descreve a recusa de acessar a um recurso ou uma funcionalidade.
 */
public class AuthorizationException extends Exception {

    private final String resourceName;
    private final AuthenticatedUser authenticatedUser;

    public AuthorizationException(final AuthenticatedUser authenticatedUser, final String resourceName) {
        super("Authorization refused.");
        this.resourceName = resourceName;
        this.authenticatedUser = authenticatedUser;
    }

    /**
     * @return The resource name that was not granted.
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @return The authenticated user, if available.
     */
    public AuthenticatedUser getAuthenticatedUser() {
        return authenticatedUser;
    }

    /**
     * Authorization refused as the user has not yet been authenticated.
     */
    public static class NotAuthenticated extends AuthorizationException {

        public NotAuthenticated(AuthenticatedUser authenticatedUser, String resourceName) {
            super(authenticatedUser, resourceName);
        }
    }

    /**
     * Authorization refused as the resource has not been granted to the user.
     */
    public static class NotAuthorized extends AuthorizationException {

        public NotAuthorized(AuthenticatedUser authenticatedUser, String resourceName) {
            super(authenticatedUser, resourceName);
        }
    }
}
