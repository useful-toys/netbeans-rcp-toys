/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.communicationtoys.cookies.impl;

import br.com.danielferber.rcp.communicationtoys.cookies.api.CookieProvider;
import br.com.danielferber.rcp.communicationtoys.cookies.api.CookieService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Daniel Felix Ferber
 */
@ServiceProvider(service = CookieService.class)
public class CookieFactoryImpl implements CookieService {

    private InstanceContent localContent = null;
    private final InstanceContent staticContent = new InstanceContent();
    private final InstanceContent selectionContent1 = new InstanceContent();
    private final InstanceContent selectionContent2 = new InstanceContent();
    private final InstanceContent focusContent1 = new InstanceContent();
    private final InstanceContent focusContent2 = new InstanceContent();
    private final org.openide.util.Lookup staticLookup = new AbstractLookup(staticContent);
    private final org.openide.util.Lookup selectionLookup1 = new AbstractLookup(selectionContent1);
    private final org.openide.util.Lookup selectionLookup2 = new AbstractLookup(selectionContent2);
    private final org.openide.util.Lookup focusLookup1 = new AbstractLookup(focusContent1);
    private final org.openide.util.Lookup focusLookup2 = new AbstractLookup(focusContent2);
    private final org.openide.util.Lookup globalLookup = new ProxyLookup(staticLookup, selectionLookup1, selectionLookup2, focusLookup1, focusLookup2);
    private final org.openide.util.Lookup.Result<Object> globalLookupResult = globalLookup.lookupResult(Object.class);

    public CookieFactoryImpl() {
        super();
        globalLookupResult.addLookupListener(new LookupListener() {
            @Override
            public void resultChanged(LookupEvent ev) {
                if (localContent != null) {
                    localContent.set(globalLookupResult.allInstances(), null);
                }
            }
        });
    }

    @Override

    public org.openide.util.Lookup getContext() {
//        return globalLookup;
        return staticLookup;
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
    public void setFocusObjects(Map<String, ? extends Object> focusObjects) {
        if (focusObjects.isEmpty()) {
            focusContent1.set(Collections.EMPTY_LIST, null);
            return;
        }
        Collection<? extends CookieProvider> providers = org.openide.util.Lookup.getDefault().lookupAll(CookieProvider.class);
        List<Object> cookiesRepository = new ArrayList<>();
        boolean cookiesCreated = false;
        for (CookieProvider provider : providers) {
            cookiesCreated |= provider.createSelectionCookies(focusObjects, cookiesRepository);
        }
        if (cookiesCreated) {
            focusContent1.set(cookiesRepository, null);
        } else {
            focusContent1.set(Collections.EMPTY_LIST, null);
        }
    }

    @Override
    public void setFocusCookies(Object... cookies) {
        focusContent2.set(Arrays.asList(cookies), null);
    }

    @Override
    public void addFocusCookies(Object... cookies) {
        for (Object cookie : cookies) {
            focusContent2.add(cookie);
        }
    }

    @Override
    public void removeFocusCookies(Object... cookies) {
        for (Object cookie : cookies) {
            focusContent2.remove(cookie);
        }
    }

    @Override
    public void clearFocus() {
        focusContent1.set(Collections.EMPTY_LIST, null);
        focusContent2.set(Collections.EMPTY_LIST, null);
    }

    @Override
    public void clearSelection() {
        selectionContent1.set(Collections.EMPTY_LIST, null);
        selectionContent2.set(Collections.EMPTY_LIST, null);
    }

    @Override
    public void setSelectionObjects(Map<String, ? extends Object> selectionObjects) {
        if (selectionObjects.isEmpty()) {
            selectionContent1.set(Collections.EMPTY_LIST, null);
            return;
        }
        Collection<? extends CookieProvider> providers = org.openide.util.Lookup.getDefault().lookupAll(CookieProvider.class);
        List<Object> cookiesRepository = new ArrayList<>();
        boolean cookiesCreated = false;
        for (CookieProvider provider : providers) {
            cookiesCreated |= provider.createSelectionCookies(selectionObjects, cookiesRepository);
        }
        if (cookiesCreated) {
            selectionContent1.set(cookiesRepository, null);
        } else {
            selectionContent1.set(Collections.EMPTY_LIST, null);
        }
    }

    @Override
    public void setSelectionCookies(Object... cookies) {
        selectionContent2.set(Arrays.asList(cookies), null);
    }

    @Override
    public void addSelectionCookies(Object... cookies) {
        for (Object cookie : cookies) {
            selectionContent2.add(cookie);
        }
    }

    @Override
    public void removeSelectionCookies(Object... cookies) {
        for (Object cookie : cookies) {
            selectionContent2.remove(cookie);
        }
    }

    @Override
    public void setLocalContent(InstanceContent content) {
        clearLocalContentAux();
        this.localContent = content;
        populateLocalContentAux();
    }

    private void clearLocalContentAux() {
        if (this.localContent != null) {
            localContent.set(Collections.EMPTY_LIST, null);
        }
    }

    @Override
    public void clearLocalContent() {
        clearLocalContentAux();
        this.localContent = null;
    }

    private void populateLocalContentAux() {
        if (this.localContent != null) {
            localContent.set(globalLookup.lookupAll(Object.class), null);
        }
    }
}
