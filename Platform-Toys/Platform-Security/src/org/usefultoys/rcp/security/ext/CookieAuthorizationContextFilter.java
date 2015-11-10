/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.security.ext;

import org.openide.util.lookup.ServiceProvider;
import org.usefultoys.platform.cookies.spi.ContextFilter;
import org.usefultoys.rcp.security.api.AuthenticatedUser;
import org.usefultoys.rcp.security.api.CookieAuthorization;
import org.usefultoys.rcp.security.api.SecurityService;

/**
 *
 * @author Daniel
 */
@ServiceProvider(service = ContextFilter.class)
public class CookieAuthorizationContextFilter implements ContextFilter {

    @Override
    public <T> FilterResult visit(T object) {
        CookieAuthorization annotation = object.getClass().getAnnotation(CookieAuthorization.class);
        if (annotation == null) {
            return FilterResult.CONTINUE;
        }
        final AuthenticatedUser user = SecurityService.getDefault().getCurrentAuthenticatedUser();
        if (user == null) {
            return FilterResult.REJECT;
        }
        if (!annotation.resource().isEmpty() && user.isResourceGranted(annotation.resource())) {
            return FilterResult.ACCEPT;
        }
        if (annotation.resources().length > 0 && user.isAnyResourceGranted(annotation.resources())) {
            return FilterResult.ACCEPT;
        }
        return FilterResult.REJECT;
    }
    
}
