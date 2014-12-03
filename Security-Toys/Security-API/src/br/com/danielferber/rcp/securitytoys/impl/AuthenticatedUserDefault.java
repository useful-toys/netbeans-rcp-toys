/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.impl;

import br.com.danielferber.rcp.securitytoys.api.AutorizacaoException;
import br.com.danielferber.rcp.securitytoys.api.SecurityService;
import br.com.danielferber.rcp.securitytoys.api.AuthenticatedUser;
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
    public String getNome() {
        return nome;
    }

    @Override
    public Set<String> getPerfis() {
        return perfis;
    }

    protected AutorizacaoException avaliaPermissao(String nomeRecurso) {
        if (! this.perfis.contains(nomeRecurso)) {
            return new AutorizacaoException.NaoAutorizado(this, nomeRecurso);
        }
        return null;
    }

    @Override
    public final boolean temPermissao(final String recurso) {
        final AutorizacaoException motivo = avaliaPermissao(recurso);
        if (motivo == null) {
            SecurityService.LOGGER.debug("Permissão concedida. login={}; recurso={}", getLogin(), recurso);
        } else {
            SecurityService.LOGGER.debug("Permissão recusada. login={}; recurso={}; motivo={}", getLogin(), recurso, motivo);
        }
        return motivo == null;
    }

    @Override
    public final void garantePermissao(final String recurso) throws AutorizacaoException {
        final AutorizacaoException motivo = avaliaPermissao(recurso);
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
