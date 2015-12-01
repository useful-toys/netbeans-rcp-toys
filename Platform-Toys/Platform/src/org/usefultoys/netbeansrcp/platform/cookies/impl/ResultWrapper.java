/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.cookies.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author Daniel
 */
public class ResultWrapper<T> extends Lookup.Result<T> {

    private final LookupWrapper lookupWrapper;
    private final Lookup.Result<T> wrappedResult;
    private final Map<LookupListener, LookupListener> listenerMap = new WeakHashMap<>();

    public ResultWrapper(LookupWrapper lookupWrapper, Lookup.Result<T> wrappedResult) {
        this.lookupWrapper = lookupWrapper;
        this.wrappedResult = wrappedResult;
    }

    @Override
    public void addLookupListener(final LookupListener l) {
        final LookupListener listenerWrapper = (LookupEvent ev) -> {
            l.resultChanged(new LookupEvent(ResultWrapper.this));
        };
        listenerMap.put(l, listenerWrapper);
        wrappedResult.addLookupListener(listenerWrapper);
    }

    @Override
    public void removeLookupListener(LookupListener l) {
        wrappedResult.removeLookupListener(listenerMap.get(l));
    }

    @Override
    public Collection<? extends T> allInstances() {
        return lookupWrapper.filterInstance(wrappedResult.allInstances());
    }

    @Override
    public Set<Class<? extends T>> allClasses() {
        return wrappedResult.allClasses();
    }

    @Override
    public Collection<? extends Lookup.Item<T>> allItems() {
        return lookupWrapper.filterItem(wrappedResult.allItems());
    }    
}
