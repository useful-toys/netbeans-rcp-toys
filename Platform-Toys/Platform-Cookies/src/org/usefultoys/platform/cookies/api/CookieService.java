/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.platform.cookies.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Daniel Felix Ferber
 */
public interface CookieService {

    /** @return Lookup exported globally to Netbeans RCP. */
    org.openide.util.Lookup getContext();
    org.openide.util.Lookup getLocalContext();

    CookieContext createCookieContext();
    
    CookieService update();

    CookieService updateStatic();
    
    public static class Lookup {

        public static CookieService getDefault() {
            return org.openide.util.Lookup.getDefault().lookup(CookieService.class);
        }
    }

    /**
     * Callback that adds static cookies to the context. Static cookies are always
     * available and do not depend on TopCompnent or selections.
     *
     * @author Daniel Felix Ferber
     */
    public static interface StaticCookieProvider {

        boolean createStaticCookies(List<Object> cookiesRepository);
    }

    /**
     * Callback that converts local objects as cookies added to the context. Local cookies
     * represent the state of the active TopComponent.
     *
     * @author Daniel Felix Ferber
     */
    public static interface LocalCookieProvider {

        boolean createLocalCookies(Map<String, ? extends Object> localMap, Set<Object> localSet, List<Object> cookiesRepository);
    }

    /**
     * Callback that converts selection objects as cookies added to the context. Selection cookies
     * represent the focus or selection of the current component of the active TopComponent.
     *
     * @author Daniel Felix Ferber
     */
    public static interface SelectionCookieProvider {

        boolean createSelectionCookies(Map<String, ? extends Object> selectionMap, Set<Object> selectionSet, List<Object> cookiesRepository);
    }

}
