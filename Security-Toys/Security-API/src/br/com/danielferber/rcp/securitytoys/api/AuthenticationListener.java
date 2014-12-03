package br.com.danielferber.rcp.securitytoys.api;

public interface AuthenticationListener {
    void notificarAutenticacao(AuthenticatedUser usuario);
}
