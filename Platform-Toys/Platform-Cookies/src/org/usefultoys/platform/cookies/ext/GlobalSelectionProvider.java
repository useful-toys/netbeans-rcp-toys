/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.platform.cookies.ext;

import org.usefultoys.platform.cookies.api.CookieService;
import org.openide.util.ContextGlobalProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Daniel Felix Ferber
 */
@ServiceProvider(service = ContextGlobalProvider.class,
        supersedes = "org.netbeans.modules.openide.windows.GlobalActionContextImpl")
public class GlobalSelectionProvider implements ContextGlobalProvider {

    public GlobalSelectionProvider() {
        super();
    }
    
    @Override
    public Lookup createGlobalContext() {
        return CookieService.Lookup.getDefault().getContext();
    }

}
