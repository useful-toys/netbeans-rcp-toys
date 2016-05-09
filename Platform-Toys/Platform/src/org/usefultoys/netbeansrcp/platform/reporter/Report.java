/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.reporter;

import java.util.Map;

/**
 *
 * @author x7ws
 */
public interface Report {

    String getParentHash();
    
    String getHash();

    long getPosition();
    
    String getCategory();

    String getName();

    String getTitle();

    String getDescription();

    String getPathId();

    String getRejectId();

    Throwable getFailThrowable();

    long getStartTime();

    long getStopTime();

    boolean isStarted();

    boolean isStopped();

    public boolean isOK();

    public boolean isReject();

    public boolean isFail();

    long getExecutionTime();

    long getCurrentIteration();

    long getExpectedIterations();

    double getIterationsPerSecond();

    Map<String, String> getContext();
}
