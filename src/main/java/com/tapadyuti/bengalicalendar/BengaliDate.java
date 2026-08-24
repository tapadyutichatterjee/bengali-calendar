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

import java.io.Serializable;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoPeriod;
import java.time.chrono.Chronology;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalField;
import java.time.temporal.TemporalUnit;
import java.time.temporal.UnsupportedTemporalTypeException;
import java.time.temporal.ValueRange;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable Bengali calendar date.
 * <p>
 * Instances store a Bengali year, month, day-of-month, and the {@link BengaliCalendarSystem} used to interpret that
 * triple. The type implements {@link ChronoLocalDate} so that it integrates with the {@code java.time.chrono}
 * ecosystem, and it implements {@link Serializable} for persistence or transport.
 * </p>
 * <p>
 * All mutating-style methods such as {@link #plusDays(long)} and {@link #with(TemporalField, long)} return new
 * instances. This class is therefore thread-safe provided it is safely published.
 * </p>
 */
public class BengaliDate implements ChronoLocalDate, TemporalAdjuster, Serializable {
    private static final long serialVersionUID = 1L;

    private final int year;
    private final BengaliMonth month;
    private final int dayOfMonth;
    private final BengaliCalendarSystem calendarSystem;

    /**
     * Creates a Bengali date in the supplied calendar system.
     *
     * @param year the Bengali year, starting at {@code 1}
     * @param month the Bengali month
     * @param dayOfMonth the day within the month, starting at {@code 1}
     * @param calendarSystem the calendar system that defines month boundaries for this date
     * @throws NullPointerException if {@code month} or {@code calendarSystem} is {@code null}
     * @throws BengaliCalendarException if the year or day is outside the valid range for the supplied month and system
     */
    public BengaliDate(int year, BengaliMonth month, int dayOfMonth, BengaliCalendarSystem calendarSystem) {
        this.year = year;
        this.month = Objects.requireNonNull(month, "month");
        this.dayOfMonth = dayOfMonth;
        this.calendarSystem = Objects.requireNonNull(calendarSystem, "calendarSystem");
        validate();
    }

    /**
     * Creates a Bengali date in the supplied calendar system.
     *
     * @param year the Bengali year
     * @param month the Bengali month
     * @param dayOfMonth the day within the month
     * @param system the calendar system to associate with the date
     * @return a validated Bengali date
     */
    public static BengaliDate of(int year, BengaliMonth month, int dayOfMonth, BengaliCalendarSystem system) {
        return new BengaliDate(year, month, dayOfMonth, system);
    }

    /**
     * Creates a Bengali date using {@link BengaliCalendar#getDefault()}.
     *
     * @param year the Bengali year
     * @param month the Bengali month
     * @param dayOfMonth the day within the month
     * @return a validated Bengali date in the default calendar system
     */
    public static BengaliDate of(int year, BengaliMonth month, int dayOfMonth) {
        return new BengaliDate(year, month, dayOfMonth, BengaliCalendar.getDefault());
    }

    /**
     * Creates a Bengali date from a numeric month value in the supplied calendar system.
     *
     * @param year the Bengali year
     * @param monthValue the 1-based Bengali month number
     * @param dayOfMonth the day within the month
     * @param system the calendar system to associate with the date
     * @return a validated Bengali date
     */
    public static BengaliDate of(int year, int monthValue, int dayOfMonth, BengaliCalendarSystem system) {
        return new BengaliDate(year, BengaliMonth.of(monthValue), dayOfMonth, system);
    }

    /**
     * Creates a Bengali date from a numeric month value using {@link BengaliCalendar#getDefault()}.
     *
     * @param year the Bengali year
     * @param monthValue the 1-based Bengali month number
     * @param dayOfMonth the day within the month
     * @return a validated Bengali date in the default calendar system
     */
    public static BengaliDate of(int year, int monthValue, int dayOfMonth) {
        return new BengaliDate(year, BengaliMonth.of(monthValue), dayOfMonth, BengaliCalendar.getDefault());
    }

    static BengaliDate resolvePreviousValid(int year, BengaliMonth month, int day, BengaliCalendarSystem system) {
        return new BengaliDate(year, month, Math.min(day, monthLength(year, month, system)), system);
    }

    /**
     * Returns the Bengali year.
     *
     * @return the year component
     */
    public int getYear() {
        return year;
    }

    /**
     * Returns the Bengali month.
     *
     * @return the month component
     */
    public BengaliMonth getMonth() {
        return month;
    }

    /**
     * Returns the day-of-month component.
     *
     * @return the day of the current Bengali month
     */
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * Returns the calendar system associated with this date.
     *
     * @return the calendar system used to interpret this date
     */
    public BengaliCalendarSystem getCalendarSystem() {
        return calendarSystem;
    }

    /**
     * Returns the Bengali season containing this date's month.
     *
     * @return the corresponding Bengali season
     */
    public BengaliSeason getSeason() {
        return month.getSeason();
    }

    /**
     * Returns the Bengali day of week corresponding to the Gregorian day of week of this date.
     *
     * @return the Bengali weekday name for this date
     */
    public BengaliDayOfWeek getDayOfWeek() {
        return BengaliDayOfWeek.fromJavaDayOfWeek(toGregorian().getDayOfWeek());
    }

    /**
     * Returns the holiday observed on this date, if one is defined for the date's year and calendar system.
     *
     * @return an {@link Optional} containing the holiday, or empty if none matches
     */
    public Optional<BengaliHoliday> getHoliday() {
        return Optional.ofNullable(BengaliHoliday.forYear(year, calendarSystem).get(this));
    }

    /**
     * Converts this Bengali date to its Gregorian equivalent.
     *
     * @return the corresponding Gregorian date
     */
    public LocalDate toGregorian() {
        return BengaliCalendar.toGregorian(this);
    }

    /**
     * Returns a copy of this date with the specified number of days added.
     *
     * @param days the number of days to add, negative to subtract
     * @return the adjusted date
     */
    public BengaliDate plusDays(long days) {
        return BengaliCalendar.from(toGregorian().plusDays(days), calendarSystem);
    }

    /**
     * Returns a copy of this date with the specified number of days subtracted.
     *
     * @param days the number of days to subtract, negative to add
     * @return the adjusted date
     */
    public BengaliDate minusDays(long days) {
        return plusDays(-days);
    }

    /**
     * Returns a copy of this date with the specified number of months added.
     * <p>
     * If the target month is shorter than the current day-of-month, the result is clamped to the last valid day of the
     * target month.
     * </p>
     *
     * @param months the number of months to add, negative to subtract
     * @return the adjusted date
     */
    public BengaliDate plusMonths(long months) {
        long totalMonths = getProlepticMonth() + months;
        int newYear = Math.toIntExact(Math.floorDiv(totalMonths, 12));
        int newMonth = Math.floorMod(totalMonths, 12) + 1;
        return resolvePreviousValid(newYear, BengaliMonth.of(newMonth), dayOfMonth, calendarSystem);
    }

    /**
     * Returns a copy of this date with the specified number of months subtracted.
     *
     * @param months the number of months to subtract, negative to add
     * @return the adjusted date
     */
    public BengaliDate minusMonths(long months) {
        return plusMonths(-months);
    }

    /**
     * Returns a copy of this date with the specified number of years added.
     * <p>
     * If the target year does not support the current day-of-month in the current month, the result is clamped to the
     * last valid day of that month.
     * </p>
     *
     * @param years the number of years to add, negative to subtract
     * @return the adjusted date
     */
    public BengaliDate plusYears(long years) {
        return resolvePreviousValid(Math.toIntExact(year + years), month, dayOfMonth, calendarSystem);
    }

    /**
     * Returns a copy of this date with the specified number of years subtracted.
     *
     * @param years the number of years to subtract, negative to add
     * @return the adjusted date
     */
    public BengaliDate minusYears(long years) {
        return plusYears(-years);
    }

    /**
     * Tests whether this date is before another Bengali date.
     *
     * @param other the date to compare against
     * @return {@code true} if this date is earlier than {@code other}
     */
    public boolean isBefore(BengaliDate other) {
        return compareTo(other) < 0;
    }

    /**
     * Tests whether this date is after another Bengali date.
     *
     * @param other the date to compare against
     * @return {@code true} if this date is later than {@code other}
     */
    public boolean isAfter(BengaliDate other) {
        return compareTo(other) > 0;
    }

    /**
     * Tests whether this date is before another chronological date.
     *
     * @param other the date to compare against
     * @return {@code true} if this date is earlier than {@code other}
     */
    @Override
    public boolean isBefore(ChronoLocalDate other) {
        return compareTo(other) < 0;
    }

    /**
     * Tests whether this date is after another chronological date.
     *
     * @param other the date to compare against
     * @return {@code true} if this date is later than {@code other}
     */
    @Override
    public boolean isAfter(ChronoLocalDate other) {
        return compareTo(other) > 0;
    }

    /**
     * Formats this date with the supplied pattern using {@link BengaliLocale#ENGLISH}.
     *
     * @param pattern the formatter pattern understood by {@link BengaliDateFormatter}
     * @return the formatted text
     */
    public String format(String pattern) {
        return BengaliDateFormatter.format(this, pattern, BengaliLocale.ENGLISH);
    }

    /**
     * Formats this date with the supplied pattern and locale.
     *
     * @param pattern the formatter pattern understood by {@link BengaliDateFormatter}
     * @param locale the output locale controlling month names and numeral script
     * @return the formatted text
     */
    public String format(String pattern, BengaliLocale locale) {
        return BengaliDateFormatter.format(this, pattern, locale);
    }

    /**
     * Returns the chronology singleton for Bengali dates.
     *
     * @return {@link BengaliChronology#INSTANCE}
     */
    @Override
    public Chronology getChronology() {
        return BengaliChronology.INSTANCE;
    }

    /**
     * Returns whether this date falls in a Bengali leap year for its calendar system.
     *
     * @return {@code true} if the year length exceeds {@code 365} days
     */
    @Override
    public boolean isLeapYear() {
        return yearLength(year, calendarSystem) > 365;
    }

    /**
     * Returns the number of days in this date's month.
     *
     * @return the month length in days
     */
    @Override
    public int lengthOfMonth() {
        return monthLength(year, month, calendarSystem);
    }

    /**
     * Returns the number of days in this date's year.
     *
     * @return the year length in days
     */
    @Override
    public int lengthOfYear() {
        return yearLength(year, calendarSystem);
    }

    /**
     * Returns whether the specified temporal field is supported.
     *
     * @param field the field to test
     * @return {@code true} if the field can be queried from this date
     */
    @Override
    public boolean isSupported(TemporalField field) {
        if (field instanceof ChronoField chronoField) {
            return switch (chronoField) {
                case DAY_OF_WEEK,
                     ALIGNED_DAY_OF_WEEK_IN_MONTH,
                     ALIGNED_DAY_OF_WEEK_IN_YEAR,
                     DAY_OF_MONTH,
                     DAY_OF_YEAR,
                     EPOCH_DAY,
                     ALIGNED_WEEK_OF_MONTH,
                     ALIGNED_WEEK_OF_YEAR,
                     MONTH_OF_YEAR,
                     PROLEPTIC_MONTH,
                     YEAR_OF_ERA,
                     YEAR,
                     ERA -> true;
                default -> false;
            };
        }
        return field != null && field.isSupportedBy(this);
    }

    /**
     * Returns whether the specified temporal unit is supported.
     *
     * @param unit the unit to test
     * @return {@code true} if the unit can be used for arithmetic on this date
     */
    public boolean isSupported(TemporalUnit unit) {
        return unit == ChronoUnit.DAYS
            || unit == ChronoUnit.WEEKS
            || unit == ChronoUnit.MONTHS
            || unit == ChronoUnit.YEARS
            || unit == ChronoUnit.DECADES
            || unit == ChronoUnit.CENTURIES
            || unit == ChronoUnit.MILLENNIA;
    }

    /**
     * Returns the valid range for a supported temporal field.
     *
     * @param field the field to query
     * @return the valid value range for the field on this date
     * @throws UnsupportedTemporalTypeException if the field is unsupported
     */
    @Override
    public ValueRange range(TemporalField field) {
        if (!(field instanceof ChronoField chronoField)) {
            return field.rangeRefinedBy(this);
        }
        return switch (chronoField) {
            case DAY_OF_WEEK, ALIGNED_DAY_OF_WEEK_IN_MONTH, ALIGNED_DAY_OF_WEEK_IN_YEAR -> ValueRange.of(1, 7);
            case DAY_OF_MONTH -> ValueRange.of(1, lengthOfMonth());
            case DAY_OF_YEAR -> ValueRange.of(1, lengthOfYear());
            case EPOCH_DAY -> ValueRange.of(Long.MIN_VALUE, Long.MAX_VALUE);
            case ALIGNED_WEEK_OF_MONTH -> ValueRange.of(1, 5);
            case ALIGNED_WEEK_OF_YEAR -> ValueRange.of(1, (lengthOfYear() + 6L) / 7L);
            case MONTH_OF_YEAR -> ValueRange.of(1, 12);
            case PROLEPTIC_MONTH -> ValueRange.of(0, 999999L * 12L + 11L);
            case YEAR_OF_ERA, YEAR -> ValueRange.of(1, 999999);
            case ERA -> ValueRange.of(1, 1);
            default -> throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        };
    }

    /**
     * Reads the value of a supported temporal field from this date.
     *
     * @param field the field to query
     * @return the field value
     * @throws UnsupportedTemporalTypeException if the field is unsupported
     */
    @Override
    public long getLong(TemporalField field) {
        if (!(field instanceof ChronoField chronoField)) {
            return field.getFrom(this);
        }
        int dayOfYear = dayOfYear();
        return switch (chronoField) {
            case EPOCH_DAY -> toEpochDay();
            case YEAR -> year;
            case MONTH_OF_YEAR -> month.getValue();
            case DAY_OF_MONTH -> dayOfMonth;
            case DAY_OF_YEAR -> dayOfYear;
            case DAY_OF_WEEK -> toGregorian().getDayOfWeek().getValue();
            case ALIGNED_DAY_OF_WEEK_IN_MONTH -> ((dayOfMonth - 1) % 7) + 1;
            case ALIGNED_DAY_OF_WEEK_IN_YEAR -> ((dayOfYear - 1) % 7) + 1;
            case ALIGNED_WEEK_OF_MONTH -> ((dayOfMonth - 1) / 7) + 1;
            case ALIGNED_WEEK_OF_YEAR -> ((dayOfYear - 1) / 7) + 1;
            case PROLEPTIC_MONTH -> getProlepticMonth();
            case YEAR_OF_ERA -> year;
            case ERA -> 1;
            default -> throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        };
    }

    /**
     * Returns a copy of this date with a specific field changed.
     *
     * @param field the field to change
     * @param newValue the new value for the field
     * @return the adjusted date
     * @throws UnsupportedTemporalTypeException if the field is unsupported
     */
    @Override
    public BengaliDate with(TemporalField field, long newValue) {
        if (!(field instanceof ChronoField chronoField)) {
            return (BengaliDate) field.adjustInto(this, newValue);
        }
        switch (chronoField) {
            case YEAR -> {
                return resolvePreviousValid(Math.toIntExact(newValue), month, dayOfMonth, calendarSystem);
            }
            case MONTH_OF_YEAR -> {
                return resolvePreviousValid(year, BengaliMonth.of(Math.toIntExact(newValue)), dayOfMonth, calendarSystem);
            }
            case DAY_OF_MONTH -> {
                return new BengaliDate(year, month, Math.toIntExact(newValue), calendarSystem);
            }
            case DAY_OF_YEAR -> {
                int requestedDayOfYear = Math.toIntExact(newValue);
                range(ChronoField.DAY_OF_YEAR).checkValidValue(newValue, ChronoField.DAY_OF_YEAR);
                int remaining = requestedDayOfYear;
                for (BengaliMonth current : BengaliMonth.values()) {
                    int monthLength = monthLength(year, current, calendarSystem);
                    if (remaining <= monthLength) {
                        return new BengaliDate(year, current, remaining, calendarSystem);
                    }
                    remaining -= monthLength;
                }
            }
            case EPOCH_DAY -> {
                return BengaliCalendar.from(LocalDate.ofEpochDay(newValue), calendarSystem);
            }
            default -> throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
        }
        throw new UnsupportedTemporalTypeException("Unsupported field: " + field);
    }

    /**
     * Returns a copy of this date with the specified amount added using the supplied unit.
     *
     * @param amountToAdd the amount to add, negative to subtract
     * @param unit the unit defining the amount
     * @return the adjusted date
     * @throws UnsupportedTemporalTypeException if the unit is unsupported
     */
    @Override
    public BengaliDate plus(long amountToAdd, TemporalUnit unit) {
        if (unit == ChronoUnit.DAYS) {
            return plusDays(amountToAdd);
        }
        if (unit == ChronoUnit.WEEKS) {
            return plusDays(Math.multiplyExact(amountToAdd, 7));
        }
        if (unit == ChronoUnit.MONTHS) {
            return plusMonths(amountToAdd);
        }
        if (unit == ChronoUnit.YEARS) {
            return plusYears(amountToAdd);
        }
        if (unit == ChronoUnit.DECADES) {
            return plusYears(Math.multiplyExact(amountToAdd, 10));
        }
        if (unit == ChronoUnit.CENTURIES) {
            return plusYears(Math.multiplyExact(amountToAdd, 100));
        }
        if (unit == ChronoUnit.MILLENNIA) {
            return plusYears(Math.multiplyExact(amountToAdd, 1000));
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    /**
     * Returns a copy of this date with the specified amount subtracted using the supplied unit.
     *
     * @param amountToSubtract the amount to subtract, negative to add
     * @param unit the unit defining the amount
     * @return the adjusted date
     */
    @Override
    public BengaliDate minus(long amountToSubtract, TemporalUnit unit) {
        return plus(-amountToSubtract, unit);
    }

    /**
     * Calculates the amount of time until another temporal in the requested unit.
     *
     * @param endExclusive the end date, exclusive
     * @param unit the unit for the result
     * @return the amount of time between this date and {@code endExclusive}
     * @throws UnsupportedTemporalTypeException if the unit is unsupported
     */
    @Override
    public long until(Temporal endExclusive, TemporalUnit unit) {
        BengaliDate end = endExclusive instanceof BengaliDate bengaliDate
            ? bengaliDate
            : BengaliCalendar.from(LocalDate.from(endExclusive), calendarSystem);
        if (unit == ChronoUnit.DAYS) {
            return ChronoUnit.DAYS.between(toGregorian(), end.toGregorian());
        }
        if (unit == ChronoUnit.WEEKS) {
            return ChronoUnit.WEEKS.between(toGregorian(), end.toGregorian());
        }
        long monthsUntil = monthsUntil(end);
        if (unit == ChronoUnit.MONTHS) {
            return monthsUntil;
        }
        if (unit == ChronoUnit.YEARS) {
            return monthsUntil / 12;
        }
        if (unit == ChronoUnit.DECADES) {
            return monthsUntil / 120;
        }
        if (unit == ChronoUnit.CENTURIES) {
            return monthsUntil / 1200;
        }
        if (unit == ChronoUnit.MILLENNIA) {
            return monthsUntil / 12000;
        }
        throw new UnsupportedTemporalTypeException("Unsupported unit: " + unit);
    }

    /**
     * Calculates the calendar period until another chronological date.
     *
     * @param endExclusive the end date, exclusive
     * @return a chronology-specific period expressed in years, months, and days
     */
    @Override
    public ChronoPeriod until(ChronoLocalDate endExclusive) {
        BengaliDate end = endExclusive instanceof BengaliDate bengaliDate
            ? bengaliDate
            : BengaliCalendar.from(LocalDate.from(endExclusive), calendarSystem);
        long monthsUntil = monthsUntil(end);
        BengaliDate candidate = plusMonths(monthsUntil);
        int days = Math.toIntExact(ChronoUnit.DAYS.between(candidate.toGregorian(), end.toGregorian()));
        int years = (int) (monthsUntil / 12);
        int months = (int) (monthsUntil % 12);
        return getChronology().period(years, months, days);
    }

    /**
     * Adjusts another temporal object so that it represents the same epoch day as this Bengali date.
     *
     * @param temporal the temporal object to adjust
     * @return the adjusted temporal
     */
    @Override
    public Temporal adjustInto(Temporal temporal) {
        return temporal.with(ChronoField.EPOCH_DAY, toEpochDay());
    }

    /**
     * Compares this date with another Bengali date.
     *
     * @param other the date to compare against
     * @return a negative value, zero, or a positive value as this date is before, equal to, or after {@code other}
     */
    public int compareTo(BengaliDate other) {
        return Long.compare(this.toEpochDay(), other.toEpochDay());
    }

    /**
     * Compares this date with another chronological date.
     *
     * @param other the date to compare against
     * @return a negative value, zero, or a positive value as this date is before, equal to, or after {@code other}
     */
    @Override
    public int compareTo(ChronoLocalDate other) {
        return Long.compare(this.toEpochDay(), other.toEpochDay());
    }

    /**
     * Returns the ISO epoch day represented by this Bengali date.
     *
     * @return the number of days since {@code 1970-01-01}
     */
    public long toEpochDay() {
        return toGregorian().toEpochDay();
    }

    /**
     * Indicates whether another object represents the same Bengali date and calendar system.
     *
     * @param o the object to compare
     * @return {@code true} if the object is an equal Bengali date
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BengaliDate that)) return false;
        return year == that.year && dayOfMonth == that.dayOfMonth && month == that.month && calendarSystem == that.calendarSystem;
    }

    /**
     * Returns a hash code for this date.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(year, month, dayOfMonth, calendarSystem);
    }

    /**
     * Returns a diagnostic string representation of this date.
     *
     * @return a string containing the date components and calendar system
     */
    @Override
    public String toString() {
        return "BengaliDate[year=" + year + ", month=" + month + ", day=" + dayOfMonth + ", system=" + calendarSystem + "]";
    }

    static int monthLength(int year, BengaliMonth month, BengaliCalendarSystem system) {
        return system == BengaliCalendarSystem.BANGLADESH_REVISED
            ? InternalSupport.bangladeshMonthLength(year, month)
            : InternalSupport.westBengalMonthLength(year, month);
    }

    static int yearLength(int year, BengaliCalendarSystem system) {
        return system == BengaliCalendarSystem.BANGLADESH_REVISED
            ? InternalSupport.bangladeshYearLength(year)
            : InternalSupport.westBengalYearLength(year);
    }

    private int dayOfYear() {
        int counter = 0;
        for (BengaliMonth current : BengaliMonth.values()) {
            if (current == month) {
                break;
            }
            counter += monthLength(year, current, calendarSystem);
        }
        return counter + dayOfMonth;
    }

    private long getProlepticMonth() {
        return year * 12L + (month.getValue() - 1L);
    }

    private long monthsUntil(BengaliDate end) {
        long months = end.getProlepticMonth() - getProlepticMonth();
        BengaliDate candidate = plusMonths(months);
        if (months > 0) {
            while (candidate.isAfter(end)) {
                months--;
                candidate = plusMonths(months);
            }
            while (!plusMonths(months + 1).isAfter(end)) {
                months++;
            }
        } else if (months < 0) {
            while (candidate.isBefore(end)) {
                months++;
                candidate = plusMonths(months);
            }
            while (!plusMonths(months - 1).isBefore(end)) {
                months--;
            }
        }
        return months;
    }

    private void validate() {
        if (year < 1) {
            throw new BengaliCalendarException("Year must be positive: " + year);
        }
        int maxDay = monthLength(year, month, calendarSystem);
        if (dayOfMonth < 1 || dayOfMonth > maxDay) {
            throw new BengaliCalendarException(
                "Day " + dayOfMonth + " is out of range for " + month.getDisplayName(BengaliLocale.ENGLISH)
                    + " (max: " + maxDay + ") in " + calendarSystem
            );
        }
    }
}
