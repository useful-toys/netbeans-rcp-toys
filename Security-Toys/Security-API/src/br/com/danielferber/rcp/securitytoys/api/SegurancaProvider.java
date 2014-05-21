package br.com.danielferber.rcp.securitytoys.api;

public interface SegurancaProvider {
    UsuarioAutenticado login(String usuario, char[] senha) throws AutenticacaoException.CredenciaisIncorretas, AutenticacaoException.ServicoIndisponivel, AutenticacaoException.UsuarioInativo;
    void logoff();

    boolean isDisponivel();
}
