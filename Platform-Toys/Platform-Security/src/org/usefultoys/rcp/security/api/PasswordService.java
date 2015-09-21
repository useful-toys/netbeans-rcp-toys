/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.security.api;

/**
 *
 * @author Daniel Felix Ferber
 */
public interface PasswordService {
    boolean canChangePassword(String login);
    boolean changePassword(String login, char [] oldPassword, char [] newPassword) throws PasswordException;
    int vefiryPasswordStrength(String login, char [] password);
}
