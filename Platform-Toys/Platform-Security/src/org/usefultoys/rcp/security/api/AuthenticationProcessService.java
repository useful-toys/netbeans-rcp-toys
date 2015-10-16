package org.usefultoys.rcp.security.api;

import org.openide.util.Lookup;

public interface AuthenticationProcessService {

    AuthenticatedUser executeAuthenticationQuery() throws AuthenticationProcessException;

    void executeLogoff();

    static AuthenticationProcessService getDefault() {
        final AuthenticationProcessService instance = Lookup.getDefault().lookup(AuthenticationProcessService.class);
        if (instance == null) {
            throw new IllegalStateException("No AuthenticationProcessService implementation.");
        }
        return instance;
    }
}
