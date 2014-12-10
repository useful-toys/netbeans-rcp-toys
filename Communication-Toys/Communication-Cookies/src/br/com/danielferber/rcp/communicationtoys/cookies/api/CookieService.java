/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.communicationtoys.cookies.api;

import java.util.Map;

/**
 *
 * @author Daniel Felix Ferber
 */
public interface CookieService {

    public org.openide.util.Lookup getContext();

    public void updateStatic();

    public void updateTopComponent(Map<String, ? extends Object> focus);

    public void updateSelection(Map<String, ? extends Object> selection);

    public void clearSelection();

    public static class Lookup {

        public static CookieService getDefault() {
            return org.openide.util.Lookup.getDefault().lookup(CookieService.class);
        }
    }

}
