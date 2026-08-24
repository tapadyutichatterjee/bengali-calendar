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

import java.util.List;

/**
 * Traditional six-season Bengali cycle.
 */
public enum BengaliSeason {
    /** Grishma (গ্রীষ্ম), the summer season covering Baishakh and Jyaistha. */
    GRISHMA(List.of(BengaliMonth.BAISHAKH, BengaliMonth.JYAISTHA), "Grishma", "গ্রীষ্ম"),
    /** Barsha (বর্ষা), the monsoon season covering Ashadha and Shraban. */
    BARSHA(List.of(BengaliMonth.ASHADHA, BengaliMonth.SHRABAN), "Barsha", "বর্ষা"),
    /** Sharat (শরৎ), the autumn season covering Bhadra and Ashwin. */
    SHARAT(List.of(BengaliMonth.BHADRA, BengaliMonth.ASHWIN), "Sharat", "শরৎ"),
    /** Hemanta (হেমন্ত), the late-autumn season covering Kartik and Agrahayan. */
    HEMANTA(List.of(BengaliMonth.KARTIK, BengaliMonth.AGRAHAYAN), "Hemanta", "হেমন্ত"),
    /** Shita (শীত), the winter season covering Paush and Magh. */
    SHITA(List.of(BengaliMonth.PAUSH, BengaliMonth.MAGH), "Shita", "শীত"),
    /** Basanta (বসন্ত), the spring season covering Falgun and Chaitra. */
    BASANTA(List.of(BengaliMonth.FALGUN, BengaliMonth.CHAITRA), "Basanta", "বসন্ত");

    private final List<BengaliMonth> months;
    private final String englishName;
    private final String bengaliName;

    BengaliSeason(List<BengaliMonth> months, String englishName, String bengaliName) {
        this.months = months;
        this.englishName = englishName;
        this.bengaliName = bengaliName;
    }

    /**
     * Returns the months that belong to this season.
     *
     * @return an ordered list of the two months in the season
     */
    public List<BengaliMonth> getMonths() {
        return months;
    }

    /**
     * Returns the localized display name for this season.
     *
     * @param locale the output locale
     * @return the season name in the requested locale
     */
    public String getDisplayName(BengaliLocale locale) {
        return locale == BengaliLocale.BENGALI ? bengaliName : englishName;
    }

    /**
     * Returns the season containing the supplied month.
     *
     * @param month the month to classify
     * @return the corresponding Bengali season
     * @throws IllegalArgumentException if no season mapping is available
     */
    public static BengaliSeason of(BengaliMonth month) {
        for (BengaliSeason season : values()) {
            if (season.months.contains(month)) {
                return season;
            }
        }
        throw new IllegalArgumentException("No season for month: " + month);
    }
}
