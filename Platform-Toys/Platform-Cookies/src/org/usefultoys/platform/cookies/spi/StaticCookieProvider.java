package org.usefultoys.platform.cookies.spi;

import java.util.List;

/**
 * Callback interface that populates the context with static cookies. Static
 * cookies are always available and do not depend on TopCompnent or
 * selections.
 */
public interface StaticCookieProvider {

    /**
     * Callback method that adds static cookies to the context.
     *
     * @param context Current context where cookies may be added to
     * @return true if cookies were added to the context
     */
    boolean createStaticCookies(List<Object> context);
    
}
