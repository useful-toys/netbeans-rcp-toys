/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.layer;

import org.usefultoys.netbeansrcp.platform.layer.DynamicLayer;
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
