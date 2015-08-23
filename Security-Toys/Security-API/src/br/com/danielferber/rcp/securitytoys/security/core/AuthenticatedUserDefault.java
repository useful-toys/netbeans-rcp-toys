package br.com.danielferber.rcp.securitytoys.security.core;

import br.com.danielferber.rcp.securitytoys.security.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.security.api.AuthorizationException;
import br.com.danielferber.rcp.securitytoys.security.api.SecurityService;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

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
            SecurityService.LOGGER.debug("Resource granted. login={}; query={}", getLogin(), resourceNames);
        } else {
            SecurityService.LOGGER.debug("Resource denied. login={}; query={}", getLogin(), resourceNames);
        }
        return granted;
    }

    private boolean checkAndLog(final String resourceName) {
        final boolean granted = this.resources.contains(resourceName);
        if (granted) {
            SecurityService.LOGGER.debug("Resource granted. login={}; query={}", getLogin(), resourceName);
        } else {
            SecurityService.LOGGER.debug("Resource denied. login={}; query={}", getLogin(), resourceName);
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
