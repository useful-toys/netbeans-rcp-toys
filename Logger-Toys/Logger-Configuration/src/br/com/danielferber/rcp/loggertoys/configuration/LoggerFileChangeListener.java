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
 * @author x7ws - Daniel Felix Ferber
 */
final class LoggerFileChangeListener implements FileChangeListener {

    private final FileObject rootFo;

    public LoggerFileChangeListener(final FileObject rootFo) {
        this.rootFo = rootFo;
    }

    @Override
    public void fileFolderCreated(final FileEvent fe) {
        /* ignorar */
    }

    @Override
    public void fileDataCreated(final FileEvent fe) {
        /* ignorar */
    }

    @Override
    public void fileChanged(final FileEvent fe) {
        /* ignorar */
    }

    @Override
    public void fileDeleted(final FileEvent fe) {
        /* ignorar */
    }

    @Override
    public void fileRenamed(final FileRenameEvent fe) {
        /* ignorar */
    }

    @Override
    public void fileAttributeChanged(final FileAttributeEvent fe) {
        final Object newValue = fe.getNewValue();
        setLoggerLevel(fe.getName(), newValue);
        resetLogManager();
    }

    public void redefinirLoggers() {
        Logger rootLogger = Logger.getLogger("");
        for (Handler handler : rootLogger.getHandlers()) {
            handler.setLevel(Level.ALL);
        }

        final Enumeration<String> attributes = rootFo.getAttributes();
        while (attributes.hasMoreElements()) {
            final String nome = attributes.nextElement();
            final Object valor = rootFo.getAttribute(nome);
            setLoggerLevel(nome, (String) valor);
        }
        resetLogManager();
    }

    public void setLoggerLevel(final String nome, final Object valor) {
        try {
            if (valor instanceof String) {
                final String loggerName = (String) valor;
                final Logger logger = Logger.getLogger(nome);
                final String levelName = loggerName.toUpperCase();

                final Level level;
                try {
                    level = Level.parse(levelName);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Logger level not listed in Level enum. name=" + nome + " stringvalue=" + levelName, e);
                }

                try {
                    logger.setLevel(level);
                    System.setProperty(nome + ".level", levelName);
                } catch (SecurityException e) {
                    throw new IllegalArgumentException("Not allowed to change logger configuration. name=" + nome + " stringvalue=" + levelName, e);
                }
            } else {
                throw new IllegalArgumentException("Attribute values must be string. name=" + nome);
            }
        } catch (Exception e) {
            Logger.getLogger("").log(Level.WARNING, "Incorrect logger configuration.", e);
        }
    }

    public void resetLogManager() {
        try {
            LogManager.getLogManager().readConfiguration();
        } catch (IOException e) {
            Logger.getLogger("").log(Level.WARNING, "Falha ao redefinir loggers.", e);
        } catch (SecurityException e) {
            Logger.getLogger("").log(Level.WARNING, "Permissão recusada para reconfigurar loggers.", e);
        }
    }
}
