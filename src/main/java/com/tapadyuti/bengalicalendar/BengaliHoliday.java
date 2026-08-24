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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Bengali cultural holidays bundled with the library.
 */
public enum BengaliHoliday {
    /** Pohela Boishakh, observed on 1 Baishakh in both supported systems. */
    POHELA_BOISHAKH("Pohela Boishakh", "পহেলা বৈশাখ"),
    /** Rabindra Jayanti, observed on 25 Baishakh. */
    RABINDRA_JAYANTI("Rabindra Jayanti", "রবীন্দ্র জয়ন্তী"),
    /** Shaheed Dibosh, observed on 8 Falgun in the Bangladesh Revised system only. */
    SHAHEED_DIBOSH("Shaheed Dibosh", "শহীদ দিবস");

    private final String englishName;
    private final String bengaliName;

    BengaliHoliday(String englishName, String bengaliName) {
        this.englishName = englishName;
        this.bengaliName = bengaliName;
    }

    /**
     * Returns the localized holiday name.
     *
     * @param locale the output locale
     * @return the holiday name in the requested locale
     */
    public String getDisplayName(BengaliLocale locale) {
        return locale == BengaliLocale.BENGALI ? bengaliName : englishName;
    }

    /**
     * Returns the holiday calendar for a Bengali year and system.
     *
     * @param banglaYear the Bengali year for which holidays should be generated
     * @param system the calendar system whose holiday dates should be used
     * @return a map keyed by {@link BengaliDate} in insertion order
     */
    public static Map<BengaliDate, BengaliHoliday> forYear(int banglaYear, BengaliCalendarSystem system) {
        Map<BengaliDate, BengaliHoliday> result = new LinkedHashMap<>();
        BengaliDate pohela = BengaliDate.of(banglaYear, BengaliMonth.BAISHAKH, 1, system);
        result.put(pohela, POHELA_BOISHAKH);
        BengaliDate rabindra = BengaliDate.of(banglaYear, BengaliMonth.BAISHAKH, 25, system);
        result.put(rabindra, RABINDRA_JAYANTI);
        if (system == BengaliCalendarSystem.BANGLADESH_REVISED) {
            BengaliDate shaheed = BengaliDate.of(banglaYear, BengaliMonth.FALGUN, 8, system);
            result.put(shaheed, SHAHEED_DIBOSH);
        }
        return result;
    }
}
