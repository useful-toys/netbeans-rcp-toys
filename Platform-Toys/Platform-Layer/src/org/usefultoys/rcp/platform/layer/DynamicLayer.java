/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.platform.layer;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.MultiFileSystem;
import org.openide.filesystems.XMLFileSystem;
import org.openide.util.lookup.ServiceProvider;
import org.xml.sax.SAXException;

/**
 * @see http://wiki.netbeans.org/DevFaqDynamicSystemFilesystem
 * @author Daniel Felix Ferber - X7WS
 */
@ServiceProvider(service = FileSystem.class)
public class DynamicLayer extends MultiFileSystem {

    private static final DynamicLayer DEFAULT = new DynamicLayer();

    public static URL validateUrl(final String layerUrlString) throws IllegalStateException {
        final URL url;
        try {
            URL url2 = new URL(layerUrlString);
            URI uri = new URI(url2.getProtocol(), url2.getUserInfo(), url2.getHost(), url2.getPort(), url2.getPath(), url2.getQuery(), url2.getRef());
            url = uri.toURL();
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Invalid URL.", ex);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid URL.", ex);
        }
        try {
            final InputStream is = url.openStream();
            final byte[] buffer = new byte[1000];
            while (-1 != is.read(buffer)) {
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("URL references an inaccessible URL.", ex);
        }
        return url;
    }

    public DynamicLayer() {
        // will be created on startup, exactly once
        setPropagateMasks(true); // permit *_hidden masks to be used
    }

    public static DynamicLayer getDefault() {
        return DEFAULT;
    }

    public void addFileSystem(FileSystem fs) {
        FileSystem[] delegates = getDelegates();
        FileSystem[] newDelegates = Arrays.copyOf(delegates, delegates.length + 1);
        newDelegates[newDelegates.length - 1] = fs;
        setDelegates(newDelegates);
    }

    public void addFileSystemUrl(URL url) {
        try {
            addFileSystem(new XMLFileSystem(url));
        } catch (SAXException ex) {
            throw new IllegalArgumentException("URL references an invalid XML document.", ex);
        }
    }
}
