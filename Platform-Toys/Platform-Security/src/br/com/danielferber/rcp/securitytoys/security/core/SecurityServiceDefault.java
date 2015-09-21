package br.com.danielferber.rcp.securitytoys.security.core;

import br.com.danielferber.rcp.securitytoys.security.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.security.api.AuthenticationException;
import br.com.danielferber.rcp.securitytoys.security.spi.AuthenticationListener;
import br.com.danielferber.rcp.securitytoys.security.spi.AuthenticationIntegration;
import br.com.danielferber.rcp.securitytoys.security.api.PasswordService;
import br.com.danielferber.rcp.securitytoys.security.api.SecurityService;
import static br.com.danielferber.rcp.securitytoys.security.api.SecurityService.LOGGER;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author X7WS
 */
public class SecurityServiceDefault implements SecurityService {

    protected AuthenticatedUser currentAuthenticatedUser;
    protected Exception lastLoginException;
    protected final List<AuthenticationListener> listeners = new ArrayList<AuthenticationListener>();
    protected final PasswordService passwordService = new PasswordServiceDefault();

    public SecurityServiceDefault() {
        super();
        this.currentAuthenticatedUser = null;
    }

    @Override
    public AuthenticatedUser getCurrentAuthenticatedUser() {
        return currentAuthenticatedUser;
    }

    @Override
    public Exception getLastLoginException() {
        return lastLoginException;
    }

    @Override
    public void logoff() {
        if (currentAuthenticatedUser == null) {
            LOGGER.warn("Inconsistência no logoff. Não existe um usuário autenticado.");
            return;
        }

        delegarLogoff();
//        notificarUsuarioAutenticado(null);
    }

    @Override
    public AuthenticatedUser login(final String chave, final char[] senha) throws AuthenticationException.IncorrectCredentials, AuthenticationException.UnavailableService, AuthenticationException.InactiveUser, AuthenticationException.InexistingUser {
        if (currentAuthenticatedUser != null) {
            LOGGER.warn("Inconsistência no login. Já existia um usuário autenticado. usuario={}", currentAuthenticatedUser);
            currentAuthenticatedUser = null;
        }
        delegarLogin(chave, senha);
        notificarUsuarioAutenticado(currentAuthenticatedUser);
        return currentAuthenticatedUser;
    }

    protected void delegarLogin(final String chave, final char[] senha) throws AuthenticationException.IncorrectCredentials, AuthenticationException.InexistingUser, AuthenticationException.UnavailableService, AuthenticationException.InactiveUser {
        /*
         * Realiza login.
         * Se o login falhar, então usuarioAutenticado=null.
         * Senão, usuarioAutenticado será uma descrição do usuário autenticado.
         */
        try {
            this.currentAuthenticatedUser = lookupAuthenticationIntegration().login(chave, senha);
            lastLoginException = null;
            LOGGER.info("Login com sucesso. usuario={}", currentAuthenticatedUser);
        } catch (final AuthenticationException.IncorrectCredentials e) {
            LOGGER.info("Login recusado. Credenciais incorretas. chave={}", chave, e);
            lastLoginException = e;
            throw e;
        } catch (final AuthenticationException.InexistingUser e) {
            LOGGER.info("Login recusado. Usuário inexistente. chave={}", chave, e);
            lastLoginException = e;
            throw e;
        } catch (final AuthenticationException.UnavailableService e) {
            LOGGER.info("Login recusado. Serviço indisponível. chave={}", chave, e);
            lastLoginException = e;
            throw e;
        } catch (final AuthenticationException.InactiveUser e) {
            LOGGER.info("Login recusado. Usuário inativo. chave={}", chave, e);
            lastLoginException = e;
            throw e;
        } catch (final RuntimeException e) {
            lastLoginException = e;
            LOGGER.error("Falha no login. chave={}; mensagem={}", chave, e.getMessage());
            throw e;
        }
    }

    protected void delegarLogoff() {
        try {
            lookupAuthenticationIntegration().logoff();
            LOGGER.info("Logoff com sucesso. usuario={}", currentAuthenticatedUser);
        } catch (final RuntimeException e) {
            LOGGER.error("Falha no logoff. usuario={}", currentAuthenticatedUser, e);
        } finally {
            currentAuthenticatedUser = null;
        }
    }

    protected AuthenticationIntegration lookupAuthenticationIntegration() {
        final AuthenticationIntegration lookup = org.openide.util.Lookup.getDefault().lookup(AuthenticationIntegration.class);
        if (lookup == null) {
            throw new IllegalStateException("Nenhum módulo provê SegurancaProvider.");
        }
        return lookup;
    }

    protected Collection<? extends AuthenticationListener> lookupSegurancaListeners() {
        return org.openide.util.Lookup.getDefault().lookupAll(AuthenticationListener.class);
    }

    protected void notificarUsuarioAutenticado(final AuthenticatedUser usuario) {
        for (final AuthenticationListener listener : lookupSegurancaListeners()) {
            try {
                listener.notifyAuthenticatedUser(usuario);
                LOGGER.info("Sucesso em SegurancaListener.usuarioAutenticado(). usuario={}, class={}", usuario, listener);
            } catch (final RuntimeException e) {
                LOGGER.error("Falha em SegurancaListener.usuarioAutenticado(). usuario={}, class={}", usuario, listener);
            }
        }
        for (final AuthenticationListener listener : listeners) {
            try {
                listener.notifyAuthenticatedUser(usuario);
                LOGGER.info("Sucesso em SegurancaListener.usuarioAutenticado(). usuario={}, class={}", usuario, listener);
            } catch (final RuntimeException e) {
                LOGGER.error("Falha em SegurancaListener.usuarioAutenticado(). usuario={}, class={}", usuario, listener);
            }
        }
    }

    @Override
    public PasswordService getPasswordService() {
        return passwordService;
    }

    @Override
    public boolean isServiceAvailable() {
        return !(lastLoginException instanceof AuthenticationException.UnavailableService)
                && !(lastLoginException instanceof RuntimeException);
    }

    @Override
    public void addListener(AuthenticationListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(AuthenticationListener listener) {
        listeners.remove(listener);
    }
}
