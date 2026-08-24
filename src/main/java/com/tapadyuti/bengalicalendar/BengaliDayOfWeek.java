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

import java.time.DayOfWeek;

/**
 * Bengali names for the seven days of the week.
 */
public enum BengaliDayOfWeek {
    /** রবিবার (Robibar), corresponding to Sunday. */
    ROBIBAR("Robibar", "রবিবার"),
    /** সোমবার (Shombar), corresponding to Monday. */
    SHOMBAR("Shombar", "সোমবার"),
    /** মঙ্গলবার (Monggolbar), corresponding to Tuesday. */
    MONGGOLBAR("Monggolbar", "মঙ্গলবার"),
    /** বুধবার (Budhbar), corresponding to Wednesday. */
    BUDHBAR("Budhbar", "বুধবার"),
    /** বৃহস্পতিবার (Brihoshpotibar), corresponding to Thursday. */
    BRIHOSHPOTIBAR("Brihoshpotibar", "বৃহস্পতিবার"),
    /** শুক্রবার (Shukrobar), corresponding to Friday. */
    SHUKROBAR("Shukrobar", "শুক্রবার"),
    /** শনিবার (Shonibar), corresponding to Saturday. */
    SHONIBAR("Shonibar", "শনিবার");

    private final String englishName;
    private final String bengaliName;

    BengaliDayOfWeek(String englishName, String bengaliName) {
        this.englishName = englishName;
        this.bengaliName = bengaliName;
    }

    /**
     * Returns the localized display name for this weekday.
     *
     * @param locale the output locale
     * @return the weekday name in the requested locale
     */
    public String getDisplayName(BengaliLocale locale) {
        return locale == BengaliLocale.BENGALI ? bengaliName : englishName;
    }

    /**
     * Converts a Java {@link DayOfWeek} into the corresponding Bengali weekday constant.
     *
     * @param dayOfWeek the Java weekday
     * @return the matching Bengali weekday
     */
    public static BengaliDayOfWeek fromJavaDayOfWeek(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case SUNDAY -> ROBIBAR;
            case MONDAY -> SHOMBAR;
            case TUESDAY -> MONGGOLBAR;
            case WEDNESDAY -> BUDHBAR;
            case THURSDAY -> BRIHOSHPOTIBAR;
            case FRIDAY -> SHUKROBAR;
            case SATURDAY -> SHONIBAR;
        };
    }
}
