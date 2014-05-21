package br.com.danielferber.rcp.securitytoys.api;

/**
 * Descreve a recusa de uma autenticação.
 */
public class AutenticacaoException extends Exception {

    private final String login;

    public AutenticacaoException(final String login) {
        super("Autenticação recusada.");
        this.login = login;
    }

    public static class CredenciaisIncorretas extends AutenticacaoException {

        public CredenciaisIncorretas(String login) {
            super(login);
        }
    }

    public static class UsuarioInativo extends AutenticacaoException {

        public UsuarioInativo(String login) {
            super(login);
        }
    }
    
    public static class ServicoIndisponivel extends AutenticacaoException {

        public ServicoIndisponivel(String login) {
            super(login);
        }
    }
}
