/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.platform.cookies.api;

import java.util.Collection;
import java.util.Map;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author Daniel Felix Ferber
 */
public interface CookieService {

    org.openide.util.Lookup getContext();

    void updateStatic();

    void setFocusObjects(Map<String, ? extends Object> focus);
    void setFocusCookies(Collection<Object> cookies);
    void setFocusCookies(Object ...cookies);
    void addFocusCookies(Collection<Object> cookies);
    void addFocusCookies(Object ...cookies);
    void removeFocusCookies(Collection<Object> cookies);
    void removeFocusCookies(Object ...cookies);
    void clearFocus();

    void setSelectionObjects(Map<String, ? extends Object> selection);
    void setSelectionCookies(Collection<Object> cookies);
    void setSelectionCookies(Object ...cookies);
    void addSelectionCookies(Collection<Object> cookies);
    void addSelectionCookies(Object ...cookies);
    void removeSelectionCookies(Collection<Object> cookies);
    void removeSelectionCookies(Object ...cookies);
    void clearSelection();

    void setLocalContent(InstanceContent content);
    void clearLocalContent();

    public static class Lookup {

        public static CookieService getDefault() {
            return org.openide.util.Lookup.getDefault().lookup(CookieService.class);
        }
    }

}
