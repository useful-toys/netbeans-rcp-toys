/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.communicationtoys.cookies.spi;

import br.com.danielferber.rcp.communicationtoys.cookies.api.CookieService;
import org.openide.util.ContextGlobalProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Daniel Felix Ferber
 */
@ServiceProvider(service = ContextGlobalProvider.class)
public class GlobalSelectionProvider implements ContextGlobalProvider {

    @Override
    public Lookup createGlobalContext() {
        return CookieService.Lookup.getDefault().getContext();
    }
    
}
