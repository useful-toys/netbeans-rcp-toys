/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Daniel
 */
public interface SecurityService {

    public static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);

    boolean isServiceAvailable();

    /**
     * @return O usuário autenticado no momento ou null se não houver.
     */
    AuthenticatedUser getCurrentAuthenticatedUser();

    Exception getLastLoginException();

    AuthenticatedUser login(String login, char[] password) throws AuthenticationException.IncorrectCredentials, AuthenticationException.UnavailableService, AuthenticationException.InactiveUser, AuthenticationException.InexistingUser;

    void logoff();

    void addListener(AuthenticationListener listener);

    void removeListener(AuthenticationListener listener);

    public static class Lookup {

        public static SecurityService getDefault() {
            final SecurityService instance = org.openide.util.Lookup.getDefault().lookup(SecurityService.class);
            if (instance == null) {
                throw new IllegalStateException("No SecurityService implementation.");
            }
            return instance;
        }
    }
}
