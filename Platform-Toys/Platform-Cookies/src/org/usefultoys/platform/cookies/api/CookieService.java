package org.usefultoys.platform.cookies.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service that creates and manages {@link TopComponentCookieContext}s.
 *
 * @author Daniel Felix Ferber
 */
public interface CookieService {

//    org.openide.util.Lookup getGlobalContext();

    /**
     * Creates a new, empty {@link TopComponentCookieContext}.
     *
     * @return the {@link CookieService} itself.
     */
    TopComponentCookieContext createTopComponentCookieContext();

//    /**
//     * Repopulates all cookies withint the context.
//     *
//     * @return the {@link CookieService} itself.
//     */
//    CookieService update();

    /**
     * Repopulates only the static cookies withint the context.
     *
     * @return the {@link CookieService} itself.
     */
    CookieService updateStatic();

    /**
     * @return the default {@link CookieService} instance.
     */
    public static CookieService getDefault() {
        return org.openide.util.Lookup.getDefault().lookup(CookieService.class);
    }




}
