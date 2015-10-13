/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.platform.cookies.spi;

/**
 *
 * @author Daniel
 */
public interface ContextFilter {

    public static enum FilterResult {

        ACCEPT, REJECT, CONTINUE
    }

    <T> FilterResult visit(T object);

}
