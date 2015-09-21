package org.usefultoys.rcp.platform.dialog.notification;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Métodos para apresentar dialógos de informação, pergunta e erro.
 *
 * @author x7ws
 */
public class MessageUtil {

    private MessageUtil() {
    }

    public static void info(String titulo, String mensagem) {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor(mensagem, titulo, NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.INFORMATION_MESSAGE, new Object[]{NotifyDescriptor.OK_OPTION}, NotifyDescriptor.OK_OPTION));
    }

    public static void error(String titulo, String mensagem) {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor(mensagem, titulo, NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.ERROR_MESSAGE, new Object[]{NotifyDescriptor.OK_OPTION}, NotifyDescriptor.OK_OPTION));
    }

    public static void warn(String titulo, String mensagem) {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor(mensagem, titulo, NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.WARNING_MESSAGE, new Object[]{NotifyDescriptor.OK_OPTION}, NotifyDescriptor.OK_OPTION));
    }

    public static Object customQuestion(String titulo, String mensagem, String... opcoes) {
        return DialogDisplayer.getDefault().notify(new NotifyDescriptor(mensagem, titulo, NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.QUESTION_MESSAGE, opcoes, null));
    }

    public static Boolean yesNoCancelQuestion(String titulo, String mensagem) {
        final Object result = DialogDisplayer.getDefault().notify(new NotifyDescriptor(mensagem, titulo, NotifyDescriptor.YES_NO_CANCEL_OPTION, NotifyDescriptor.QUESTION_MESSAGE, null, null));
        if (result == DialogDescriptor.CANCEL_OPTION) {
            return null;
        } else 
        if (result == DialogDescriptor.YES_OPTION) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public static boolean yesNoQuestion(String titulo, String mensagem) {
        return DialogDisplayer.getDefault().notify(new NotifyDescriptor(mensagem, titulo, NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.QUESTION_MESSAGE, null, null)) == DialogDescriptor.YES_OPTION;
    }

    public static void plain(String titulo, String mensagem) {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor(mensagem, titulo, NotifyDescriptor.DEFAULT_OPTION, NotifyDescriptor.PLAIN_MESSAGE, new Object[]{NotifyDescriptor.OK_OPTION}, NotifyDescriptor.OK_OPTION));
    }
}
