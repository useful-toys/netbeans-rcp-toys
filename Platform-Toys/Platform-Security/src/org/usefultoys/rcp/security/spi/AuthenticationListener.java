package org.usefultoys.rcp.security.spi;

import org.usefultoys.rcp.security.api.AuthenticatedUser;

public interface AuthenticationListener {
    void notifyAuthenticatedUser(AuthenticatedUser usuario);
}
