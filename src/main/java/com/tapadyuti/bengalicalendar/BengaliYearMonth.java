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

import java.util.Objects;

/**
 * Immutable year-month value for the Bengali calendar.
 * <p>
 * This type is useful when only the Bengali year and month are needed, for example when navigating month views or
 * creating dates with {@link #atDay(int)} using the current default calendar system.
 * </p>
 */
public final class BengaliYearMonth {
    private final int year;
    private final BengaliMonth month;

    /**
     * Creates a Bengali year-month.
     *
     * @param year the Bengali year
     * @param month the Bengali month
     */
    public BengaliYearMonth(int year, BengaliMonth month) {
        this.year = year;
        this.month = Objects.requireNonNull(month, "month");
    }

    /**
     * Creates a Bengali year-month.
     *
     * @param year the Bengali year
     * @param month the Bengali month
     * @return a new year-month instance
     */
    public static BengaliYearMonth of(int year, BengaliMonth month) {
        return new BengaliYearMonth(year, month);
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
     * Creates a {@link BengaliDate} on the supplied day-of-month using the current default calendar system.
     *
     * @param dayOfMonth the day of month to combine with this year-month
     * @return the resulting Bengali date
     */
    public BengaliDate atDay(int dayOfMonth) {
        return BengaliDate.of(year, month, dayOfMonth, BengaliCalendar.getDefault());
    }

    /**
     * Returns the number of days in this month according to the current default calendar system.
     *
     * @return the month length in days
     */
    public int lengthOfMonth() {
        return BengaliDate.monthLength(year, month, BengaliCalendar.getDefault());
    }

    /**
     * Returns whether this year-month falls in a Bengali leap year under the current default calendar system.
     *
     * @return {@code true} if the year length exceeds {@code 365} days
     */
    public boolean isLeapYear() {
        return BengaliDate.yearLength(year, BengaliCalendar.getDefault()) > 365;
    }

    /**
     * Returns a copy of this year-month with a number of months added.
     *
     * @param monthsToAdd the number of months to add, negative to subtract
     * @return the adjusted year-month
     */
    public BengaliYearMonth plusMonths(long monthsToAdd) {
        long total = year * 12L + month.getValue() - 1L + monthsToAdd;
        int targetYear = Math.toIntExact(Math.floorDiv(total, 12));
        int targetMonthValue = Math.floorMod(total, 12) + 1;
        return new BengaliYearMonth(targetYear, BengaliMonth.of(targetMonthValue));
    }

    /**
     * Returns a copy of this year-month with a number of months subtracted.
     *
     * @param monthsToAdd the number of months to subtract, negative to add
     * @return the adjusted year-month
     */
    public BengaliYearMonth minusMonths(long monthsToAdd) {
        return plusMonths(-monthsToAdd);
    }
}
