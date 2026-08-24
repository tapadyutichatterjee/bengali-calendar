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
package com.tapadyuti.bengalicalendar.internal;

import com.tapadyuti.bengalicalendar.BengaliCalendarSystem;
import com.tapadyuti.bengalicalendar.BengaliDate;
import com.tapadyuti.bengalicalendar.BengaliMonth;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

final class WestBengalConverter {
    private WestBengalConverter() {
    }

    static BengaliDate fromGregorian(LocalDate date) {
        int zodiacIndex = AstronomicalCalculator.zodiacIndex(date);
        LocalDate monthStart = sankrantiForDate(date, zodiacIndex);
        int bengaliYear = bengaliYear(date);
        int dayOfMonth = Math.toIntExact(ChronoUnit.DAYS.between(monthStart, date)) + 1;
        return new BengaliDate(
            bengaliYear,
            BengaliMonth.of(zodiacIndex + 1),
            dayOfMonth,
            BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL
        );
    }

    static LocalDate toGregorian(BengaliDate date) {
        int baseGregorianYear = date.getYear() + 593;
        int monthValue = date.getMonth().getValue();
        int sankrantiYear = monthValue <= 9 ? baseGregorianYear : baseGregorianYear + 1;
        LocalDate monthStart = AstronomicalCalculator.sankrantiDate(sankrantiYear, monthValue - 1);
        return monthStart.plusDays(date.getDayOfMonth() - 1L);
    }

    static int monthLength(int bengaliYear, BengaliMonth month) {
        LocalDate start = monthStart(bengaliYear, month);
        LocalDate next = month == BengaliMonth.CHAITRA
            ? AstronomicalCalculator.sankrantiDate(bengaliYear + 594, 0)
            : monthStart(bengaliYear, BengaliMonth.of(month.getValue() + 1));
        return Math.toIntExact(ChronoUnit.DAYS.between(start, next));
    }

    static int yearLength(int bengaliYear) {
        LocalDate start = AstronomicalCalculator.sankrantiDate(bengaliYear + 593, 0);
        LocalDate next = AstronomicalCalculator.sankrantiDate(bengaliYear + 594, 0);
        return Math.toIntExact(ChronoUnit.DAYS.between(start, next));
    }

    private static int bengaliYear(LocalDate date) {
        int gregorianYear = date.getYear();
        LocalDate meshaSankranti = AstronomicalCalculator.sankrantiDate(gregorianYear, 0);
        return !date.isBefore(meshaSankranti) ? gregorianYear - 593 : gregorianYear - 594;
    }

    private static LocalDate sankrantiForDate(LocalDate date, int zodiacIndex) {
        LocalDate candidate = AstronomicalCalculator.sankrantiDate(date.getYear(), zodiacIndex);
        return date.isBefore(candidate) ? AstronomicalCalculator.sankrantiDate(date.getYear() - 1, zodiacIndex) : candidate;
    }

    private static LocalDate monthStart(int bengaliYear, BengaliMonth month) {
        int baseGregorianYear = bengaliYear + 593;
        int monthValue = month.getValue();
        int sankrantiYear = monthValue <= 9 ? baseGregorianYear : baseGregorianYear + 1;
        return AstronomicalCalculator.sankrantiDate(sankrantiYear, monthValue - 1);
    }
}
