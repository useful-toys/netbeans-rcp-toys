/*
 */
package br.com.danielferber.rcp.loggertoys.configuration;

import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;

/**
 *
 * Daniel Felix Ferber
 */
final class LoggerFileChangeListener implements FileChangeListener {

    @Override
    public void fileFolderCreated(final FileEvent fe) {
        /* ignore */
    }

    @Override
    public void fileDataCreated(final FileEvent fe) {
        /* ignore */
    }

    @Override
    public void fileChanged(final FileEvent fe) {
        /* ignore */
    }

    @Override
    public void fileDeleted(final FileEvent fe) {
        /* ignore */
    }

    @Override
    public void fileRenamed(final FileRenameEvent fe) {
        /* ignore */
    }

    @Override
    public void fileAttributeChanged(final FileAttributeEvent fe) {
        final Object newValue = fe.getNewValue();
        LoggerConfigurationUtils.setLoggerLevel(fe.getName(), newValue);
        LoggerConfigurationUtils.resetLogManager();
    }
}
