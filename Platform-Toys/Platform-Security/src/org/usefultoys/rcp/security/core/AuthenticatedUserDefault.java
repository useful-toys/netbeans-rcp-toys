package org.usefultoys.rcp.security.core;

import org.usefultoys.rcp.security.api.AuthenticatedUser;
import org.usefultoys.rcp.security.api.AuthorizationException;
import org.usefultoys.rcp.security.api.SecurityService;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

/**
 * A default implementation for {@link AuthenticatedUser}.
 *
 * @author Daniel Felix Ferber
 */
public class AuthenticatedUserDefault implements AuthenticatedUser {

    private final String login;
    private final String name;
    private final Set<String> resources;

    public AuthenticatedUserDefault(String login, String name, Set<String> resources) {
        this.login = login;
        this.name = name;
        this.resources = Collections.unmodifiableSet(new TreeSet<String>(resources));
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<String> getResources() {
        return resources;
    }

    protected boolean checkAndLogAny(Collection<String> resourceNames) {
        final boolean granted = !Collections.disjoint(resources, resourceNames);
        if (granted) {
            SecurityService.LOGGER.log(Level.FINE, "Resource granted. login={0}; query={1}", new Object[] {getLogin(), resourceNames});
        } else {
            SecurityService.LOGGER.log(Level.FINE, "Resource denied. login={0}; query={1}", new Object[] {getLogin(), resourceNames});
        }
        return granted;
    }

    private boolean checkAndLog(final String resourceName) {
        final boolean granted = this.resources.contains(resourceName);
        if (granted) {
            SecurityService.LOGGER.log(Level.FINE, "Resource granted. login={0}; query={1}", new Object[] {getLogin(), resourceName});
        } else {
            SecurityService.LOGGER.log(Level.FINE, "Resource denied. login={0}; query={1}", new Object[] {getLogin(), resourceName});
        }
        return granted;
    }

    @Override
    public final boolean isResourceGranted(final String resourceName) {
        return AuthenticatedUserDefault.this.checkAndLog(resourceName);
    }

    @Override
    public final void resourceGranted(final String resourceName) throws AuthorizationException {
        if (!AuthenticatedUserDefault.this.checkAndLog(resourceName)) {
            throw new AuthorizationException.NotAuthorized(this, resourceName);
        }
    }

    @Override
    public final boolean isAnyResourceGranted(Collection<String> resourceNames) {
        return checkAndLogAny(resourceNames);
    }

    @Override
    public boolean isAnyResourceGranted(String... resourceNames) {
        return checkAndLogAny(Arrays.asList(resourceNames));
    }
    
    @Override
    public String toString() {
        return "AuthenticatedUserDefault{" + "login=" + login + ", resources=" + resources + '}';
    }
}
