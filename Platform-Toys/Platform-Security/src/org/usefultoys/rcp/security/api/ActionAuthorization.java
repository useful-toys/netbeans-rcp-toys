/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.rcp.security.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Daniel
 */
@Retention(value = RetentionPolicy.SOURCE)
@Target(value = {ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface ActionAuthorization {
    public String value();
    public String [] anyResource();
    public String resource();
}
