/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.platform.dialog.notification;

import java.util.concurrent.TimeUnit;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;
import org.openide.util.RequestProcessor;

/**
 * Métodos para mostrar uma mensagem de notificação que desaparece após algum
 * tempo.
 *
 * @author x7ws
 */
public class NotificationUtil {

    private NotificationUtil() {
        // não permite instâncias
    }

    public static void scheduleNotificationHide(final Notification notification) {
        RequestProcessor.getDefault().schedule(new Runnable() {
            @Override
            public void run() {
                notification.clear();
            }
        }, 90, TimeUnit.SECONDS);
    }

    public static void debug(String titulo, String mensagem) {
        Notification notification = NotificationDisplayer.getDefault().notify(titulo,
                IconBundle.NOTIFICATION_PLAIN,
                mensagem,
                null);
        scheduleNotificationHide(notification);
    }

    public static void success(String titulo, String mensagem) {
        Notification notification = NotificationDisplayer.getDefault().notify(titulo,
                IconBundle.NOTIFICATION_SUCCESS,
                mensagem,
                null);
        scheduleNotificationHide(notification);
    }

    public static void info(String titulo, String mensagem) {
        Notification notification = NotificationDisplayer.getDefault().notify(titulo,
                IconBundle.NOTIFICATION_INFO,
                mensagem,
                null);
        scheduleNotificationHide(notification);
    }

    public static void warn(String titulo, String mensagem) {
        Notification notification = NotificationDisplayer.getDefault().notify(titulo,
                IconBundle.NOTIFICATION_WARN,
                mensagem,
                null);
        scheduleNotificationHide(notification);
    }

    public static void error(String titulo, String mensagem) {
        Notification notification = NotificationDisplayer.getDefault().notify(titulo,
                IconBundle.NOTIFICATION_ERROR,
                mensagem,
                null);
        scheduleNotificationHide(notification);
    }
}
