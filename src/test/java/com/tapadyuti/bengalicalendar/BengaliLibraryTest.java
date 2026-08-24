package com.tapadyuti.bengalicalendar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.Year;
import java.time.chrono.ChronoPeriod;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BengaliLibraryTest {

    @BeforeEach
    void resetDefaultCalendarSystem() {
        System.clearProperty("bengalicalendar.default.system");
        BengaliCalendar.setDefault(BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL);
    }

    @AfterEach
    void restoreDefaultCalendarSystem() {
        System.clearProperty("bengalicalendar.default.system");
        BengaliCalendar.setDefault(BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL);
    }

    @Test
    void convertsKnownBangladeshRevisedDate() {
        BengaliDate date = BengaliCalendar.from(LocalDate.of(2024, 4, 14), BengaliCalendarSystem.BANGLADESH_REVISED);
        assertEquals(1431, date.getYear());
        assertEquals(BengaliMonth.BAISHAKH, date.getMonth());
        assertEquals(1, date.getDayOfMonth());
        assertEquals(BengaliCalendarSystem.BANGLADESH_REVISED, date.getCalendarSystem());
    }

    @Test
    void defaultSystemIsWestBengalTraditional() {
        assertEquals(BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL, BengaliCalendar.getDefault());
        BengaliDate date = BengaliCalendar.from(LocalDate.of(2024, 4, 14));
        assertEquals(BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL, date.getCalendarSystem());
    }

    @Test
    void formatsDatesInEnglishAndBengali() {
        BengaliDate date = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED);
        assertEquals("01-01-1431", date.format("dd-MM-yyyy", BengaliLocale.ENGLISH));
        assertTrue(date.format("yyyy MMMM dd", BengaliLocale.BENGALI).contains("বৈশাখ"));
        assertTrue(date.format("yyyy MMMM dd", BengaliLocale.BENGALI).contains("১৪৩১"));
    }

    @Test
    void parsesDateText() {
        BengaliDate parsed = BengaliDateFormatter.parse("01-01-1431", "dd-MM-yyyy", BengaliCalendarSystem.BANGLADESH_REVISED, BengaliLocale.ENGLISH);
        assertEquals(1431, parsed.getYear());
        assertEquals(BengaliMonth.BAISHAKH, parsed.getMonth());
        assertEquals(1, parsed.getDayOfMonth());
    }

    @Test
    void supportsYearMonthAndRangeHelpers() {
        BengaliCalendar.setDefault(BengaliCalendarSystem.BANGLADESH_REVISED);
        BengaliYearMonth yearMonth = BengaliYearMonth.of(1431, BengaliMonth.BAISHAKH);
        assertEquals(31, yearMonth.lengthOfMonth());

        BengaliDateRange range = BengaliDateRange.of(
            BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED),
            BengaliDate.of(1431, BengaliMonth.BAISHAKH, 3, BengaliCalendarSystem.BANGLADESH_REVISED)
        );
        assertEquals(3, range.lengthInDays());
        assertEquals(3, range.stream().count());
    }

    @Test
    void convertsWestBengalTraditionalAroundMeshaSankranti() {
        BengaliDate before = BengaliCalendar.from(LocalDate.of(2024, 4, 13), BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL);
        BengaliDate start = BengaliCalendar.from(LocalDate.of(2024, 4, 14), BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL);
        BengaliDate after = BengaliCalendar.from(LocalDate.of(2024, 4, 15), BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL);

        assertEquals(BengaliMonth.CHAITRA, before.getMonth());
        assertEquals(BengaliMonth.BAISHAKH, start.getMonth());
        assertEquals(1, start.getDayOfMonth());
        assertEquals(1431, start.getYear());
        assertEquals(BengaliMonth.BAISHAKH, after.getMonth());
        assertTrue(after.getDayOfMonth() >= 1 && after.getDayOfMonth() <= 2);
        assertEquals(LocalDate.of(2024, 4, 14), BengaliCalendar.toGregorian(start));
    }

    @Test
    void supportsChronoFieldAccessors() {
        BengaliDate date = BengaliDate.of(1431, BengaliMonth.JYAISTHA, 5, BengaliCalendarSystem.BANGLADESH_REVISED);

        assertEquals(1431, date.getLong(ChronoField.YEAR));
        assertEquals(2, date.getLong(ChronoField.MONTH_OF_YEAR));
        assertEquals(5, date.getLong(ChronoField.DAY_OF_MONTH));
        assertEquals(36, date.getLong(ChronoField.DAY_OF_YEAR));
        assertEquals(date.toGregorian().getDayOfWeek().getValue(), date.getLong(ChronoField.DAY_OF_WEEK));
        assertEquals(5, date.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH));
        assertEquals(1, date.getLong(ChronoField.ALIGNED_WEEK_OF_MONTH));
        assertEquals(1, date.getLong(ChronoField.ALIGNED_DAY_OF_WEEK_IN_YEAR));
        assertEquals(6, date.getLong(ChronoField.ALIGNED_WEEK_OF_YEAR));
        assertEquals(1431L * 12L + 1L, date.getLong(ChronoField.PROLEPTIC_MONTH));
        assertEquals(1431, date.getLong(ChronoField.YEAR_OF_ERA));
        assertEquals(1, date.getLong(ChronoField.ERA));
        assertEquals(date.toEpochDay(), date.getLong(ChronoField.EPOCH_DAY));
    }

    @Test
    void untilInDaysUsesEpochDays() {
        BengaliDate start = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED);
        BengaliDate end = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 11, BengaliCalendarSystem.BANGLADESH_REVISED);

        assertEquals(10, start.until(end, ChronoUnit.DAYS));
        ChronoPeriod period = start.until(end);
        assertEquals(0, period.get(ChronoUnit.YEARS));
        assertEquals(0, period.get(ChronoUnit.MONTHS));
        assertEquals(10, period.get(ChronoUnit.DAYS));
    }

    @Test
    void plusMonthsClampsToPreviousValidDate() {
        BengaliDate leapFalgun = BengaliDate.of(1430, BengaliMonth.FALGUN, 31, BengaliCalendarSystem.BANGLADESH_REVISED);
        BengaliDate clamped = leapFalgun.plusMonths(1);

        assertEquals(BengaliMonth.CHAITRA, clamped.getMonth());
        assertEquals(30, clamped.getDayOfMonth());
        assertEquals(1430, clamped.getYear());
    }

    @Test
    void convertsBangladeshRevisedKnownDatePairs() {
        assertBangladeshRevised(LocalDate.of(2024, 4, 14), 1431, BengaliMonth.BAISHAKH, 1);
        assertBangladeshRevised(LocalDate.of(2024, 5, 15), 1431, BengaliMonth.JYAISTHA, 1);
        assertBangladeshRevised(LocalDate.of(2024, 6, 15), 1431, BengaliMonth.ASHADHA, 1);
        assertBangladeshRevised(LocalDate.of(2024, 7, 16), 1431, BengaliMonth.SHRABAN, 1);
        assertBangladeshRevised(LocalDate.of(2024, 8, 16), 1431, BengaliMonth.BHADRA, 1);
        assertBangladeshRevised(LocalDate.of(2024, 9, 16), 1431, BengaliMonth.ASHWIN, 1);
        assertBangladeshRevised(LocalDate.of(2024, 10, 16), 1431, BengaliMonth.KARTIK, 1);
        assertBangladeshRevised(LocalDate.of(2024, 11, 15), 1431, BengaliMonth.AGRAHAYAN, 1);
        assertBangladeshRevised(LocalDate.of(2024, 12, 15), 1431, BengaliMonth.PAUSH, 1);
        assertBangladeshRevised(LocalDate.of(2025, 1, 14), 1431, BengaliMonth.MAGH, 1);
        assertBangladeshRevised(LocalDate.of(2025, 2, 13), 1431, BengaliMonth.FALGUN, 1);
        assertBangladeshRevised(LocalDate.of(2025, 3, 15), 1431, BengaliMonth.CHAITRA, 1);
    }

    @Test
    void handlesBangladeshRevisedYearBoundary() {
        BengaliDate lastDay = BengaliCalendar.from(LocalDate.of(2024, 4, 13), BengaliCalendarSystem.BANGLADESH_REVISED);
        BengaliDate firstDay = BengaliCalendar.from(LocalDate.of(2024, 4, 14), BengaliCalendarSystem.BANGLADESH_REVISED);

        assertEquals(BengaliDate.of(1430, BengaliMonth.CHAITRA, 30, BengaliCalendarSystem.BANGLADESH_REVISED), lastDay);
        assertEquals(BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED), firstDay);
        assertEquals(LocalDate.of(2024, 4, 14), lastDay.plusDays(1).toGregorian());
    }

    @Test
    void bangladeshRevisedFalgunLengthFollowsGregorianLeapYear() {
        BengaliDate leapYearFalgun = BengaliCalendar.from(LocalDate.of(2024, 2, 13), BengaliCalendarSystem.BANGLADESH_REVISED);
        BengaliDate commonYearFalgun = BengaliCalendar.from(LocalDate.of(2025, 2, 13), BengaliCalendarSystem.BANGLADESH_REVISED);

        assertEquals(1430, leapYearFalgun.getYear());
        assertEquals(BengaliMonth.FALGUN, leapYearFalgun.getMonth());
        assertTrue(Year.isLeap(leapYearFalgun.toGregorian().getYear()));
        assertEquals(31, leapYearFalgun.lengthOfMonth());

        assertEquals(1431, commonYearFalgun.getYear());
        assertEquals(BengaliMonth.FALGUN, commonYearFalgun.getMonth());
        assertFalse(Year.isLeap(commonYearFalgun.toGregorian().getYear()));
        assertEquals(30, commonYearFalgun.lengthOfMonth());
    }

    @Test
    void roundTripsBangladeshRevisedDatesExactly() {
        List<LocalDate> dates = List.of(
            LocalDate.of(2024, 4, 14),
            LocalDate.of(2024, 4, 30),
            LocalDate.of(2024, 5, 15),
            LocalDate.of(2024, 6, 20),
            LocalDate.of(2024, 7, 16),
            LocalDate.of(2024, 8, 31),
            LocalDate.of(2024, 9, 16),
            LocalDate.of(2024, 11, 15),
            LocalDate.of(2024, 12, 31),
            LocalDate.of(2025, 1, 14),
            LocalDate.of(2025, 2, 21),
            LocalDate.of(2025, 3, 20)
        );

        for (LocalDate date : dates) {
            BengaliDate bengaliDate = BengaliCalendar.from(date, BengaliCalendarSystem.BANGLADESH_REVISED);
            assertEquals(date, bengaliDate.toGregorian(), () -> "Expected exact round-trip for " + date);
        }
    }

    @Test
    void roundTripsWestBengalTraditionalDatesWithinOneDay() {
        List<LocalDate> dates = List.of(
            LocalDate.of(2024, 4, 13),
            LocalDate.of(2024, 4, 14),
            LocalDate.of(2024, 6, 1),
            LocalDate.of(2024, 8, 16),
            LocalDate.of(2024, 10, 16),
            LocalDate.of(2025, 1, 1)
        );

        for (LocalDate date : dates) {
            BengaliDate bengaliDate = BengaliCalendar.from(date, BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL);
            long difference = Math.abs(ChronoUnit.DAYS.between(date, bengaliDate.toGregorian()));
            assertTrue(difference <= 1, () -> "Expected <= 1 day drift for " + date + " but was " + difference);
        }
    }

    @Test
    void formatsAllSupportedPatternTokensInBothLocales() {
        BengaliDate date = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED);

        String english = BengaliDateFormatter.format(date, "d|dd|M|MM|MMM|MMMM|y|yyyy|E", BengaliLocale.ENGLISH);
        String bengali = BengaliDateFormatter.format(date, "d|dd|M|MM|MMM|MMMM|y|yyyy|E", BengaliLocale.BENGALI);

        assertEquals("1|01|1|01|Bai|Baishakh|1431|1431|BD", english);
        assertEquals("১|০১|১|০১|বৈশা|বৈশাখ|১৪৩১|১৪৩১|BD", bengali);
        assertTrue(bengali.contains("১৪৩১"));
        assertTrue(bengali.contains("বৈশাখ"));
    }

    @Test
    void parsesFormattedDatesBackToSameDate() {
        BengaliDate original = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED);
        String formatted = BengaliDateFormatter.format(original, "dd MMMM yyyy E", BengaliLocale.ENGLISH);

        BengaliDate parsed = BengaliDateFormatter.parse(formatted, "dd MMMM yyyy E", BengaliLocale.ENGLISH);
        assertEquals(original, parsed);
    }

    @Test
    void parsesBengaliLocaleInput() {
        BengaliDate original = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED);
        String formatted = BengaliDateFormatter.format(original, "dd MMMM yyyy E", BengaliLocale.BENGALI);

        BengaliDate parsed = BengaliDateFormatter.parse(formatted, "dd MMMM yyyy E", BengaliLocale.BENGALI);
        assertEquals(original, parsed);
    }

    @Test
    void supportsDateArithmeticAcrossBoundaries() {
        BengaliDate monthBoundary = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 31, BengaliCalendarSystem.BANGLADESH_REVISED).plusDays(1);
        BengaliDate yearBoundary = BengaliDate.of(1430, BengaliMonth.CHAITRA, 30, BengaliCalendarSystem.BANGLADESH_REVISED).plusDays(1);
        BengaliDate plusMonths = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 15, BengaliCalendarSystem.BANGLADESH_REVISED).plusMonths(1);
        BengaliDate plusYears = BengaliDate.of(1430, BengaliMonth.BAISHAKH, 10, BengaliCalendarSystem.BANGLADESH_REVISED).plusYears(1);
        BengaliDate minusDays = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED).minusDays(1);

        assertEquals(BengaliDate.of(1431, BengaliMonth.JYAISTHA, 1, BengaliCalendarSystem.BANGLADESH_REVISED), monthBoundary);
        assertEquals(BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED), yearBoundary);
        assertEquals(BengaliDate.of(1431, BengaliMonth.JYAISTHA, 15, BengaliCalendarSystem.BANGLADESH_REVISED), plusMonths);
        assertEquals(BengaliDate.of(1431, BengaliMonth.BAISHAKH, 10, BengaliCalendarSystem.BANGLADESH_REVISED), plusYears);
        assertEquals(BengaliDate.of(1430, BengaliMonth.CHAITRA, 30, BengaliCalendarSystem.BANGLADESH_REVISED), minusDays);
    }

    @Test
    void supportsChronoLocalDateQueriesAndPeriods() {
        BengaliDate date = BengaliDate.of(1430, BengaliMonth.FALGUN, 31, BengaliCalendarSystem.BANGLADESH_REVISED);
        BengaliDate end = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 2, BengaliCalendarSystem.BANGLADESH_REVISED);

        assertEquals(1430, date.getLong(ChronoField.YEAR));
        assertEquals(11, date.getLong(ChronoField.MONTH_OF_YEAR));
        assertEquals(31, date.getLong(ChronoField.DAY_OF_MONTH));
        assertEquals(336, date.getLong(ChronoField.DAY_OF_YEAR));
        assertEquals(date.toGregorian().getDayOfWeek().getValue(), date.getLong(ChronoField.DAY_OF_WEEK));
        assertEquals(date.toEpochDay(), date.getLong(ChronoField.EPOCH_DAY));
        assertEquals(1430L * 12L + 10L, date.getLong(ChronoField.PROLEPTIC_MONTH));
        assertEquals(1, date.getLong(ChronoField.ERA));
        assertEquals(1430, date.getLong(ChronoField.YEAR_OF_ERA));
        assertTrue(date.isLeapYear());
        assertEquals(31, date.lengthOfMonth());
        assertEquals(31, BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED).lengthOfMonth());
        assertEquals(32, date.until(end, ChronoUnit.DAYS));

        ChronoPeriod period = date.until(end);
        assertEquals(0, period.get(ChronoUnit.YEARS));
        assertEquals(1, period.get(ChronoUnit.MONTHS));
        assertEquals(2, period.get(ChronoUnit.DAYS));
    }

    @Test
    void supportsBengaliYearMonthOperations() {
        BengaliCalendar.setDefault(BengaliCalendarSystem.BANGLADESH_REVISED);

        int[] expectedLengths = {31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 31, 30};
        BengaliMonth[] months = BengaliMonth.values();
        for (int i = 0; i < months.length; i++) {
            final BengaliMonth m = months[i];
            final int expected = expectedLengths[i];
            BengaliYearMonth yearMonth = BengaliYearMonth.of(1430, m);
            assertEquals(expected, yearMonth.lengthOfMonth(), "Unexpected length for " + m);
        }

        BengaliYearMonth baishakh = BengaliYearMonth.of(1431, BengaliMonth.BAISHAKH);
        assertEquals(BengaliDate.of(1431, BengaliMonth.BAISHAKH, 12, BengaliCalendarSystem.BANGLADESH_REVISED), baishakh.atDay(12));
        assertEquals(BengaliYearMonth.of(1431, BengaliMonth.ASHADHA).getMonth(), baishakh.plusMonths(2).getMonth());
        assertEquals(1430, baishakh.minusMonths(1).getYear());
        assertEquals(BengaliMonth.CHAITRA, baishakh.minusMonths(1).getMonth());
    }

    @Test
    void supportsBengaliDateRangeQueriesAndTraversal() {
        BengaliDateRange range = BengaliDateRange.of(
            BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED),
            BengaliDate.of(1431, BengaliMonth.BAISHAKH, 5, BengaliCalendarSystem.BANGLADESH_REVISED)
        );

        assertTrue(range.contains(BengaliDate.of(1431, BengaliMonth.BAISHAKH, 3, BengaliCalendarSystem.BANGLADESH_REVISED)));
        assertFalse(range.contains(BengaliDate.of(1431, BengaliMonth.BAISHAKH, 6, BengaliCalendarSystem.BANGLADESH_REVISED)));
        assertEquals(5, range.stream().count());

        Iterator<BengaliDate> iterator = range.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        assertEquals(5, count);
        assertEquals(5, range.lengthInDays());
    }

    @Test
    void exposesSeasonAndWeekdayConsistently() {
        BengaliDate springDate = BengaliDate.of(1430, BengaliMonth.FALGUN, 8, BengaliCalendarSystem.BANGLADESH_REVISED);
        BengaliDate summerDate = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED);

        assertEquals(BengaliSeason.BASANTA, springDate.getSeason());
        assertEquals(BengaliSeason.GRISHMA, summerDate.getSeason());
        assertEquals(BengaliDayOfWeek.fromJavaDayOfWeek(summerDate.toGregorian().getDayOfWeek()), summerDate.getDayOfWeek());
    }

    @Test
    void exposesHolidayCalendarsBySystem() {
        Map<BengaliDate, BengaliHoliday> bangladeshHolidays = BengaliHoliday.forYear(1431, BengaliCalendarSystem.BANGLADESH_REVISED);
        Map<BengaliDate, BengaliHoliday> westBengalHolidays = BengaliHoliday.forYear(1431, BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL);

        assertEquals(BengaliHoliday.POHELA_BOISHAKH, bangladeshHolidays.get(BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.BANGLADESH_REVISED)));
        assertEquals(BengaliHoliday.POHELA_BOISHAKH, westBengalHolidays.get(BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1, BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL)));
        assertEquals("শহীদ দিবস", BengaliHoliday.SHAHEED_DIBOSH.getDisplayName(BengaliLocale.BENGALI));
        assertTrue(bangladeshHolidays.containsValue(BengaliHoliday.SHAHEED_DIBOSH));
        assertFalse(westBengalHolidays.containsValue(BengaliHoliday.SHAHEED_DIBOSH));
    }

    @Test
    void supportsChangingAndOverridingTheDefaultCalendarSystem() throws Exception {
        assertEquals(BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL, BengaliCalendar.getDefault());
        assertEquals(BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL, BengaliCalendar.from(LocalDate.of(2024, 4, 14)).getCalendarSystem());

        BengaliCalendar.setDefault(BengaliCalendarSystem.BANGLADESH_REVISED);
        assertEquals(BengaliCalendarSystem.BANGLADESH_REVISED, BengaliCalendar.getDefault());
        assertEquals(BengaliCalendarSystem.BANGLADESH_REVISED, BengaliCalendar.from(LocalDate.of(2024, 4, 14)).getCalendarSystem());

        System.setProperty("bengalicalendar.default.system", "BANGLADESH_REVISED");
        try (URLClassLoader loader = new URLClassLoader(classpathUrls(), ClassLoader.getPlatformClassLoader())) {
            Class<?> calendarClass = Class.forName("com.tapadyuti.bengalicalendar.BengaliCalendar", true, loader);
            Object defaultSystem = calendarClass.getMethod("getDefault").invoke(null);
            assertEquals("BANGLADESH_REVISED", defaultSystem.toString());
        }
    }

    private static void assertBangladeshRevised(LocalDate gregorianDate, int year, BengaliMonth month, int day) {
        BengaliDate actual = BengaliCalendar.from(gregorianDate, BengaliCalendarSystem.BANGLADESH_REVISED);
        assertEquals(BengaliDate.of(year, month, day, BengaliCalendarSystem.BANGLADESH_REVISED), actual);
        assertEquals(gregorianDate, actual.toGregorian());
    }

    private static URL[] classpathUrls() {
        return List.of(System.getProperty("java.class.path").split(System.getProperty("path.separator"))).stream()
            .map(Path::of)
            .map(path -> {
                try {
                    return path.toUri().toURL();
                } catch (Exception e) {
                    throw new IllegalStateException("Unable to convert classpath entry to URL: " + path, e);
                }
            })
            .toArray(URL[]::new);
    }
}
