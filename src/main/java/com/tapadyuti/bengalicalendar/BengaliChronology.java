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

import java.time.Clock;
import java.time.LocalDate;
import java.time.chrono.AbstractChronology;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.Era;
import java.time.chrono.IsoEra;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.ValueRange;
import java.util.List;
import java.util.Map;

/**
 * {@link Chronology} implementation for Bengali calendar dates.
 * <p>
 * This class bridges {@link BengaliDate} with the {@code java.time.chrono} SPI so that callers can resolve the
 * chronology by id, create chronology-aware dates, and work with chronology-specific periods.
 * </p>
 */
public final class BengaliChronology extends AbstractChronology {
    /** Recommended shared instance. */
    public static final BengaliChronology INSTANCE = new BengaliChronology();

    public BengaliChronology() {
    }

    /**
     * Returns the chronology identifier.
     *
     * @return {@code "Bengali"}
     */
    @Override
    public String getId() {
        return "Bengali";
    }

    /**
     * Returns the calendar type identifier.
     *
     * @return {@code "bengali"}
     */
    @Override
    public String getCalendarType() {
        return "bengali";
    }

    /**
     * Returns today's date in this chronology using the default zone and default Bengali calendar system.
     *
     * @return today's Bengali date
     */
    @Override
    public ChronoLocalDate dateNow() {
        return BengaliCalendar.today();
    }

    /**
     * Returns today's date in this chronology for the supplied zone.
     *
     * @param zone the zone to use
     * @return today's Bengali date
     */
    @Override
    public ChronoLocalDate dateNow(java.time.ZoneId zone) {
        return BengaliCalendar.today(zone);
    }

    /**
     * Returns today's date in this chronology for the supplied clock.
     *
     * @param clock the clock to query
     * @return today's Bengali date
     */
    @Override
    public ChronoLocalDate dateNow(Clock clock) {
        return BengaliCalendar.today(clock);
    }

    /**
     * Creates a Bengali date from year, month, and day values using the current default calendar system.
     *
     * @param prolepticYear the Bengali year
     * @param month the 1-based Bengali month number
     * @param dayOfMonth the day of month
     * @return the resulting chronology date
     */
    @Override
    public ChronoLocalDate date(int prolepticYear, int month, int dayOfMonth) {
        return BengaliDate.of(prolepticYear, BengaliMonth.of(month), dayOfMonth, BengaliCalendar.getDefault());
    }

    /**
     * Creates a Bengali date from an ISO epoch day using the current default calendar system.
     *
     * @param epochDay the ISO epoch day
     * @return the resulting chronology date
     */
    @Override
    public ChronoLocalDate dateEpochDay(long epochDay) {
        return BengaliCalendar.from(LocalDate.ofEpochDay(epochDay), BengaliCalendar.getDefault());
    }

    /**
     * Creates a Bengali date from another temporal accessor using the current default calendar system.
     *
     * @param temporal the temporal to convert
     * @return the resulting chronology date
     */
    @Override
    public ChronoLocalDate date(TemporalAccessor temporal) {
        LocalDate iso = LocalDate.from(temporal);
        return BengaliCalendar.from(iso, BengaliCalendar.getDefault());
    }

    /**
     * Returns whether the supplied Bengali year is a leap year under the current default calendar system.
     *
     * @param prolepticYear the Bengali year to test
     * @return {@code true} if the year length exceeds {@code 365} days
     */
    @Override
    public boolean isLeapYear(long prolepticYear) {
        return BengaliDate.yearLength((int) prolepticYear, BengaliCalendar.getDefault()) > 365;
    }

    /**
     * Resolves a date from parsed field values.
     *
     * @param fieldValues the parsed temporal fields
     * @param resolverStyle the requested resolver style
     * @return the resolved date
     */
    @Override
    public ChronoLocalDate resolveDate(Map<TemporalField, Long> fieldValues, ResolverStyle resolverStyle) {
        long year = fieldValues.getOrDefault(ChronoField.YEAR, 1L);
        long month = fieldValues.getOrDefault(ChronoField.MONTH_OF_YEAR, 1L);
        long day = fieldValues.getOrDefault(ChronoField.DAY_OF_MONTH, 1L);
        return BengaliDate.of((int) year, BengaliMonth.of((int) month), (int) day, BengaliCalendar.getDefault());
    }

    /**
     * Returns the supported value range for a chronology field.
     *
     * @param field the field to query
     * @return the valid range for the field
     */
    @Override
    public ValueRange range(ChronoField field) {
        return switch (field) {
            case YEAR -> ValueRange.of(1, 999999);
            case MONTH_OF_YEAR -> ValueRange.of(1, 12);
            case DAY_OF_MONTH -> ValueRange.of(1, 31);
            case DAY_OF_YEAR -> ValueRange.of(1, 366);
            case ERA -> ValueRange.of(1, 1);
            default -> field.range();
        };
    }

    /**
     * Creates a Bengali date from a year and day-of-year using the current default calendar system.
     *
     * @param year the Bengali year
     * @param dayOfYear the 1-based day of year
     * @return the resulting chronology date
     */
    @Override
    public ChronoLocalDate dateYearDay(int year, int dayOfYear) {
        return BengaliDate.of(year, 1, 1, BengaliCalendar.getDefault()).with(ChronoField.DAY_OF_YEAR, dayOfYear);
    }

    /**
     * Creates a Bengali date from an era, year-of-era, and day-of-year.
     *
     * @param era the era
     * @param yearOfEra the year within the era
     * @param dayOfYear the 1-based day of year
     * @return the resulting chronology date
     */
    @Override
    public ChronoLocalDate dateYearDay(Era era, int yearOfEra, int dayOfYear) {
        int year = prolepticYear(era, yearOfEra);
        return dateYearDay(year, dayOfYear);
    }

    /**
     * Converts an era and year-of-era to a proleptic year.
     *
     * @param era the era
     * @param yearOfEra the year within the era
     * @return the proleptic year
     */
    @Override
    public int prolepticYear(Era era, int yearOfEra) {
        if (era == IsoEra.CE) {
            return yearOfEra;
        }
        return 1 - yearOfEra;
    }

    /**
     * Returns the era for the supplied value.
     *
     * @param eraValue the numeric era value
     * @return the matching era
     */
    @Override
    public Era eraOf(int eraValue) {
        return eraValue == 1 ? IsoEra.CE : IsoEra.BCE;
    }

    /**
     * Returns the eras recognized by this chronology.
     *
     * @return the supported eras
     */
    @Override
    public List<Era> eras() {
        return List.of(IsoEra.CE, IsoEra.BCE);
    }

    /**
     * Compares this chronology with another chronology by identifier.
     *
     * @param other the chronology to compare with
     * @return the comparison result
     */
    @Override
    public int compareTo(Chronology other) {
        return getId().compareTo(other.getId());
    }

    /**
     * Indicates whether another object is a Bengali chronology instance.
     *
     * @param obj the object to compare
     * @return {@code true} if the object is a Bengali chronology
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BengaliChronology;
    }

    /**
     * Returns the hash code for this chronology type.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return 31;
    }

    /**
     * Returns the chronology name.
     *
     * @return {@code "BengaliChronology"}
     */
    @Override
    public String toString() {
        return "BengaliChronology";
    }
}
