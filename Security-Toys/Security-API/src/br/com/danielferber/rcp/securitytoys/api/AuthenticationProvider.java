package br.com.danielferber.rcp.securitytoys.api;

public interface AuthenticationProvider {
    AuthenticatedUser login(String usuario, char[] senha) throws AuthenticationException.IncorrectCredentials, AuthenticationException.UnavailableService, AuthenticationException.InactiveUser, AuthenticationException.InactiveUser, AuthenticationException.InexistingUser;
    void logoff();

    boolean isDisponivel();
}
