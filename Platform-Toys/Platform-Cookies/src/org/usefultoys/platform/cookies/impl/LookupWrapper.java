/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.platform.cookies.impl;

import org.usefultoys.platform.cookies.spi.ContextFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import javax.swing.ActionMap;
import org.openide.util.Lookup;
import org.usefultoys.platform.cookies.spi.ContextFilter.FilterResult;
import static org.usefultoys.platform.cookies.spi.ContextFilter.FilterResult.ACCEPT;
import static org.usefultoys.platform.cookies.spi.ContextFilter.FilterResult.REJECT;

/**
 *
 * @author Daniel
 */
public class LookupWrapper extends Lookup {

    private final Lookup wrappedLookup;
    private final Collection<? extends ContextFilter> registeredFilters;

    public LookupWrapper(Lookup wrappedLookup) {
        this.wrappedLookup = wrappedLookup;
        this.registeredFilters = Lookup.getDefault().lookupAll(ContextFilter.class);
    }

    @Override
    public <T> T lookup(Class<T> clazz) {
        final T candidate = wrappedLookup.lookup(clazz);
        if (ActionMap.class.equals(clazz)) {
            return candidate;
        }
        return filter(candidate);
    }

    @Override
    public <T> Result<T> lookup(Template<T> template) {
        final Result<T> result = wrappedLookup.lookup(template);
        if (ActionMap.class.equals(template.getType())) {
            return result;
        }
        return new ResultWrapper<>(this, result);
    }

    <T> T filter(final T candidate) {
        for (ContextFilter filter : registeredFilters) {
            FilterResult filterResult = filter.visit(candidate);
            if (filterResult == null) {
                continue;
            }
            switch (filterResult) {
                case ACCEPT:
                    return candidate;
                case REJECT:
                    return null;
            }
        }
        return candidate;
    }

    <T> Collection<T> filter(final Collection<T> candidateCollection) {
        Collection<T> resultCollection = candidateCollection instanceof List ? new ArrayList<>() : new HashSet<>();
        nextCandidate:
        for (T candidate : candidateCollection) {
            if (ActionMap.class.equals(candidate.getClass())) {
                resultCollection.add(candidate);
                continue;
            }
            for (ContextFilter filter : registeredFilters) {
                FilterResult filterResult = filter.visit(candidate);
                if (filterResult == null) {
                    continue;
                }
                switch (filterResult) {
                    case ACCEPT:
                        resultCollection.add(candidate);
                        continue nextCandidate;
                    case REJECT:
                        continue nextCandidate;
                }
            }
            resultCollection.add(candidate);
        }
        return resultCollection;
    }

}
