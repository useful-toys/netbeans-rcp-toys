/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.cookies.ext;

import org.netbeans.modules.openide.windows.GlobalActionContextImpl;
import org.openide.util.ContextGlobalProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;
import org.usefultoys.netbeansrcp.platform.cookies.impl.LookupWrapper;

/**
 *
 * @author Daniel Felix Ferber
 */
@ServiceProvider(service = ContextGlobalProvider.class,
        supersedes = "org.netbeans.modules.openide.windows.GlobalActionContextImpl")
public class GlobalSelectionProvider implements ContextGlobalProvider {
    private LookupWrapper proxyLookup;

    public GlobalSelectionProvider() {
        super();
    }

    @Override
    public Lookup createGlobalContext() {
        if (proxyLookup == null) {
            GlobalActionContextImpl globalContextProvider = new GlobalActionContextImpl();
            Lookup globalContextLookup = globalContextProvider.createGlobalContext();
            proxyLookup = new LookupWrapper(globalContextLookup);
        }
        return proxyLookup;
    }

}
