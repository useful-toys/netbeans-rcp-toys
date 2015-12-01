package org.usefultoys.netbeansrcp.platform.cookies.api;

import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.openide.util.Lookup;

/**
 * Describes the TopComponent context as a collection or map of objects, that
 * are eventually translated into cookies.
 *
 * @author Daniel Felix Ferber
 */
public interface TopComponentCookieContext {

    Lookup getLookup();
    Lookup getLocalLookup();
    
    /**
     * Add or replace a single cookie object, without using translation. Call
     * {@link #apply()} to make the cookie object visible within the context.
     *
     * @param cookieObject The cookie object to be added or replaced.
     * @return the {@link TopComponentCookieContext} itself.
     */
    TopComponentCookieContext addCookie(Object cookieObject);

    /**
     * Add or replace a list of cookie objects, without using translation. Call
     * {@link #apply()} to make the cookie objects visible within the context.
     *
     * @param cookieObjects The cookie objects to be added or replaced.
     * @return the {@link TopComponentCookieContext} itself.
     */
    TopComponentCookieContext addCookie(Object... cookieObjects);

    /**
     * Add or replace a value from the context Set. Call {@link #apply()} to
     * translate the updated Map into cookies.
     *
     * @param object Value to add or replace
     * @return the {@link TopComponentCookieContext} itself.
     */
    TopComponentCookieContext addLocal(Object object);

    /**
     * Add or replace a value from the context Map. Call {@link #apply()} in
     * order to translate the updated Set into cookies.
     *
     * @param key Key to add or replace
     * @param object Value to add or replace
     * @return the {@link TopComponentCookieContext} itself.
     */
    TopComponentCookieContext addLocal(String key, Object object);

    TopComponentCookieContext apply();

    TopComponentCookieContext clearCookies();

    TopComponentCookieContext clearSelection();

    TopComponentCookieContext clearLocalMap();

    TopComponentCookieContext clearLocalSet();

    TopComponentCookieContext removeCookie(Object object);

    TopComponentCookieContext removeCookie(Object... cookies);

    TopComponentCookieContext removeLocal(Object object);

    TopComponentCookieContext removeLocal(String ket, Object object);

    TopComponentCookieContext setCookieSet(Set<Object> newSet);

    TopComponentCookieContext setLocalSet(Set<Object> newSet);

    TopComponentCookieContext setSelection(Map<String, ? extends Object> newMap, Set<Object> newSet);

    TopComponentCookieContext setLocalMap(Map<String, ? extends Object> newMap);

//    CookieContext activate();
//
//    CookieContext deactivate();

    Lookup actionsLocalContext();

    void populateToolbar(JToolBar toolbar, List<? extends Action> actions);

    void populateToolbar(JToolBar toolbar, String actionsPath);

    void update();
}
