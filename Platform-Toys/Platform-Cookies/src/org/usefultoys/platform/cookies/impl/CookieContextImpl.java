/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.platform.cookies.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JToolBar;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.usefultoys.platform.cookies.api.CookieService;
import org.usefultoys.platform.cookies.api.CookieContext;
import org.usefultoys.platform.cookies.api.ToolbarBuilder;

public class CookieContextImpl implements CookieContext {

    private static final Logger logger = Logger.getLogger(CookieService.class.getName());

    private final InstanceContent localContent = new InstanceContent();
    private final Lookup localLookup = new AbstractLookup(localContent);
    private final Lookup actionsLocalLookup;

    private final Map<String, Object> localMap = new TreeMap<>();
    private final Set<Object> localSet = new HashSet<>();
    private final Set<Object> cookieSet = new HashSet<>();

    private final Map<String, Object> selectionMap = new TreeMap<>();
    private final Set<Object> selectionSet = new HashSet<>();

    private final CookieServiceImpl parent;

    public CookieContextImpl(CookieServiceImpl parent) {
        this.parent = parent;
        this.actionsLocalLookup = new ProxyLookup(localLookup, parent.getStaticLookup());
    }

    Lookup getLocalLookup() {
        return localLookup;
    }

    @Override
    public CookieContext setSelection(Map<String, ? extends Object> newMap, Set<Object> newSet) {
        selectionMap.clear();
        if (newMap != null) {
            selectionMap.putAll(newMap);
        }
        selectionSet.clear();
        if (newSet != null) {
            selectionSet.addAll(newSet);
        }
        return this;
    }

    @Override
    public CookieContext setLocalMap(Map<String, ? extends Object> newMap) {
        localMap.clear();
        if (newMap != null) {
            localMap.putAll(newMap);
        }
        return this;
    }

    @Override
    public CookieContext setLocalSet(Set<Object> newSet) {
        localSet.clear();
        if (newSet != null) {
            localSet.addAll(newSet);
        }
        return this;
    }

    @Override
    public CookieContext setCookieSet(Set<Object> newSet) {
        cookieSet.clear();
        if (newSet != null) {
            cookieSet.addAll(newSet);
        }
        return this;
    }

    @Override
    public CookieContext clearLocalMap() {
        localMap.clear();
        return this;
    }

    @Override
    public CookieContext clearSelection() {
        selectionMap.clear();
        selectionSet.clear();
        return this;
    }

    @Override
    public CookieContext clearLocalSet() {
        localSet.clear();
        return this;
    }

    @Override
    public CookieContext clearCookies() {
        cookieSet.clear();
        return this;
    }

    @Override
    public CookieContext addLocal(String ket, Object newObject) {
        localMap.put(ket, newObject);
        return this;
    }

    @Override
    public CookieContext addLocal(Object newObject) {
        localSet.add(newObject);
        return this;
    }

    @Override
    public CookieContext addCookie(Object newObject) {
        cookieSet.add(newObject);
        return this;
    }

    @Override
    public CookieContext addCookie(Object... newCookies) {
        cookieSet.addAll(Arrays.asList(newCookies));
        return this;
    }

    @Override
    public CookieContext removeLocal(String ket, Object object) {
        localMap.remove(ket, object);
        return this;
    }

    @Override
    public CookieContext removeLocal(Object object) {
        localSet.remove(object);
        return this;
    }

    @Override
    public CookieContext removeCookie(Object object) {
        cookieSet.remove(object);
        return this;
    }

    @Override
    public CookieContext removeCookie(Object... cookies) {
        cookieSet.remove(Arrays.asList(cookies));
        return this;
    }

    @Override
    public CookieContext apply() {
        Collection<? extends CookieService.SelectionCookieProvider> selectionProviders = Lookup.getDefault().lookupAll(CookieService.SelectionCookieProvider.class);
        Collection<? extends CookieService.LocalCookieProvider> localProviders = Lookup.getDefault().lookupAll(CookieService.LocalCookieProvider.class);

        List<Object> cookies = new ArrayList<>();
        boolean cookiesCreated = false;
        for (CookieService.SelectionCookieProvider provider : selectionProviders) {
            cookiesCreated |= provider.createSelectionCookies(selectionMap, selectionSet, cookies);
        }
        for (CookieService.LocalCookieProvider provider : localProviders) {
            cookiesCreated |= provider.createLocalCookies(localMap, localSet, cookies);
        }
        cookiesCreated |= cookies.addAll(cookieSet);
        if (cookiesCreated) {
            logger.info("Set " + cookies.size() + " context cookies");
            localContent.set(cookies, null);
        } else {
            logger.info("Set zero static cookies");
            localContent.set(Collections.EMPTY_LIST, null);
        }
        return this;
    }

    @Override
    public CookieContext activate() {
        parent.activate(this);
        return this;
    }

    @Override
    public CookieContext deactivate() {
        parent.deactivate();
        return this;
    }

    @Override
    public Lookup getActionsLocalContext() {
        return actionsLocalLookup;
    }

    @Override
    public void populateToolbar(JToolBar toolbar, List<? extends Action> actions) {
        ToolbarBuilder.build(toolbar, actions, this.actionsLocalLookup);
    }

    @Override
    public void populateToolbar(JToolBar toolbar, String actionsPath) {
        ToolbarBuilder.build(toolbar, actionsPath, this.actionsLocalLookup);
    }

    @Override
    public void update() {
        this.apply();
    }
}
