/*
 * Copyright 2026 Tapadyuti Chatterjee
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tapadyuti.bengalicalendar;

import com.tapadyuti.bengalicalendar.exception.BengaliCalendarException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Entry point for converting between Gregorian dates and Bengali calendar dates.
 * <p>
 * This utility class exposes factory-style methods for obtaining {@link BengaliDate} instances from
 * {@link LocalDate} values, converting Bengali dates back to Gregorian dates, and reading or changing the
 * process-wide default {@link BengaliCalendarSystem}. The default system is
 * {@link BengaliCalendarSystem#WEST_BENGAL_TRADITIONAL} unless overridden by the
 * {@code bengalicalendar.default.system} system property or by calling {@link #setDefault(BengaliCalendarSystem)}.
 * </p>
 * <p>
 * The class is thread-safe. Its mutable global state is limited to the default calendar system, which is stored in an
 * {@link AtomicReference}; all conversion methods are otherwise stateless.
 * </p>
 */
public final class BengaliCalendar {
    private static final AtomicReference<BengaliCalendarSystem> DEFAULT = new AtomicReference<>(BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL);

    static {
        String configured = System.getProperty("bengalicalendar.default.system");
        if (configured != null && !configured.isBlank()) {
            DEFAULT.set(parseSystem(configured));
        }
    }

    private BengaliCalendar() {
    }

    /**
     * Returns the process-wide default Bengali calendar system.
     *
     * @return the current default calendar system
     */
    public static BengaliCalendarSystem getDefault() {
        return DEFAULT.get();
    }

    /**
     * Sets the process-wide default Bengali calendar system used by overloads that do not receive a system explicitly.
     *
     * @param system the new default calendar system
     * @throws NullPointerException if {@code system} is {@code null}
     */
    public static void setDefault(BengaliCalendarSystem system) {
        DEFAULT.set(Objects.requireNonNull(system, "system"));
    }

    /**
     * Returns today's Bengali date using the system default time zone and the current default calendar system.
     *
     * @return today's date in the default Bengali calendar system
     */
    public static BengaliDate today() {
        return today(Clock.systemDefaultZone());
    }

    /**
     * Returns today's Bengali date using the system default time zone and the supplied calendar system.
     *
     * @param system the calendar system to use
     * @return today's date in the requested calendar system
     * @throws NullPointerException if {@code system} is {@code null}
     */
    public static BengaliDate today(BengaliCalendarSystem system) {
        return from(LocalDate.now(), system);
    }

    /**
     * Returns today's Bengali date using the supplied clock and the current default calendar system.
     *
     * @param clock the clock to query for the current date
     * @return today's date in the default Bengali calendar system
     * @throws NullPointerException if {@code clock} is {@code null}
     */
    public static BengaliDate today(Clock clock) {
        return from(LocalDate.now(clock), getDefault());
    }

    /**
     * Returns today's Bengali date for the supplied zone and the current default calendar system.
     *
     * @param zoneId the time zone used to obtain the current date
     * @return today's date in the default Bengali calendar system
     * @throws NullPointerException if {@code zoneId} is {@code null}
     */
    public static BengaliDate today(ZoneId zoneId) {
        return from(LocalDate.now(zoneId), getDefault());
    }

    /**
     * Returns today's Bengali date for the supplied zone and calendar system.
     *
     * @param zoneId the time zone used to obtain the current date
     * @param system the calendar system to use
     * @return today's date in the requested calendar system
     * @throws NullPointerException if either argument is {@code null}
     */
    public static BengaliDate today(ZoneId zoneId, BengaliCalendarSystem system) {
        return from(LocalDate.now(zoneId), system);
    }

    /**
     * Converts a Gregorian date into a Bengali date using the current default calendar system.
     *
     * @param date the Gregorian date to convert
     * @return the converted Bengali date
     * @throws NullPointerException if {@code date} is {@code null}
     */
    public static BengaliDate from(LocalDate date) {
        return from(date, getDefault());
    }

    /**
     * Converts a Gregorian date into a Bengali date using the supplied calendar system.
     *
     * @param date the Gregorian date to convert
     * @param system the Bengali calendar system to use for the conversion
     * @return the converted Bengali date
     * @throws NullPointerException if either argument is {@code null}
     */
    public static BengaliDate from(LocalDate date, BengaliCalendarSystem system) {
        Objects.requireNonNull(date, "date");
        Objects.requireNonNull(system, "system");
        return system == BengaliCalendarSystem.BANGLADESH_REVISED
            ? InternalSupport.fromBangladeshRevised(date)
            : InternalSupport.fromWestBengalTraditional(date);
    }

    /**
     * Converts a Bengali date into a Gregorian {@link LocalDate}.
     *
     * @param bengaliDate the Bengali date to convert
     * @return the corresponding Gregorian date
     * @throws NullPointerException if {@code bengaliDate} is {@code null}
     */
    public static LocalDate toGregorian(BengaliDate bengaliDate) {
        Objects.requireNonNull(bengaliDate, "bengaliDate");
        return bengaliDate.getCalendarSystem() == BengaliCalendarSystem.BANGLADESH_REVISED
            ? InternalSupport.toBangladeshRevised(bengaliDate)
            : InternalSupport.toWestBengalTraditional(bengaliDate);
    }

    private static BengaliCalendarSystem parseSystem(String value) {
        String normalized = value.trim();
        if (normalized.equalsIgnoreCase("BANGLADESH_REVISED") || normalized.equalsIgnoreCase("BD")) {
            return BengaliCalendarSystem.BANGLADESH_REVISED;
        }
        if (normalized.equalsIgnoreCase("WEST_BENGAL_TRADITIONAL") || normalized.equalsIgnoreCase("WB")) {
            return BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL;
        }
        throw new BengaliCalendarException("Unsupported calendar system: " + value);
    }
}
