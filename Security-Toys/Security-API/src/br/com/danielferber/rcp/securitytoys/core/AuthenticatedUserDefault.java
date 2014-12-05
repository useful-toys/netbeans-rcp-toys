/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.core;

import br.com.danielferber.rcp.securitytoys.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.api.AuthorizationException;
import br.com.danielferber.rcp.securitytoys.api.SecurityService;
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

    protected AuthorizationException avaliaPermissao(String nomeRecurso) {
        if (! this.perfis.contains(nomeRecurso)) {
            return new AuthorizationException.NotAuthorized(this, nomeRecurso);
        }
        return null;
    }

    @Override
    public final boolean isResourceGranted(final String recurso) {
        final AuthorizationException motivo = avaliaPermissao(recurso);
        if (motivo == null) {
            SecurityService.LOGGER.debug("Permissão concedida. login={}; recurso={}", getLogin(), recurso);
        } else {
            SecurityService.LOGGER.debug("Permissão recusada. login={}; recurso={}; motivo={}", getLogin(), recurso, motivo);
        }
        return motivo == null;
    }

    @Override
    public final void resourceGranted(final String recurso) throws AuthorizationException {
        final AuthorizationException motivo = avaliaPermissao(recurso);
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
