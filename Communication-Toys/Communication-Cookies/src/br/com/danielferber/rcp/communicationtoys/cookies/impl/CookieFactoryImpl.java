/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.communicationtoys.cookies.impl;

import br.com.danielferber.rcp.communicationtoys.cookies.api.CookieService;
import br.com.danielferber.rcp.communicationtoys.cookies.api.CookieProvider;
import br.com.danielferber.rcp.communicationtoys.cookies.lookup.DynamicProxyLookup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Daniel Felix Ferber
 */
@ServiceProvider(service = CookieService.class)
public class CookieFactoryImpl implements CookieService {

    private final InstanceContent staticContent = new InstanceContent();
    private final InstanceContent selectionContent = new InstanceContent();
    private final InstanceContent focusContent = new InstanceContent();
    private final org.openide.util.Lookup instanceLookup = new AbstractLookup(staticContent);
    private final org.openide.util.Lookup selectionLookup = new AbstractLookup(selectionContent);
    private final org.openide.util.Lookup focusLookup = new AbstractLookup(focusContent);
    private final org.openide.util.Lookup globalLookup = new DynamicProxyLookup(instanceLookup, selectionLookup, focusLookup);

    @Override
    public org.openide.util.Lookup getContext() {
        return globalLookup;
    }

    @Override
    public void updateStatic() {
        Collection<? extends CookieProvider> providers = org.openide.util.Lookup.getDefault().lookupAll(CookieProvider.class);
        List<Object> cookiesRepository = new ArrayList<>();
        boolean cookiesCreated = false;
        for (CookieProvider provider : providers) {
            cookiesCreated |= provider.createStaticCookies(cookiesRepository);
        }
        if (cookiesCreated) {
            staticContent.set(cookiesRepository, null);
        } else {
            staticContent.set(Collections.EMPTY_LIST, null);
        }
    }

    @Override
    public void updateTopComponent(Map<String, ? extends Object> focus) {
        if (focus.isEmpty()) {
            focusContent.set(Collections.EMPTY_LIST, null);
            return;
        }
        Collection<? extends CookieProvider> providers = org.openide.util.Lookup.getDefault().lookupAll(CookieProvider.class);
        List<Object> cookiesRepository = new ArrayList<>();
        boolean cookiesCreated = false;
        for (CookieProvider provider : providers) {
            cookiesCreated |= provider.createSelectionCookies(focus, cookiesRepository);
        }
        if (cookiesCreated) {
            focusContent.set(cookiesRepository, null);
        } else {
            focusContent.set(Collections.EMPTY_LIST, null);
        }
    }

    @Override
    public void clearSelection() {
        selectionContent.set(Collections.EMPTY_LIST, null);
    }

    @Override
    public void updateSelection(Map<String, ? extends Object> selection) {
        if (selection.isEmpty()) {
            selectionContent.set(Collections.EMPTY_LIST, null);
            return;
        }
        Collection<? extends CookieProvider> providers = org.openide.util.Lookup.getDefault().lookupAll(CookieProvider.class);
        List<Object> cookiesRepository = new ArrayList<>();
        boolean cookiesCreated = false;
        for (CookieProvider provider : providers) {
            cookiesCreated |= provider.createSelectionCookies(selection, cookiesRepository);
        }
        if (cookiesCreated) {
            selectionContent.set(cookiesRepository, null);
        } else {
            selectionContent.set(Collections.EMPTY_LIST, null);
        }
    }
}
