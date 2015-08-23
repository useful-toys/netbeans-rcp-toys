package br.com.danielferber.rcp.securitytoys.security.api;

import org.openide.util.NbBundle;

/**
 * Descreve a recusa de acessar a um recurso ou uma funcionalidade.
 */
@NbBundle.Messages({
    "AuthorizationException_NotAuthenticated=User not authenticated.",
    "AuthorizationException_NotAuthorized=User not authorized."
})
public class AuthorizationException extends Exception {

    private final String resourceName;
    private final AuthenticatedUser authenticatedUser;

    protected AuthorizationException(final String message, final AuthenticatedUser authenticatedUser, final String resourceName) {
        super(message);
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
            super(Bundle.AuthorizationException_NotAuthenticated(), authenticatedUser, resourceName);
        }
    }

    /**
     * Authorization refused as the resource has not been granted to the user.
     */
    public static class NotAuthorized extends AuthorizationException {

        public NotAuthorized(AuthenticatedUser authenticatedUser, String resourceName) {
            super(Bundle.AuthorizationException_NotAuthorized(), authenticatedUser, resourceName);
        }
    }
}
