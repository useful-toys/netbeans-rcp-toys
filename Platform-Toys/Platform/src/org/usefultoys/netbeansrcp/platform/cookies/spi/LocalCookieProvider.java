/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.cookies.spi;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Callback interface that populates the context with local cookies. Local
 * cookies represent the state of the active TopComponent.
 */
public interface LocalCookieProvider {

    /**
     * Callback method that adds local cookies to the context. The method is
     * intended to translate the active TopComponent state (represented by
     * the Map and Set parameters, if applicable) into one or more cookies
     * added to the context.
     *
     * @param localMap A map of values that represent the active
     * TopComponent's state.
     * @param localSet A set of values that represent the active
     * TopComponent's state.
     * @param context Current context where cookies may be added to
     * @return true if cookies were added to the context
     */
    boolean createLocalCookies(Map<String, ? extends Object> localMap, Set<Object> localSet, List<Object> context);
    
}
