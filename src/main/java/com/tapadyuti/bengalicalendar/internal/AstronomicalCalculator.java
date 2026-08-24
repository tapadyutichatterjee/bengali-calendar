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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

final class AstronomicalCalculator {
    private static final int[] LIKELY_MONTHS = {4, 5, 6, 7, 8, 9, 10, 11, 12, 1, 2, 3};
    private static final int[] LIKELY_DAYS = {14, 15, 15, 16, 17, 17, 18, 17, 16, 14, 13, 15};

    private AstronomicalCalculator() {
    }

    static double siderealLongitude(LocalDate date) {
        double jde = julianEphemerisDay(date);
        double t = (jde - 2451545.0) / 36525.0;

        double l0 = normalizeDegrees(280.46646 + 36000.76983 * t + 0.0003032 * t * t);
        double m = normalizeDegrees(357.52911 + 35999.05029 * t - 0.0001537 * t * t);
        double e = 0.016708634 - 0.000042037 * t - 0.0000001267 * t * t;
        double mRad = Math.toRadians(m);
        double c = (1.9146 - 0.004817 * t - 0.000014 * t * t) * Math.sin(mRad)
            + (0.019993 - 0.000101 * t) * Math.sin(2 * mRad)
            + 0.00029 * Math.sin(3 * mRad);
        double sunTrueLon = l0 + c;

        double omega = 125.04 - 1934.136 * t;
        double apparentLon = sunTrueLon - 0.00569 - 0.00478 * Math.sin(Math.toRadians(omega));
        double ayanamsa = 23.85 + 0.0139 * ((jde - 2451545.0) / 365.25);

        return normalizeDegrees(apparentLon - ayanamsa);
    }

    static int zodiacIndex(LocalDate date) {
        return (int) Math.floor(siderealLongitude(date) / 30.0) % 12;
    }

    static LocalDate sankrantiDate(int gregorianYear, int zodiacIndex) {
        if (zodiacIndex < 0 || zodiacIndex > 11) {
            throw new IllegalArgumentException("Invalid zodiac index: " + zodiacIndex);
        }
        LocalDate likelyEntry = LocalDate.of(gregorianYear, LIKELY_MONTHS[zodiacIndex], LIKELY_DAYS[zodiacIndex]);
        LocalDate low = likelyEntry.minusDays(1);
        LocalDate high = likelyEntry.plusDays(40);
        double targetLongitude = zodiacIndex * 30.0;

        while (low.isBefore(high)) {
            long days = ChronoUnit.DAYS.between(low, high);
            LocalDate mid = low.plusDays(days / 2);
            if (hasEntered(mid, targetLongitude)) {
                high = mid;
            } else {
                low = mid.plusDays(1);
            }
        }
        return low;
    }

    private static boolean hasEntered(LocalDate date, double targetLongitude) {
        double delta = normalizeDegrees(siderealLongitude(date) - targetLongitude);
        return delta < 180.0;
    }

    private static double julianEphemerisDay(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();

        if (month <= 2) {
            year--;
            month += 12;
        }

        int a = year / 100;
        int b = 2 - a + (a / 4);
        double dayFraction = 6.5 / 24.0;

        return Math.floor(365.25 * (year + 4716))
            + Math.floor(30.6001 * (month + 1))
            + day + b - 1524.5 + dayFraction;
    }

    private static double normalizeDegrees(double angle) {
        double normalized = angle % 360.0;
        return normalized < 0 ? normalized + 360.0 : normalized;
    }
}
