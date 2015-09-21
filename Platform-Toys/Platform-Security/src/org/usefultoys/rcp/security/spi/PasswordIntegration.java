/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.security.spi;

import org.usefultoys.rcp.security.api.PasswordException;

/**
 *
 * @author Daniel Felix Ferber
 */
public interface PasswordIntegration {

    boolean canChangePassword(String login);

    boolean changePassword(String login, char[] oldPassword, char[] newPassword) throws PasswordException;
}
