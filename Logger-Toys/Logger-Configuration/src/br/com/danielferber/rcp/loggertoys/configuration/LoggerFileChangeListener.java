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
        if (newValue instanceof String) {
            setLoggerLevel(fe.getName(), (String) newValue);
        }
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
            if (valor instanceof String) {
                setLoggerLevel(nome, (String) valor);
            }
        }
        resetLogManager();
    }

    public void setLoggerLevel(final String nome, final String nomeLevel) {
        final Logger logger = Logger.getLogger(nome);
        final String levelNameUpr = nomeLevel.toUpperCase();
        try {
            final Level level = Level.parse(levelNameUpr);
            logger.setLevel(level);
            System.setProperty(nome + ".level", levelNameUpr);
        } catch (IllegalArgumentException e) {
            Logger.getLogger("").log(Level.WARNING, "Parâmetro inválido. attr=" + nome + " stringvalue=" + levelNameUpr, e);
        } catch (SecurityException e) {
            Logger.getLogger("").log(Level.WARNING, "Permissão recusada para configurar logger. attr=" + nome + " stringvalue=" + levelNameUpr, e);
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
