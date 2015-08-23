package br.com.danielferber.rcp.securitytoys.security.api;

import org.openide.util.Lookup;

public interface AuthenticationProcessService {

    public abstract AuthenticatedUser executeAuthenticationQuery() throws AuthenticationProcessException;

    public abstract void executeLogoff();

    static AuthenticationProcessService getDefault() {
        final AuthenticationProcessService instance = Lookup.getDefault().lookup(AuthenticationProcessService.class);
        if (instance == null) {
            throw new IllegalStateException("No module provides AuthenticationProcessService.");
        }
        return instance;
    }

}
