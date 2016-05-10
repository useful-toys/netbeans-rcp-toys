/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.reporter;

/**
 *
 * @author x7ws
 */
public interface Reporter {

    public static enum Severity {
        Error, Warning, Info, None
    }

//    Report createReport(String operationName);
//
    Reporter title(String title);

    Reporter description(String description);
//    
//    Report message(String message);
//    
//    Report ctx(String name);
//
//    Report ctx(final boolean condition, final String trueName);
//
//    Report ctx(final boolean condition, final String trueName, final String falseName);
//
//    Report ctx(final String name, final int value);
//
//    Report ctx(final String name, final long value);
//
//    Report ctx(final String name, final boolean value);
//
//    Report ctx(final String name, final float value);
//
//    Report ctx(final String name, final double value);
//
//    Report ctx(final String name, final String value);
//
//    Report ctx(final String name, final String format, final Object... args);
//
//    Report ctx(final String name, final Object object);
//
//    Report unctx(final String name);
//
//    Report cancel();
//
//    Report inc();
//
//    Report incBy(final long increment);
//
//    Report incTo(final long currentIteration);

    Reporter start();

    Reporter path(Object pathId);

    Reporter ok();

    Reporter ok(Object pathId);

    Reporter reject(Object cause);

    Reporter fail(final Throwable cause);

    
}
