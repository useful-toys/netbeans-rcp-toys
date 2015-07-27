/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.platform.layer.impl;

import org.usefultoys.rcp.platform.layer.DynamicLayer;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.netbeans.api.sendopts.CommandException;
import org.netbeans.spi.sendopts.Arg;
import org.netbeans.spi.sendopts.ArgsProcessor;
import org.netbeans.spi.sendopts.Env;
import org.openide.filesystems.FileSystem;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

/**
 *
 * @author X7WS
 */
@ServiceProviders(value = {
    @ServiceProvider(service = ArgsProcessor.class),
    @ServiceProvider(service = FileSystem.class)}
)
public class ArgumentUrlLayer extends DynamicLayer implements ArgsProcessor {

    public static final Logger logger = Logger.getLogger(ArgumentUrlLayer.class.getName());

    @Arg(longName = "layerUrl")
    public String layerUrlString;

    @Override
    public void process(Env env) throws CommandException {
        logger.log(Level.INFO, "layerUrl={0}", layerUrlString);
        if (layerUrlString != null) {
            try {
                URL url = validateUrl(layerUrlString);
                addFileSystemUrl(url);
            } catch (IllegalArgumentException e) {
                logger.log(Level.SEVERE, "Failed to add layer file. layerUrl=" + layerUrlString + "'", e);
            }
        }
    }
}
