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
 * Callback interface that populates the context with selection cookies.
 * Selection cookies represent the focus or selection of the focused
 * component of the active TopComponent.
 */
public interface SelectionCookieProvider {

    /**
     * Callback method that adds selection cookies to the context. The
     * method is intended to translate the active TopComponent selection or
     * focus (represented by the Map and Set parameters, if applicable) into
     * one or more cookies added to the context.
     *
     * @param selectionMap A map of values that represent the active
     * TopComponent's selection or focus.
     * @param selectionSet A set of values that represent the active
     * TopComponent's selection or focus.
     * @param context Current context where cookies may be added to
     * @return true if cookies were added to the context
     */
    boolean createSelectionCookies(Map<String, ? extends Object> selectionMap, Set<Object> selectionSet, List<Object> context);
    
}
