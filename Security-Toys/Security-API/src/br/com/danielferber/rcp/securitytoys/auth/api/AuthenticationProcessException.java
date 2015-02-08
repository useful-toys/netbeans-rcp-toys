package br.com.danielferber.rcp.securitytoys.auth.api;

public class AuthenticationProcessException extends RuntimeException {

    public AuthenticationProcessException() {
        super();
    }

    public static class Exceeded extends AuthenticationProcessException {

    }

    public static class Unavailable extends AuthenticationProcessException {

    }
    
    public static class Cancelled extends AuthenticationProcessException {

    }

}
