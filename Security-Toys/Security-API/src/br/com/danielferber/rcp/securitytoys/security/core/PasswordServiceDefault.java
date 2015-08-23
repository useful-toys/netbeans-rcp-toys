/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.danielferber.rcp.securitytoys.security.core;

import br.com.danielferber.rcp.securitytoys.security.api.PasswordException;
import br.com.danielferber.rcp.securitytoys.security.api.PasswordService;
import br.com.danielferber.rcp.securitytoys.security.spi.PasswordIntegration;
import java.nio.CharBuffer;
import java.util.regex.Pattern;

public class PasswordServiceDefault implements PasswordService {

    @Override
    public boolean canChangePassword(String login) {
        final PasswordIntegration integration = lookupPasswordIntegration();
        return integration.canChangePassword(login);
    }

    @Override
    public boolean changePassword(String login, char[] oldPassword, char[] newPassword) throws PasswordException {
        final PasswordIntegration integration = lookupPasswordIntegration();
        return integration.changePassword(login, oldPassword, newPassword);
    }

    private static final Pattern[] partialRegexChecks = {
        Pattern.compile(".*[a-z]+.*"), // lower
        Pattern.compile(".*[A-Z]+.*"), // upper
        Pattern.compile(".*[\\d]+.*"), // digits
        Pattern.compile(".*[@#$%]+.*") // symbols
    };

    @Override
    public int vefiryPasswordStrength(String login, char[] password) {
        int strengthPercentage = 0;
        final CharSequence sequence = CharBuffer.wrap(password);

        for (final Pattern pattern : partialRegexChecks) {
            if (pattern.matcher(sequence).matches()) {
                strengthPercentage++;
            }
        }
        return strengthPercentage;
    }
    
     protected PasswordIntegration lookupPasswordIntegration() {
        final PasswordIntegration lookup = org.openide.util.Lookup.getDefault().lookup(PasswordIntegration.class);
        if (lookup == null) {
            throw new IllegalStateException("Nenhum módulo provê PasswordIntegration.");
        }
        return lookup;
    }
}
