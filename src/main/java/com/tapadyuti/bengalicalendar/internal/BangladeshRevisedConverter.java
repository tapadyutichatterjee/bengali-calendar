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
import java.time.Year;
import java.time.temporal.ChronoUnit;

final class BangladeshRevisedConverter {
    private BangladeshRevisedConverter() {
    }

    static BengaliDate fromGregorian(LocalDate date) {
        int gregorianYear = date.getYear();
        LocalDate newYearStart = LocalDate.of(gregorianYear, 4, 14);
        int banglaYear = date.isBefore(newYearStart) ? gregorianYear - 594 : gregorianYear - 593;
        LocalDate banglaYearStart = date.isBefore(newYearStart)
            ? LocalDate.of(gregorianYear - 1, 4, 14)
            : newYearStart;
        long daysSinceBanglaYearStart = ChronoUnit.DAYS.between(banglaYearStart, date);

        int monthValue = 1;
        long remaining = daysSinceBanglaYearStart;
        while (monthValue <= 12) {
            int monthLength = lengthOfMonth(banglaYear, BengaliMonth.of(monthValue));
            if (remaining < monthLength) {
                return new BengaliDate(
                    banglaYear,
                    BengaliMonth.of(monthValue),
                    Math.toIntExact(remaining) + 1,
                    BengaliCalendarSystem.BANGLADESH_REVISED
                );
            }
            remaining -= monthLength;
            monthValue++;
        }
        return new BengaliDate(banglaYear, BengaliMonth.CHAITRA, 30, BengaliCalendarSystem.BANGLADESH_REVISED);
    }

    static LocalDate toGregorian(BengaliDate date) {
        int gregorianYear = date.getYear() + 593;
        LocalDate yearStart = LocalDate.of(gregorianYear, 4, 14);
        long offset = 0;
        for (int m = 1; m < date.getMonth().getValue(); m++) {
            offset += lengthOfMonth(date.getYear(), BengaliMonth.of(m));
        }
        offset += date.getDayOfMonth() - 1L;
        return yearStart.plusDays(offset);
    }

    static int lengthOfMonth(int bengaliYear, BengaliMonth month) {
        if (month == BengaliMonth.FALGUN && Year.isLeap(bengaliYear + 594L)) {
            return 31;
        }
        return month.getValue() <= 5 ? 31 : 30;
    }

    static int lengthOfYear(int bengaliYear) {
        return Year.isLeap(bengaliYear + 594L) ? 366 : 365;
    }
}
