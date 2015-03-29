package br.com.danielferber.rcp.securitytoys.api;

import br.com.danielferber.rcp.securitytoys.api.AuthenticationException.InactiveUser;
import br.com.danielferber.rcp.securitytoys.api.AuthenticationException.IncorrectCredentials;
import br.com.danielferber.rcp.securitytoys.api.AuthenticationException.InexistingUser;
import br.com.danielferber.rcp.securitytoys.api.AuthenticationException.UnavailableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides entry points to security related services.
 * <p>
 * The service keeps the currently authenticated user as a global state.
 * <p>
 * Call {@code SecurityService.Lookup.getDefault()} to get the global instance of this service.
 * <p>
 * Changes to this state are broadcasted to the authentication listeners. Interested modules my supply a
 * {@link AuthenticationListener} implementation annotated with
 * {@code @ServiceProvider(service = AuthenticationListener.class)} in order to receive authentication events. Or they
 * may programmatically supply a listener by calling {@link #addListener(br.com.danielferber.rcp.securitytoys.api.AuthenticationListener)
 * }.
 * <p>
 * The service resorts to an application specific {@link AuthenticationProvider} implementation, supplied by a module
 * and annotated with {@code @ServiceProvider(service = AuthenticationProvider.class)}.
 * <br>
 * The underlying implementation may resort to remote service that may be unavailable during login attempt. The service
 * reports this state via {@link #isServiceAvailable()} and {@link UnavailableService} exception.
 *
 * @author Daniel Felix Ferber
 */
public interface SecurityService {

    public static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);

    /**
     * If the underlying service implementation is available.
     *
     * @return true if the service implementation is available; false otherwise.
     */
    boolean isServiceAvailable();

    /**
     * The currently authenticated user.
     *
     * @return The currently authenticated user; null otherwise.
     */
    AuthenticatedUser getCurrentAuthenticatedUser();

    /**
     * The exception from the last login attempt.
     *
     * @return The exception from the last login attempt; null if the last attempt was successful.
     */
    Exception getLastLoginException();

    /**
     * Notify a login attempt. Resorts to the underlying implementation the validate user credentials. If successful,
     * the login turns into the current authenticated user. An event is broadcasted to the authentication listeners.
     * Future calls to {@link #getCurrentAuthenticatedUser() } will return an object that describes this newly
     * authenticated user. If unsuccessful, nothing changes and a proper exception is thrown.
     *
     * @param login String used as user identifier when logging in.
     * @param password User credentials.
     * @return The new authenticated user, if login was accepted and successful.
     * @throws IncorrectCredentials Incorrect login or password.
     * @throws UnavailableService Underlying service implementation is not available.
     * @throws InactiveUser User is not active.
     * @throws InexistingUser User does not exist.
     */
    AuthenticatedUser login(String login, char[] password) throws AuthenticationException.IncorrectCredentials, AuthenticationException.UnavailableService, AuthenticationException.InactiveUser, AuthenticationException.InexistingUser;

    /**
     * Nofify that the current user is not logged in anymore. Future calls to {@link #getCurrentAuthenticatedUser() }
     * will return null.
     */
    void logoff();

    /**
     * Add a authentication event listener. Interested parts may get broadcasted authentication by supplying a listener
     * programatically. As an alternative, interested modules my supply a {@link AuthenticationListener} implementation
     * annotated with {@code @ServiceProvider(service = AuthenticationListener.class)}.
     *
     * @param listener Listener to add.
     */
    void addListener(AuthenticationListener listener);

    /**
     * Add a authentication event listener. Interested parts may get broadcasted authentication events by registering
     *
     * @param listener Lister to remove.
     */
    void removeListener(AuthenticationListener listener);

    public static class Lookup {

        public static SecurityService getDefault() {
            final SecurityService instance = org.openide.util.Lookup.getDefault().lookup(SecurityService.class);
            if (instance == null) {
                throw new IllegalStateException("No SecurityService implementation.");
            }
            return instance;
        }
    }
}
