package br.com.danielferber.rcp.securitytoys.api;

/**
 * Descreve a recusa de acessar a um recurso ou uma funcionalidade.
 */
public class AutorizacaoException extends Exception {

    private final String nomeRecurso;
    private final AuthenticatedUser usuario;

    public AutorizacaoException(final AuthenticatedUser usuario, final String nomeRecurso) {
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
    public AuthenticatedUser getUsuario() {
        return usuario;
    }

    /**
     * O usuário ainda não foi autenticado.
     */
    public static class NaoAutenticado extends AutorizacaoException {

        public NaoAutenticado(AuthenticatedUser usuario, String nomeRecurso) {
            super(usuario, nomeRecurso);
        }
    }

    /**
     * O usuário não possui autorização para acessar um recurso ou uma
     * funcionalidade.
     */
    public static class NaoAutorizado extends AutorizacaoException {

        public NaoAutorizado(AuthenticatedUser usuario, String nomeRecurso) {
            super(usuario, nomeRecurso);
        }
    }
}
