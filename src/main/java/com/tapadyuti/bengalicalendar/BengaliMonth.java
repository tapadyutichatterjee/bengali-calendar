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

/**
 * Bengali calendar months in calendar order.
 */
public enum BengaliMonth {
    /** The first Bengali month, বৈশাখ (Baishakh). */
    BAISHAKH(1, "Baishakh", "বৈশাখ"),
    /** The second Bengali month, জ্যৈষ্ঠ (Jyaistha). */
    JYAISTHA(2, "Jyaistha", "জ্যৈষ্ঠ"),
    /** The third Bengali month, আষাঢ় (Ashadha). */
    ASHADHA(3, "Ashadha", "আষাঢ়"),
    /** The fourth Bengali month, শ্রাবণ (Shraban). */
    SHRABAN(4, "Shraban", "শ্রাবণ"),
    /** The fifth Bengali month, ভাদ্র (Bhadra). */
    BHADRA(5, "Bhadra", "ভাদ্র"),
    /** The sixth Bengali month, আশ্বিন (Ashwin). */
    ASHWIN(6, "Ashwin", "আশ্বিন"),
    /** The seventh Bengali month, কার্তিক (Kartik). */
    KARTIK(7, "Kartik", "কার্তিক"),
    /** The eighth Bengali month, অগ্রহায়ণ (Agrahayan). */
    AGRAHAYAN(8, "Agrahayan", "অগ্রহায়ণ"),
    /** The ninth Bengali month, পৌষ (Paush). */
    PAUSH(9, "Paush", "পৌষ"),
    /** The tenth Bengali month, মাঘ (Magh). */
    MAGH(10, "Magh", "মাঘ"),
    /** The eleventh Bengali month, ফাল্গুন (Falgun). */
    FALGUN(11, "Falgun", "ফাল্গুন"),
    /** The twelfth Bengali month, চৈত্র (Chaitra). */
    CHAITRA(12, "Chaitra", "চৈত্র");

    private final int value;
    private final String englishName;
    private final String bengaliName;

    BengaliMonth(int value, String englishName, String bengaliName) {
        this.value = value;
        this.englishName = englishName;
        this.bengaliName = bengaliName;
    }

    /**
     * Returns the 1-based month number.
     *
     * @return the numeric month value from {@code 1} to {@code 12}
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the localized full display name for this month.
     *
     * @param locale the output locale
     * @return the full month name in the requested locale
     */
    public String getDisplayName(BengaliLocale locale) {
        return locale == BengaliLocale.BENGALI ? bengaliName : englishName;
    }

    /**
     * Returns the localized abbreviated display name for this month.
     *
     * @param locale the output locale
     * @return the localized month abbreviation
     */
    public String getAbbreviation(BengaliLocale locale) {
        if (locale == BengaliLocale.BENGALI) {
            String name = bengaliName;
            return name.length() <= 4 ? name : name.substring(0, 4);
        }
        return englishName.substring(0, Math.min(3, englishName.length()));
    }

    /**
     * Returns the Bengali season containing this month.
     *
     * @return the associated Bengali season
     */
    public BengaliSeason getSeason() {
        return BengaliSeason.of(this);
    }

    /**
     * Returns the month length for the supplied calendar system.
     *
     * @param system the calendar system whose month rules should be applied
     * @param gregorianLeapYear whether the related Gregorian year is a leap year, used for Falgun length rules
     * @return the number of days in this month for the supplied rules
     */
    public int length(BengaliCalendarSystem system, boolean gregorianLeapYear) {
        if (system == BengaliCalendarSystem.BANGLADESH_REVISED) {
            if (this == FALGUN && gregorianLeapYear) {
                return 31;
            }
            return value <= 5 ? 31 : 30;
        }
        if (this == FALGUN && gregorianLeapYear) {
            return 31;
        }
        return value <= 5 ? 31 : 30;
    }

    /**
     * Returns the month corresponding to a 1-based numeric value.
     *
     * @param value the month number from {@code 1} to {@code 12}
     * @return the matching month constant
     * @throws IllegalArgumentException if {@code value} is outside the valid range
     */
    public static BengaliMonth of(int value) {
        for (BengaliMonth month : values()) {
            if (month.value == value) {
                return month;
            }
        }
        throw new IllegalArgumentException("Invalid Bengali month value: " + value);
    }
}
