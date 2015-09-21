package org.usefultoys.rcp.security.api;

import org.openide.util.NbBundle;

/**
 * The authentication process could not determine an authenticated user.
 *
 * @author Daniel Felix Ferber
 */
@NbBundle.Messages({
    "AuthenticationProcessException_Exeeded=Too many authentication attempts.",
    "AuthenticationProcessException_Unavailable=Authentication service unavailable.",
    "AuthenticationProcessException_Canceled=Authentication canceled by user."
})
public class AuthenticationProcessException extends RuntimeException {

    protected AuthenticationProcessException(String message) {
        super(message);
    }

    /**
     * The authentication process exceeded the maximal number of attempts.
     */
    public static class Exceeded extends AuthenticationProcessException {
        public Exceeded() {
            super(Bundle.AuthenticationProcessException_Exeeded());
        }
    }

    /**
     * The underlying authentication service is unavailable.
     */
    public static class Unavailable extends AuthenticationProcessException {
        public Unavailable() {
            super(Bundle.AuthenticationProcessException_Unavailable());
        }
    }

    /**
     * The authentication process was canceled by the user.
     */
    public static class Canceled extends AuthenticationProcessException {
        public Canceled() {
            super(Bundle.AuthenticationProcessException_Canceled());
        }
    }
}
