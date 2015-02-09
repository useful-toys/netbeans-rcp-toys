package br.com.danielferber.rcp.securitytoys.auth.api;

import br.com.danielferber.rcp.securitytoys.api.AuthenticatedUser;
import org.openide.util.Lookup;

public abstract class AuthenticationProcessService {

    protected AuthenticationProcessService() {
        super();
    }

    public static final AuthenticationProcessService getDefault() {
        final AuthenticationProcessService instance = Lookup.getDefault().lookup(AuthenticationProcessService.class);
        if (instance == null) {
            throw new IllegalStateException("Nenhum módulo provê ProcessoAutenticacaoService.");
        }
        return instance;
    }

    public abstract AuthenticatedUser executeAuthenticationQuery() throws AuthenticationProcessException;
    public abstract void executeLogoff();

    

}
