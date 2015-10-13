/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.platform.cookies.impl;

import java.util.Collection;
import org.openide.util.Lookup;
import org.openide.util.LookupListener;

/**
 *
 * @author Daniel
 */
public class ResultWrapper<T> extends Lookup.Result<T> {

    private final LookupWrapper lookupWrapper;
    private final Lookup.Result<T> wrappedResult;

    public ResultWrapper(LookupWrapper lookupWrapper, Lookup.Result<T> wrappedResult) {
        this.lookupWrapper = lookupWrapper;
        this.wrappedResult = wrappedResult;
    }

    @Override
    public void addLookupListener(LookupListener l) {
        wrappedResult.addLookupListener(l);
    }

    @Override
    public void removeLookupListener(LookupListener l) {
        wrappedResult.removeLookupListener(l);
    }

    @Override
    public Collection<? extends T> allInstances() {
        return lookupWrapper.filter(wrappedResult.allInstances());
    }
}
