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

/**
 * Formatter and parser for {@link BengaliDate} values.
 * <p>
 * Patterns are made from repeated token letters and literal separators. Numeric output uses Western digits for
 * {@link BengaliLocale#ENGLISH} and Bengali numerals for {@link BengaliLocale#BENGALI}. Month-name output switches
 * between English transliterations and Bengali script according to the locale.
 * </p>
 * <table>
 *     <caption>Pattern tokens</caption>
 *     <thead>
 *         <tr><th>Token</th><th>Meaning</th></tr>
 *     </thead>
 *     <tbody>
 *         <tr><td>{@code d}</td><td>Day of month (no padding)</td></tr>
 *         <tr><td>{@code dd}</td><td>Day of month (zero-padded)</td></tr>
 *         <tr><td>{@code M}</td><td>Month number</td></tr>
 *         <tr><td>{@code MM}</td><td>Month number (zero-padded)</td></tr>
 *         <tr><td>{@code MMM}</td><td>Month abbreviation</td></tr>
 *         <tr><td>{@code MMMM}</td><td>Full month name</td></tr>
 *         <tr><td>{@code y}</td><td>Year</td></tr>
 *         <tr><td>{@code yyyy}</td><td>Year (4-digit)</td></tr>
 *         <tr><td>{@code E}</td><td>Calendar system ({@code BD}/{@code WB})</td></tr>
 *     </tbody>
 * </table>
 */
public final class BengaliDateFormatter {
    private BengaliDateFormatter() {
    }

    /**
     * Formats a date using {@link BengaliLocale#ENGLISH}.
     *
     * @param date the date to format
     * @param pattern the formatting pattern
     * @return the formatted text
     * @throws NullPointerException if an argument is {@code null}
     */
    public static String format(BengaliDate date, String pattern) {
        return format(date, pattern, BengaliLocale.ENGLISH);
    }

    /**
     * Formats a date using the supplied locale.
     *
     * @param date the date to format
     * @param pattern the formatting pattern
     * @param locale the locale controlling month names and numeral script
     * @return the formatted text
     * @throws NullPointerException if an argument is {@code null}
     */
    public static String format(BengaliDate date, String pattern, BengaliLocale locale) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < pattern.length(); ) {
            char ch = pattern.charAt(i);
            int count = tokenLength(pattern, i, ch);
            switch (ch) {
                case 'd' -> {
                    String day = count >= 2 ? String.format("%02d", date.getDayOfMonth()) : Integer.toString(date.getDayOfMonth());
                    result.append(locale == BengaliLocale.BENGALI ? toBengaliDigits(day) : day);
                }
                case 'M' -> {
                    if (count >= 4) {
                        result.append(date.getMonth().getDisplayName(locale));
                    } else if (count == 3) {
                        result.append(date.getMonth().getAbbreviation(locale));
                    } else {
                        String month = count == 2 ? String.format("%02d", date.getMonth().getValue()) : Integer.toString(date.getMonth().getValue());
                        result.append(locale == BengaliLocale.BENGALI ? toBengaliDigits(month) : month);
                    }
                }
                case 'y' -> {
                    String year = count >= 4 ? String.format("%04d", date.getYear()) : Integer.toString(date.getYear());
                    result.append(locale == BengaliLocale.BENGALI ? toBengaliDigits(year) : year);
                }
                case 'E' -> result.append(date.getCalendarSystem() == BengaliCalendarSystem.BANGLADESH_REVISED ? "BD" : "WB");
                default -> result.append(pattern, i, i + count);
            }
            i += count;
        }
        return result.toString();
    }

    /**
     * Parses a date using the current default calendar system and {@link BengaliLocale#ENGLISH}.
     * <p>
     * If the pattern includes {@code E}, the parsed {@code BD}/{@code WB} token overrides the current default system.
     * </p>
     *
     * @param text the input text to parse
     * @param pattern the parsing pattern
     * @return the parsed Bengali date
     */
    public static BengaliDate parse(String text, String pattern) {
        return parseInternal(text, pattern, null, BengaliLocale.ENGLISH);
    }

    /**
     * Parses a date using the current default calendar system and the supplied locale.
     * <p>
     * If the pattern includes {@code E}, the parsed {@code BD}/{@code WB} token overrides the current default system.
     * </p>
     *
     * @param text the input text to parse
     * @param pattern the parsing pattern
     * @param locale the locale controlling accepted month names and numeral script
     * @return the parsed Bengali date
     */
    public static BengaliDate parse(String text, String pattern, BengaliLocale locale) {
        return parseInternal(text, pattern, null, locale);
    }

    /**
     * Parses a date using the supplied calendar system and {@link BengaliLocale#ENGLISH}.
     *
     * @param text the input text to parse
     * @param pattern the parsing pattern
     * @param system the calendar system to associate with the parsed date
     * @return the parsed Bengali date
     */
    public static BengaliDate parse(String text, String pattern, BengaliCalendarSystem system) {
        return parse(text, pattern, system, BengaliLocale.ENGLISH);
    }

    /**
     * Parses a date using the supplied calendar system and locale.
     * <p>
     * When {@code system} is non-null it takes precedence over any {@code E} token present in the text.
     * </p>
     *
     * @param text the input text to parse
     * @param pattern the parsing pattern
     * @param system the calendar system to associate with the parsed date
     * @param locale the locale controlling accepted month names and numeral script
     * @return the parsed Bengali date
     */
    public static BengaliDate parse(String text, String pattern, BengaliCalendarSystem system, BengaliLocale locale) {
        return parseInternal(text, pattern, system, locale);
    }

    static String toBengaliDigits(String value) {
        StringBuilder result = new StringBuilder();
        for (char ch : value.toCharArray()) {
            result.append(switch (ch) {
                case '0' -> '০';
                case '1' -> '১';
                case '2' -> '২';
                case '3' -> '৩';
                case '4' -> '৪';
                case '5' -> '৫';
                case '6' -> '৬';
                case '7' -> '৭';
                case '8' -> '৮';
                case '9' -> '৯';
                default -> ch;
            });
        }
        return result.toString();
    }

    private static BengaliDate parseInternal(String text, String pattern, BengaliCalendarSystem explicitSystem, BengaliLocale locale) {
        String normalizedText = normalizeInputText(text, locale);
        int position = 0;
        int year = -1;
        int monthValue = -1;
        int day = -1;
        BengaliCalendarSystem parsedSystem = null;

        for (int i = 0; i < pattern.length(); ) {
            char ch = pattern.charAt(i);
            int count = tokenLength(pattern, i, ch);
            switch (ch) {
                case 'd' -> {
                    DigitRead read = readDigits(normalizedText, position, count >= 2 ? 2 : 1, count >= 2 ? 2 : Integer.MAX_VALUE);
                    day = Integer.parseInt(read.value());
                    position = read.nextIndex();
                }
                case 'M' -> {
                    if (count >= 4) {
                        MonthRead read = readMonthToken(normalizedText, position, locale, false);
                        monthValue = read.monthValue();
                        position = read.nextIndex();
                    } else if (count == 3) {
                        MonthRead read = readMonthToken(normalizedText, position, locale, true);
                        monthValue = read.monthValue();
                        position = read.nextIndex();
                    } else {
                        DigitRead read = readDigits(normalizedText, position, count == 2 ? 2 : 1, count == 2 ? 2 : Integer.MAX_VALUE);
                        monthValue = Integer.parseInt(read.value());
                        position = read.nextIndex();
                    }
                }
                case 'y' -> {
                    DigitRead read = readDigits(normalizedText, position, count >= 4 ? 4 : 1, count >= 4 ? 4 : Integer.MAX_VALUE);
                    year = Integer.parseInt(read.value());
                    position = read.nextIndex();
                }
                case 'E' -> {
                    if (normalizedText.startsWith("BD", position)) {
                        parsedSystem = BengaliCalendarSystem.BANGLADESH_REVISED;
                        position += 2;
                    } else if (normalizedText.startsWith("WB", position)) {
                        parsedSystem = BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL;
                        position += 2;
                    } else {
                        throw new BengaliCalendarException("Unable to parse calendar system from text: " + text);
                    }
                }
                default -> {
                    String literal = pattern.substring(i, i + count);
                    if (!normalizedText.regionMatches(position, literal, 0, literal.length())) {
                        throw new BengaliCalendarException("Literal mismatch at position " + position + " while parsing: " + text);
                    }
                    position += literal.length();
                }
            }
            i += count;
        }

        if (position != normalizedText.length()) {
            throw new BengaliCalendarException("Unparsed trailing text: " + text.substring(Math.min(text.length(), position)));
        }
        if (year < 0 || monthValue < 0 || day < 0) {
            throw new BengaliCalendarException("Unable to parse date from text: " + text + " with pattern: " + pattern);
        }

        BengaliCalendarSystem system = explicitSystem != null ? explicitSystem : parsedSystem != null ? parsedSystem : BengaliCalendar.getDefault();
        return BengaliDate.of(year, monthValue, day, system);
    }

    private static int tokenLength(String pattern, int start, char ch) {
        int index = start;
        while (index < pattern.length() && pattern.charAt(index) == ch) {
            index++;
        }
        return index - start;
    }

    private static String normalizeInputText(String text, BengaliLocale locale) {
        if (locale != BengaliLocale.BENGALI) {
            return text;
        }
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            result.append(switch (ch) {
                case '০' -> '0';
                case '১' -> '1';
                case '২' -> '2';
                case '৩' -> '3';
                case '৪' -> '4';
                case '৫' -> '5';
                case '৬' -> '6';
                case '৭' -> '7';
                case '৮' -> '8';
                case '৯' -> '9';
                default -> ch;
            });
        }
        return result.toString();
    }

    private static DigitRead readDigits(String text, int start, int minDigits, int maxDigits) {
        int end = start;
        while (end < text.length() && Character.isDigit(text.charAt(end)) && end - start < maxDigits) {
            end++;
        }
        if (end - start < minDigits) {
            throw new BengaliCalendarException("Expected at least " + minDigits + " digit(s) at position " + start + " in: " + text);
        }
        return new DigitRead(text.substring(start, end), end);
    }

    private static MonthRead readMonthToken(String text, int start, BengaliLocale locale, boolean abbreviationOnly) {
        String remainder = text.substring(start);
        MonthRead best = null;
        for (BengaliMonth month : BengaliMonth.values()) {
            String token = abbreviationOnly ? month.getAbbreviation(locale) : month.getDisplayName(locale);
            if (remainder.startsWith(token)) {
                MonthRead candidate = new MonthRead(month.getValue(), start + token.length(), token.length());
                if (best == null || candidate.length() > best.length()) {
                    best = candidate;
                }
            }
        }
        if (best != null) {
            return best;
        }
        throw new BengaliCalendarException("Month token not recognized in input: " + remainder);
    }

    private record DigitRead(String value, int nextIndex) {
    }

    private record MonthRead(int monthValue, int nextIndex, int length) {
    }
}
