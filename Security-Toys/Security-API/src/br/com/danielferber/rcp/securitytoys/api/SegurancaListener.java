package br.com.danielferber.rcp.securitytoys.api;

public interface SegurancaListener {
    void notificarAutenticacao(UsuarioAutenticado usuario);
}
