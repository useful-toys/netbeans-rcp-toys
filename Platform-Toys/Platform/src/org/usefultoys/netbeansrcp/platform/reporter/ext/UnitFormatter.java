/*
 * Copyright 2015 Daniel Felix Ferber.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.usefultoys.netbeansrcp.platform.reporter.ext;

/**
 * Collection of methods to format numbers, rounding them to an unit that reduces their string representation.
 *
 * @author Daniel Felix Ferber
 */
public final class UnitFormatter {

    private UnitFormatter() {
        // prevent instances
    }

    private static final int[] TIME_FACTORS = new int[]{1000, 1000, 1000, 60, 60};
    private static final String[] TIME_UNITS = new String[]{"ns", "us", "ms", "s", "m", "h"};
    private static final String[] MEMORY_UNITS = new String[]{"B", "kB", "MB", "GB"};
    private static final int[] MEMORY_FACTORS = new int[]{1000, 1000, 1000};
    private static final String[] ITERATIONS_PER_TIME_UNITS = new String[]{"/s", "k/s", "M/s"};
    private static final int[] ITERATIONS_PER_TIME_FACTORS = new int[]{1000, 1000, 1000};
    private static final String[] ITERATIONS_UNITS = new String[]{"", "k", "M"};
    private static final int[] ITERATIONS_FACTORS = new int[]{1000, 1000, 1000};

    static String longUnit(long value, final String[] units, final int[] factors) {
        int index = 0;
        final int limit = factors[index] + factors[index] / 10;
        if (value < limit) {
            return String.format("%d%s", value, units[index]);
        }

        final int length = factors.length;
        double doubleValue = value;

        while (index < length && value >= (factors[index] + factors[index] / 10)) {
            doubleValue = (double) value / (double) factors[index];
            value /= factors[index];
            index++;
        }
        return String.format("%.1f%s", doubleValue, units[index]);
    }

    static final double Epsylon = 0.001;

    static String doubleUnit(double value, final String[] units, final int[] factors) {
        if (value == 0.0) {
            return "0" + units[0];
        }

        int index = 0;
        final int length = factors.length;

        while (index < length && (value + Epsylon) >= (factors[index] + factors[index] / 10)) {
            value /= factors[index];
            index++;
        }
        return String.format("%.1f%s", value, units[index]);
    }

    /**
     * Format number of bytes as string.
     * @param value number of bytes
     * @return formatted string
     */
    public static String bytes(final long value) {
        return longUnit(value, MEMORY_UNITS, MEMORY_FACTORS);
    }

    /**
     * Format number of nanoseconds as string.
     * @param value number of nanoseconds
     * @return formatted string
     */
    public static String nanoseconds(final long value) {
        return longUnit(value, TIME_UNITS, TIME_FACTORS);
    }

    /**
     * Format number of nanoseconds as string.
     * @param value number of nanoseconds
     * @return formatted string
     */
    public static String nanoseconds(final double value) {
        return doubleUnit(value, TIME_UNITS, TIME_FACTORS);
    }

    /**
     * Format number of iterations as string.
     * @param value number of iterations
     * @return formatted string
     */
    public static String iterations(final long value) {
        return longUnit(value, ITERATIONS_UNITS, ITERATIONS_FACTORS);
    }

    /**
     * Format number of iterations as string.
     * @param value number of iterations
     * @return formatted string
     */
    public static String iterations(final double value) {
        return doubleUnit(value, ITERATIONS_UNITS, ITERATIONS_FACTORS);
    }

    /**
     * Format number of iterations/second as string.
     * @param value number of iterations
     * @return formatted string
     */
    public static String iterationsPerSecond(final long value) {
        return longUnit(value, ITERATIONS_PER_TIME_UNITS, ITERATIONS_PER_TIME_FACTORS);
    }

    /**
     * Format number of iterations/second as string.
     * @param value number of iterations
     * @return formatted string
     */
    public static String iterationsPerSecond(final double value) {
        return doubleUnit(value, ITERATIONS_PER_TIME_UNITS, ITERATIONS_PER_TIME_FACTORS);
    }
}
