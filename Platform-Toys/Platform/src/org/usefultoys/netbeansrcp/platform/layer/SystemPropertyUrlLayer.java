/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Daniel Felix Ferber
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.usefultoys.netbeansrcp.platform.layer;

import java.net.URL;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.filesystems.Repository;
import org.openide.filesystems.Repository.LayerProvider;
import org.openide.util.lookup.ServiceProvider;

/**
 * Configures an additional layer file given by url from the 'layer.url' system property. For webstart compatibility,
 * the 'jnlp.layer.url' system property is also checked.
 * <p>
 * This feature is implemented as a LayerProvider, which is called while the system file system is configured by the
 * platform in order to provide URLs to additional layer files.
 *
 * @author Daniel Felix Ferber
 */
@ServiceProvider(service = LayerProvider.class)
public class SystemPropertyUrlLayer extends Repository.LayerProvider {

    public static final Logger logger = Logger.getLogger(SystemPropertyUrlLayer.class.getName());
    public static final String LAYER_URL_JNLP_SYSTEM_PROPERTY = "jnlp.layer.url";
    public static final String LAYER_URL_SYSTEM_PROPERTY = "layer.url";

    @Override
    protected void registerLayers(Collection<? super URL> context) {
        final String layerUrlString = System.getProperty(LAYER_URL_SYSTEM_PROPERTY);
        final String layerUrlJnlpString = System.getProperty(LAYER_URL_JNLP_SYSTEM_PROPERTY);
        logger.log(Level.INFO, LAYER_URL_SYSTEM_PROPERTY + "={0}", layerUrlString);
        logger.log(Level.INFO, LAYER_URL_JNLP_SYSTEM_PROPERTY + "={0}", layerUrlJnlpString);
        addUrl(layerUrlString, context);
        addUrl(layerUrlJnlpString, context);
    }

    static void addUrl(final String layerUrlString, Collection<? super URL> context) throws IllegalStateException {
        try {
            if (layerUrlString == null) {
                return;
            }
            URL url = DynamicLayer.validateUrl(layerUrlString);
            context.add(url);
        } catch (IllegalStateException e) {
            logger.log(Level.SEVERE, "Failed to add layer file. layerUrl=" + layerUrlString + "'", e);
        }
    }
}
