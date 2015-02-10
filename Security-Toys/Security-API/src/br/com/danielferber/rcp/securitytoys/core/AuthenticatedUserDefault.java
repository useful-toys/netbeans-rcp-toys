package br.com.danielferber.rcp.securitytoys.core;

import br.com.danielferber.rcp.securitytoys.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.api.AuthorizationException;
import br.com.danielferber.rcp.securitytoys.api.SecurityService;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 *
 * @author Daniel
 */
public class AuthenticatedUserDefault implements AuthenticatedUser {

    private final String login;
    private final String nome;
    private final Set<String> perfis;

    public AuthenticatedUserDefault(String login, String nome, Set<String> perfis) {
        this.login = login;
        this.nome = nome;
        this.perfis = Collections.unmodifiableSet(perfis);
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getName() {
        return nome;
    }

    @Override
    public Set<String> getPerfis() {
        return perfis;
    }

    protected AuthorizationException checkPermission(String resource) {
        if (!this.perfis.contains(resource)) {
            return new AuthorizationException.NotAuthorized(this, resource);
        }
        return null;
    }

    protected AuthorizationException checkPermission(Collection<String> resources) {
        if (! Collections.disjoint(perfis, resources)) {
            return new AuthorizationException.NotAuthorized(this, resources.iterator().next());
        }
        return null;
    }

    @Override
    public final boolean isResourceGranted(final String resource) {
        final AuthorizationException motivo = AuthenticatedUserDefault.this.checkPermission(resource);
        if (motivo == null) {
            SecurityService.LOGGER.debug("Permissão concedida. login={}; recurso={}", getLogin(), resource);
        } else {
            SecurityService.LOGGER.debug("Permissão recusada. login={}; recurso={}; motivo={}", getLogin(), resource, motivo);
        }
        return motivo == null;
    }

    @Override
    public final boolean isAnyResourceGranted(Collection<String> resources) {
        final AuthorizationException motivo = checkPermission(resources);
        if (motivo == null) {
            SecurityService.LOGGER.debug("Permissão concedida. login={}; recurso={}", getLogin(), resources);
        } else {
            SecurityService.LOGGER.debug("Permissão recusada. login={}; recurso={}; motivo={}", getLogin(), resources, motivo);
        }
        return motivo == null;
    }

    @Override
    public boolean isAnyResourceGranted(String... resources) {
        final AuthorizationException motivo = checkPermission(Arrays.asList(resources));
        if (motivo == null) {
            SecurityService.LOGGER.debug("Permissão concedida. login={}; recurso={}", getLogin(), resources);
        } else {
            SecurityService.LOGGER.debug("Permissão recusada. login={}; recurso={}; motivo={}", getLogin(), resources, motivo);
        }
        return motivo == null;
    }

    @Override
    public final void resourceGranted(final String recurso) throws AuthorizationException {
        final AuthorizationException motivo = AuthenticatedUserDefault.this.checkPermission(recurso);
        if (motivo == null) {
            SecurityService.LOGGER.debug("Permissão concedida. login={}; recurso={}", getLogin(), recurso);
        } else {
            SecurityService.LOGGER.debug("Permissão recusada. login={}; recurso={}; motivo={}", getLogin(), recurso, motivo);
        }
        if (motivo != null) {
            throw motivo;
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{login=" + this.getLogin() + '}';
    }
}
