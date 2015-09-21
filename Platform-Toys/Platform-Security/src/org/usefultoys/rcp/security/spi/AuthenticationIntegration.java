package org.usefultoys.rcp.security.spi;

import org.usefultoys.rcp.security.api.AuthenticatedUser;
import org.usefultoys.rcp.security.api.AuthenticationException;

public interface AuthenticationIntegration {
    AuthenticatedUser login(String usuario, char[] senha) throws AuthenticationException.IncorrectCredentials, AuthenticationException.UnavailableService, AuthenticationException.InactiveUser, AuthenticationException.InactiveUser, AuthenticationException.InexistingUser;
    void logoff();
}
