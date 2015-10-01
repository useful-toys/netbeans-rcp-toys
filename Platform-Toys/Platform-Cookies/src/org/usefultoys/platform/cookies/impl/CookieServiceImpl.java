/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.platform.cookies.impl;

import org.usefultoys.platform.cookies.api.CookieService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;
import org.usefultoys.platform.cookies.api.CookieContext;
import org.usefultoys.platform.cookies.core.DynamicProxyLookup;

/**
 *
 * @author Daniel Felix Ferber
 */
@ServiceProvider(service = CookieService.class)
public class CookieServiceImpl implements CookieService {

    private static final Logger logger = Logger.getLogger(CookieService.class.getName());

    private final InstanceContent staticContent = new InstanceContent();
    private final org.openide.util.Lookup staticLookup = new AbstractLookup(staticContent);

    private CookieContextImpl currentCookieContext = null;

    private final DynamicProxyLookup globalLookup = new DynamicProxyLookup(staticLookup);
//    private final org.openide.util.Lookup.Result<Object> globalLookupResult = globalLookup.lookupResult(Object.class);

//    private InstanceContent exportedContent = new InstanceContent();
//    private final org.openide.util.Lookup exportedLookup = new AbstractLookup(exportedContent);

//    private InstanceContent localContent = new InstanceContent();
//    private final org.openide.util.Lookup localLookup = new AbstractLookup(localContent);

//    final LookupListener globalLookupListener = new LookupListener() {
//        @Override
//        public void resultChanged(LookupEvent ev) {
//            if (localContent != null) {
//                final Collection<? extends Object> allInstances = globalLookupResult.allInstances();
//                logger.info("Copy " + allInstances.size() + " objects");
//                localContent.set(allInstances, null);
//            }
//        }
//    };

//    final LookupListener globalLookupListener2 = new LookupListener() {
//        @Override
//        public void resultChanged(LookupEvent ev) {
//            final Collection<? extends Object> allInstances = globalLookupResult.allInstances();
//            logger.info("Export " + allInstances.size() + " objects");
//            exportedContent.set(allInstances, null);
//        }
//    };

    public CookieServiceImpl() {
        super();
//        globalLookupResult.addLookupListener(globalLookupListener);
//        globalLookupResult.addLookupListener(globalLookupListener2);
    }

    org.openide.util.Lookup getStaticLookup() {
        return staticLookup;
    }

    @Override
    public org.openide.util.Lookup getGlobalContext() {
        return globalLookup;
    }

//    @Override
//    public org.openide.util.Lookup getLocalContext() {
//        return localLookup;
//    }

    @Override
    public CookieContext createCookieContext() {
        return new CookieContextImpl(this);
    }

    void activate(CookieContextImpl localCookieContext) {
        this.currentCookieContext = localCookieContext;
        redirectLookups();
    }

    void deactivate() {
        this.currentCookieContext = null;
        redirectLookups();
    }

    @Override
    public CookieService update() {
        updateStaticCookies();
        if (this.currentCookieContext != null) {
            this.currentCookieContext.update();
        }
        return this;
    }

    @Override
    public CookieService updateStatic() {
        updateStaticCookies();
        return this;
    }

    CookieServiceImpl updateStaticCookies() {
        Collection<? extends CookieService.StaticCookieProvider> providers = org.openide.util.Lookup.getDefault().lookupAll(CookieService.StaticCookieProvider.class);
        List<Object> cookiesRepository = new ArrayList<>();
        boolean cookiesCreated = false;
        for (CookieService.StaticCookieProvider provider : providers) {
            cookiesCreated |= provider.createStaticCookies(cookiesRepository);
        }
        if (cookiesCreated) {
            logger.info("Set " + cookiesRepository.size() + " static cookies");
            staticContent.set(cookiesRepository, null);
        } else {
            logger.info("Set zero static cookies");
            staticContent.set(Collections.EMPTY_LIST, null);
        }
        return this;
    }

    void redirectLookups() {
        if (this.currentCookieContext == null) {
            globalLookup.setLookup(staticLookup);
        } else {
            globalLookup.setLookup(staticLookup, this.currentCookieContext.getLocalLookup());   
        }        
    }

}
