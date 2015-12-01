/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.cookies.impl;

import org.usefultoys.netbeansrcp.platform.cookies.api.CookieService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;
import org.usefultoys.netbeansrcp.platform.cookies.api.TopComponentCookieContext;
import org.usefultoys.netbeansrcp.platform.cookies.spi.StaticCookieProvider;

/**
 *
 * @author Daniel Felix Ferber
 */
@ServiceProvider(service = CookieService.class)
public class CookieServiceImpl implements CookieService {

    private static final Logger logger = Logger.getLogger(CookieService.class.getName());

    private final InstanceContent staticContent = new InstanceContent();
    private final org.openide.util.Lookup staticLookup = new AbstractLookup(staticContent);

    public CookieServiceImpl() {
        super();
    }

    org.openide.util.Lookup getStaticLookup() {
        return staticLookup;
    }

    @Override
    public TopComponentCookieContext createTopComponentCookieContext() {
        return new TopComponentCookieContextImpl(this);
    }

    @Override
    public CookieService updateStatic() {
        Collection<? extends StaticCookieProvider> providers = org.openide.util.Lookup.getDefault().lookupAll(StaticCookieProvider.class);
        List<Object> cookiesRepository = new ArrayList<>();
        boolean cookiesCreated = false;
        for (StaticCookieProvider provider : providers) {
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
}
