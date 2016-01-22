package org.usefultoys.rcp.security.core;

import org.usefultoys.rcp.security.api.AuthenticatedUser;
import org.usefultoys.rcp.security.api.AuthenticationException;
import org.usefultoys.rcp.security.spi.AuthenticationListener;
import org.usefultoys.rcp.security.spi.AuthenticationIntegration;
import org.usefultoys.rcp.security.api.PasswordService;
import org.usefultoys.rcp.security.api.SecurityService;
import static org.usefultoys.rcp.security.api.SecurityService.LOGGER;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.openide.util.Exceptions;

/**
 *
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
            LOGGER.log(Level.WARNING, "Inconsistência no logoff. Não existe um usuário autenticado.");
            return;
        }

        delegarLogoff();
//        notificarUsuarioAutenticado(null);
    }

    @Override
    public AuthenticatedUser login(final String chave, final char[] senha) throws AuthenticationException {
        if (currentAuthenticatedUser != null) {
            LOGGER.log(Level.WARNING, "Inconsistência no login. Já existia um usuário autenticado. usuario={0}", currentAuthenticatedUser);
            currentAuthenticatedUser = null;
        }
        delegarLogin(chave, senha);
        notificarUsuarioAutenticado(currentAuthenticatedUser);
        return currentAuthenticatedUser;
    }

    protected void delegarLogin(final String chave, final char[] senha) throws AuthenticationException {
        /*
         * Realiza login.
         * Se o login falhar, então usuarioAutenticado=null.
         * Senão, usuarioAutenticado será uma descrição do usuário autenticado.
         */
        try {
            this.currentAuthenticatedUser = lookupAuthenticationIntegration().login(chave, senha);
            lastLoginException = null;
            LOGGER.log(Level.INFO, "Login com sucesso. usuario={}", currentAuthenticatedUser);
        } catch (final AuthenticationException.IncorrectCredentials e) {
            LOGGER.log(Level.INFO, "Login recusado. Credenciais incorretas. chave={0}", chave);
            lastLoginException = e;
            throw e;
        } catch (final AuthenticationException.InexistingUser e) {
            LOGGER.log(Level.INFO, "Login recusado. Usuário inexistente. chave={0}", chave);
            lastLoginException = e;
            throw e;
        } catch (final AuthenticationException.UnavailableService e) {
            LOGGER.log(Level.INFO, "Login recusado. Serviço indisponível. chave={0}", chave);
            lastLoginException = e;
            throw e;
        } catch (final AuthenticationException.InactiveUser e) {
            LOGGER.log(Level.INFO, "Login recusado. Usuário inativo. chave={0}", chave);
            lastLoginException = e;
            throw e;
        } catch (final RuntimeException e) {
            lastLoginException = e;
            LOGGER.log(Level.SEVERE, "Falha no login. chave={}; mensagem={}", new Object[]{chave, e.getMessage()});
            throw e;
        }
    }

    protected void delegarLogoff() {
        try {
            lookupAuthenticationIntegration().logoff();
            LOGGER.log(Level.INFO, "Logoff com sucesso. usuario={}", currentAuthenticatedUser);
        } catch (final RuntimeException e) {
            LogRecord record = new LogRecord(Level.SEVERE, "Falha no logoff. usuario={}");
            record.setParameters(new Object[]{currentAuthenticatedUser});
            record.setThrown(e);
            LOGGER.log(record);
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
                LOGGER.log(Level.INFO, "Sucesso em SegurancaListener.usuarioAutenticado(). usuario={}, class={}", new Object[]{usuario, listener});
            } catch (final RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Falha em SegurancaListener.usuarioAutenticado(). usuario={}, class={}", new Object[]{usuario, listener});
            }
        }
        for (final AuthenticationListener listener : listeners) {
            try {
                listener.notifyAuthenticatedUser(usuario);
                LOGGER.log(Level.INFO, "Sucesso em SegurancaListener.usuarioAutenticado(). usuario={}, class={}", new Object[]{usuario, listener});
            } catch (final RuntimeException e) {
                LOGGER.log(Level.SEVERE, "Falha em SegurancaListener.usuarioAutenticado(). usuario={}, class={}", new Object[]{usuario, listener});
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
