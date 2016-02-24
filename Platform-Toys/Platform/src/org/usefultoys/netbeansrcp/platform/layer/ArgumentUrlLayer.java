/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Daniel Felix Ferber
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
 * Command line argument processor that loads the resource given by the
 * --layerUrl <path> argument into the Netbeans RCP system filesystem. This
 * allows adding extra layer,xml files as configuration files hosted outside of
 * your application deployment.
 * <br>Warning: The command line processor runs during platform initialization,
 * in parallel to other module installers or runnable annotated with @OnStart.
 * Therefore, the resource may not have been loaded into the system filesystem
 * yet. Such installers or runnables must not relay on configuration provided by
 * the resource.
 *
 * @author Daniel Felix Ferber
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
                logger.log(Level.SEVERE, "Failed to add layer file. layerUrl='" + layerUrlString + "'", e);
            }
        }
    }
}
