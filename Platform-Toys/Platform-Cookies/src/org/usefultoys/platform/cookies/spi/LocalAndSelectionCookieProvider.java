/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.platform.cookies.spi;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Callback interface that populates the context with cookies from the
 * combination of local state and selection. Used in rare situations, when a
 * cookie depends on both TopComponent state and selection.
 */
public interface LocalAndSelectionCookieProvider {

    /**
     * Callback method that adds selection cookies to the context. The
     * method is intended to translate the active TopComponent state and selection or
     * focus (represented by the Map and Set parameters, if applicable) into
     * one or more cookies added to the context.
     * @param localMap A map of values that represent the active
     * TopComponent's state.
     * @param localSet A set of values that represent the active
     * @param selectionMap A map of values that represent the active
     * TopComponent's selection or focus.
     * @param selectionSet A set of values that represent the active
     * TopComponent's selection or focus.
     * @param context Current context where cookies may be added to
     * @return true if cookies were added to the context
     */
    boolean createLocalAndSelectionCookies(Map<String, ? extends Object> localMap, Set<Object> localSet, Map<String, ? extends Object> selectionMap, Set<Object> selectionSet, List<Object> context);
    
}
