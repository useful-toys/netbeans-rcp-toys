/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.platform.dialog.notification;

import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Daniel Felix Ferber
 */
public class IconBundle {
    public static final ImageIcon NOTIFICATION_INFO = ImageUtilities.loadImageIcon("org/usefultoys/rcp/platform/dialog/notification/NotificationInfo.png", true);
    public static final ImageIcon NOTIFICATION_WARN = ImageUtilities.loadImageIcon("org/usefultoys/rcp/platform/dialog/notification/NotificationWarn.png", true);
    public static final ImageIcon NOTIFICATION_ERROR = ImageUtilities.loadImageIcon("org/usefultoys/rcp/platform/dialog/notification/NotificationError.png", true);
    public static final ImageIcon NOTIFICATION_PLAIN = ImageUtilities.loadImageIcon("org/usefultoys/rcp/platform/dialog/notification/NotificationPlain.png", true);
    public static final ImageIcon NOTIFICATION_SUCCESS = ImageUtilities.loadImageIcon("org/usefultoys/rcp/platform/dialog/notification/NotificationSuccess.png", true);
}
