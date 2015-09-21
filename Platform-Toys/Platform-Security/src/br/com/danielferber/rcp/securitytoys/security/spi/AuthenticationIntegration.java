package br.com.danielferber.rcp.securitytoys.security.spi;

import br.com.danielferber.rcp.securitytoys.security.api.AuthenticatedUser;
import br.com.danielferber.rcp.securitytoys.security.api.AuthenticationException;

public interface AuthenticationIntegration {
    AuthenticatedUser login(String usuario, char[] senha) throws AuthenticationException.IncorrectCredentials, AuthenticationException.UnavailableService, AuthenticationException.InactiveUser, AuthenticationException.InactiveUser, AuthenticationException.InexistingUser;
    void logoff();
}
