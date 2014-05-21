package br.com.danielferber.rcp.securitytoys.api;

/**
 * Descreve a recusa de acessar a um recurso ou uma funcionalidade.
 */
public class AutorizacaoException extends Exception {

    private final String nomeRecurso;
    private final UsuarioAutenticado usuario;

    public AutorizacaoException(final UsuarioAutenticado usuario, final String nomeRecurso) {
        super("Autorização recusada.");
        this.nomeRecurso = nomeRecurso;
        this.usuario = usuario;
    }

    /**
     * @return O recurso ou a funcionalidade cuja autorização foi recusada.
     */
    public String getNomeRecurso() {
        return nomeRecurso;
    }

    /**
     * @return O usuário para o qual a autorização foi recusada.
     */
    public UsuarioAutenticado getUsuario() {
        return usuario;
    }

    /**
     * O usuário ainda não foi autenticado.
     */
    public static class NaoAutenticado extends AutorizacaoException {

        public NaoAutenticado(UsuarioAutenticado usuario, String nomeRecurso) {
            super(usuario, nomeRecurso);
        }
    }

    /**
     * O usuário não possui autorização para acessar um recurso ou uma
     * funcionalidade.
     */
    public static class NaoAutorizado extends AutorizacaoException {

        public NaoAutorizado(UsuarioAutenticado usuario, String nomeRecurso) {
            super(usuario, nomeRecurso);
        }
    }
}
