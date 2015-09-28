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
import org.openide.util.Lookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Daniel Felix Ferber
 */
public interface CookieContext {


    CookieContext addCookie(Object newObject);

    CookieContext addCookie(Object... newCookies);

    CookieContext addLocal(Object newObject);

    CookieContext addLocal(String ket, Object newObject);

    CookieContext apply();

    CookieContext clearCookies();

    CookieContext clearSelection();

    CookieContext clearLocalMap();

    CookieContext clearLocalSet();

    Lookup getContext();

    CookieContext removeCookie(Object object);

    CookieContext removeCookie(Object... cookies);

    CookieContext removeLocal(Object object);

    CookieContext removeLocal(String ket, Object object);

    CookieContext setCookieSet(Set<Object> newSet);

    CookieContext setLocalSet(Set<Object> newSet);

    CookieContext setSelection(Map<String, ? extends Object> newMap, Set<Object> newSet);

    CookieContext setLocalMap(Map<String, ? extends Object> newMap);
    CookieContext activate();

    CookieContext deactivate();
}
