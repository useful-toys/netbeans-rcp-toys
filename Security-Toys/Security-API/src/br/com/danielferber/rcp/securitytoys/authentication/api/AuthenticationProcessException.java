package br.com.danielferber.rcp.securitytoys.authentication.api;

/**
 * The authentication process could not determine an authenticated user.
 *
 * @author Daniel Felix Ferber
 */
public class AuthenticationProcessException extends RuntimeException {

    public AuthenticationProcessException() {
        super();
    }

    /**
     * The authentication process exceeded the maximal number of attempts.
     */
    public static class Exceeded extends AuthenticationProcessException {

    }

    /**
     * The underlying authentication service is unavailable.
     */
    public static class Unavailable extends AuthenticationProcessException {

    }

    /**
     * The authentication process was canceled by the user.
     */
    public static class Canceled extends AuthenticationProcessException {

    }
}
