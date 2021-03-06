/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.usefultoys.netbeansrcp.platform.reporter.impl;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.usefultoys.netbeansrcp.platform.reporter.Report;

/**
 * Default implementation for {@link Report}.
 *
 * @author Daniel Felix Ferber
 */
public class ReportImpl implements Report {

    private static final ConcurrentMap<String, AtomicLong> positionByCategory = new ConcurrentHashMap<String, AtomicLong>();

    protected final String parentHash;
    protected final String hash;
    protected final String category;
    protected final String name;
    protected final long position;
    /**
     * A short, human readable, optional title of the execution.
     */
    protected String title = null;
    /**
     * An long, human readable, optional description of the execution.
     */
    protected String description = null;
    /**
     * When the execution started, or 0 if not yet started.
     */
    protected long startTime = 0;
    /**
     * When the execution stopped, or 0 if not yet stopped.
     */
    protected long stopTime = 0;
    /**
     * For successful execution, a string token that identifies the execution
     * path.
     */
    protected String pathId;
    /**
     * For cancelled.
     */
    protected boolean cancel = false;
    /**
     * For rejected execution, a string token that identifies the rejection
     * cause.
     */
    protected String rejectId;
    /**
     * For failed execution, the exception that caused the failure.
     */
    protected Throwable failThrowable = null;
    /**
     * How many iterations were already run by the execution.
     */
    protected long iteration = 0;
    /**
     * How many iterations are expected to run by the execution.
     */
    protected long expectedIterations = 0;
    /**
     * Additional meta data describing the job.
     */
    protected Map<String, String> context;

    public ReportImpl(String category, String name) {
        this(category, name, null);
    }

    private ReportImpl(String category, String name, String parentHash) {
        this.category = category;
        this.name = name;
        positionByCategory.computeIfAbsent(category, c -> new AtomicLong(0));
        final AtomicLong atomicLong = positionByCategory.get(category);
        atomicLong.compareAndSet(Long.MAX_VALUE, 0);
        this.position = atomicLong.incrementAndGet();
        if (name == null) {
            this.hash = category + '/' + position;
        } else {
            this.hash = category + '/' + name + '/' + position;
        }
        this.parentHash = parentHash;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public long getStopTime() {
        return stopTime;
    }

    @Override
    public boolean isStarted() {
        return startTime != 0;
    }

    @Override
    public boolean isStopped() {
        return stopTime != 0;
    }

    public boolean isCancel() {
        return cancel;
    }

    @Override
    public boolean isOK() {
        return ! cancel && (stopTime != 0) && (failThrowable == null && rejectId == null);
    }

    @Override
    public boolean isReject() {
        return ! cancel && (stopTime != 0) && (rejectId != null);
    }

    @Override
    public boolean isFail() {
        return ! cancel && (stopTime != 0) && (failThrowable != null);
    }

    @Override
    public long getExecutionTime() {
        if (startTime == 0) {
            return 0;
        } else if (stopTime == 0) {
            return System.nanoTime() - startTime;
        }
        return stopTime - startTime;
    }

    @Override
    public String getPathId() {
        return pathId;
    }

    @Override
    public String getRejectId() {
        return rejectId;
    }

    @Override
    public Throwable getFailThrowable() {
        return failThrowable;
    }

    @Override
    public long getCurrentIteration() {
        return iteration;
    }

    @Override
    public long getExpectedIterations() {
        return expectedIterations;
    }

    @Override
    public double getIterationsPerSecond() {
        if (iteration == 0 || startTime == 0) {
            return 0.0d;
        }
        final float executionTimeNS = getExecutionTime();
        if (executionTimeNS == 0) {
            return 0.0d;
        }
        return ((double) this.iteration) / executionTimeNS * 1000000000;
    }

    @Override
    public Map<String, String> getContext() {
        if (context == null) {
            return null;
        }
        return Collections.unmodifiableMap(context);
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    public String getParentHash() {
        return parentHash;
    }

    @Override
    public int hashCode() {
        return hash.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReportImpl other = (ReportImpl) obj;
        return this.hash.equals(other.hash);
    }
}
