/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.cookies.core;

import org.openide.util.Lookup;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Daniel Felix Ferber
 */
public class DynamicProxyLookup extends ProxyLookup {
    public DynamicProxyLookup(Lookup... lookups) {
        super(lookups);
    }
    
    public DynamicProxyLookup setLookup(Lookup... lookup) {
        this.setLookups(lookup);
        return this;
    }

    public DynamicProxyLookup setLookup(Lookup lookup) {
        resetLookup();
        addLookup(lookup);
        return this;
    }

    public DynamicProxyLookup addLookup(Lookup lookup) {
        Lookup[] newLookup = null;
        Lookup[] currentLookup = getLookups();
        if ((currentLookup != null) && (currentLookup.length > 0)) {
            newLookup = new Lookup[currentLookup.length + 1];
            for (int i = newLookup.length - 2; i >= 0; i--) {
                newLookup[i] = currentLookup[i];
            }
            newLookup[currentLookup.length] = lookup;
        } else {
            newLookup = new Lookup[]{lookup};
        }
        if (newLookup != null) {
            setLookups(newLookup);
        }
        return this;
    }

    public DynamicProxyLookup removeLookup(Lookup lookup) {
        Lookup[] currentLookup = getLookups();
        if ((currentLookup != null) && (currentLookup.length > 0)) {
            int removedIndex = -1;
            for (int i = currentLookup.length - 1; i >= 0; i--) {
                if (currentLookup[i].equals(lookup)) {
                    removedIndex = i;
                    break;
                }
            }
            if (removedIndex > 0) {
                Lookup[] newLookup = new Lookup[currentLookup.length - 1];
                int newIndex = 0;
                for (int i = currentLookup.length - 1; i >= 0; i--) {
                    if (i != removedIndex) {
                        newLookup[newIndex] = currentLookup[i];
                        newIndex++;
                    }
                }
            }
        }
        return this;
    }

    public DynamicProxyLookup resetLookup() {
        setLookups(new Lookup[]{});
        return this;
    }
}
