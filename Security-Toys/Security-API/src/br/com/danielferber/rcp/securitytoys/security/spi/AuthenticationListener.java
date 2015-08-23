package br.com.danielferber.rcp.securitytoys.security.spi;

import br.com.danielferber.rcp.securitytoys.security.api.AuthenticatedUser;

public interface AuthenticationListener {
    void notificarAutenticacao(AuthenticatedUser usuario);
}
