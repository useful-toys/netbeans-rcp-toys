/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.platform.cookies.api;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Daniel Felix Ferber
 */
public interface CookieProvider {
    boolean createStaticCookies(List<Object> cookiesRepository);
    boolean createSelectionCookies(Map<String, ? extends Object> selection, List<Object> cookiesRepository);
}
