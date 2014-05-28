package br.com.danielferber.rcp.securitytoys.core;

import br.com.danielferber.rcp.securitytoys.api.AutenticacaoException;
import org.openide.util.lookup.ServiceProvider;
import br.com.danielferber.rcp.securitytoys.api.SegurancaListener;
import br.com.danielferber.rcp.securitytoys.api.SegurancaProvider;
import br.com.danielferber.rcp.securitytoys.api.SegurancaService;
import static br.com.danielferber.rcp.securitytoys.api.SegurancaService.LOGGER;
import br.com.danielferber.rcp.securitytoys.api.UsuarioAutenticado;
import java.util.Collection;
import org.openide.util.Lookup;

/**
 *
 * @author X7WS
 */
@ServiceProvider(service = SegurancaService.class)
public class SegurancaServicePadrao extends SegurancaService {

    protected UsuarioAutenticado usuarioAutenticado;
    protected AutenticacaoException lastLoginException;

    public SegurancaServicePadrao() {
        super();
        this.usuarioAutenticado = null;
    }

    @Override
    public UsuarioAutenticado getUsuario() {
        return usuarioAutenticado;
    }

    @Override
    public void logoff() {
        if (usuarioAutenticado == null) {
            LOGGER.warn("Inconsistência no logoff. Não existe um usuário autenticado.");
            return;
        }

        delegarLogoff();

        notificarUsuarioAutenticado(null);
    }

    @Override
    public UsuarioAutenticado login(final String chave, final char[] senha) throws AutenticacaoException.CredenciaisIncorretas, AutenticacaoException.ServicoIndisponivel, AutenticacaoException.UsuarioInativo, AutenticacaoException.UsuarioInexistente {
        if (usuarioAutenticado != null) {
            LOGGER.warn("Inconsistência no login. Já existia um usuário autenticado. usuario={}", usuarioAutenticado);
            usuarioAutenticado = null;
        }

        delegarLogin(chave, senha);

        notificarUsuarioAutenticado(usuarioAutenticado);

        return usuarioAutenticado;
    }

    protected void delegarLogin(final String chave, final char[] senha) throws AutenticacaoException.CredenciaisIncorretas, AutenticacaoException.ServicoIndisponivel, AutenticacaoException.UsuarioInativo, AutenticacaoException.UsuarioInexistente {
        /*
         * Realiza login.
         * Se o login falhar, então usuarioAutenticado=null.
         * Senão, usuarioAutenticado será uma descrição do usuário autenticado.
         */
        try {
            this.usuarioAutenticado = lookupSegurancaProvider().login(chave, senha);
            lastLoginException = null;
            LOGGER.info("Login com sucesso. usuario={}", usuarioAutenticado);
        } catch (final AutenticacaoException.CredenciaisIncorretas e) {
            LOGGER.info("Login recusado. Credenciais incorretas. chave={}", chave, e);
            lastLoginException = e;
            throw e;
        } catch (final AutenticacaoException.UsuarioInexistente e) {
            LOGGER.info("Login recusado. Usuário inexistente. chave={}", chave, e);
            lastLoginException = e;
            throw e;
        } catch (final AutenticacaoException.ServicoIndisponivel e) {
            LOGGER.info("Login recusado. Serviço indisponível. chave={}", chave, e);
            lastLoginException = e;
            throw e;
        } catch (final AutenticacaoException.UsuarioInativo e) {
            LOGGER.info("Login recusado. Usuário inativo. chave={}", chave, e);
            lastLoginException = e;
            throw e;
        } catch (final RuntimeException e) {
            LOGGER.error("Falha no login. chave={}; mensagem={}", chave, e.getMessage());
            throw e;
        }
    }

    @Override
    public AutenticacaoException getLoginException() {
        return lastLoginException;
    }

    protected void delegarLogoff() {
        try {
            lookupSegurancaProvider().logoff();
            LOGGER.info("Logoff com sucesso. usuario={}", usuarioAutenticado);
        } catch (final RuntimeException e) {
            LOGGER.error("Falha no logoff. usuario={}", usuarioAutenticado, e);
        } finally {
            usuarioAutenticado = null;
        }
    }

    protected SegurancaProvider lookupSegurancaProvider() {
        final SegurancaProvider lookup = Lookup.getDefault().lookup(SegurancaProvider.class);
        if (lookup == null) {
            throw new IllegalStateException("Nenhum módulo provê SegurancaProvider.");
        }
        return lookup;
    }

    protected Collection<? extends SegurancaListener> lookupSegurancaListeners() {
        return Lookup.getDefault().lookupAll(SegurancaListener.class);
    }

    protected void notificarUsuarioAutenticado(final UsuarioAutenticado usuario) {
        for (final SegurancaListener listener : lookupSegurancaListeners()) {
            try {
                listener.notificarAutenticacao(usuario);
                LOGGER.info("Sucesso em SegurancaListener.usuarioAutenticado(). usuario={}, class={}", usuario, listener);
            } catch (final RuntimeException e) {
                LOGGER.error("Falha em SegurancaListener.usuarioAutenticado(). usuario={}, class={}", usuario, listener);
            }
        }
    }

    @Override
    public boolean isDisponivel() {
        return lookupSegurancaProvider().isDisponivel();
    }
}
