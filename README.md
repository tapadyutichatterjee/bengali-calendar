# Bengali Calendar (Java 21)

A Java library for converting between Gregorian and Bengali (Bangla) dates with support for:

- `BANGLADESH_REVISED` calendar
- `WEST_BENGAL_TRADITIONAL` calendar
- formatting and parsing
- Bengali/English locale output
- `java.time` chronology integration

## Installation

```xml
<dependency>
  <groupId>com.tapadyuti</groupId>
  <artifactId>bengali-calendar</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Default behavior

By default, no-arg/system-omitting APIs use:

`BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL`

That means these use WEST Bengal by default:

```java
BengaliCalendar.today();
BengaliCalendar.from(LocalDate.now());
BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1);
BengaliDateFormatter.parse("01-01-1431", "dd-MM-yyyy");
```

### Override default globally

```java
BengaliCalendar.setDefault(BengaliCalendarSystem.BANGLADESH_REVISED);
```

### Override default with JVM property

```bash
java -Dbengalicalendar.default.system=BANGLADESH_REVISED ...
```

Accepted property values:

- `BANGLADESH_REVISED` or `BD`
- `WEST_BENGAL_TRADITIONAL` or `WB`

### Explicit system always wins

```java
BengaliDate bdDate = BengaliCalendar.from(
    LocalDate.of(2024, 4, 14),
    BengaliCalendarSystem.BANGLADESH_REVISED
);
```

## Quick start

```java
import com.tapadyuti.bengalicalendar.*;
import java.time.LocalDate;

public class Example {
    public static void main(String[] args) {
        BengaliDate today = BengaliCalendar.today();
        System.out.println(today);

        BengaliDate converted = BengaliCalendar.from(LocalDate.of(2024, 4, 14));
        System.out.println(converted);

        LocalDate gregorian = BengaliCalendar.toGregorian(converted);
        System.out.println(gregorian);
    }
}
```

## Conversion examples

### Bangladesh Revised conversion

```java
BengaliDate bd = BengaliCalendar.from(
    LocalDate.of(2024, 4, 14),
    BengaliCalendarSystem.BANGLADESH_REVISED
);
// 1431-BAISHAKH-1 (BD system)
```

### West Bengal Traditional conversion

```java
BengaliDate wb = BengaliCalendar.from(
    LocalDate.of(2024, 4, 14),
    BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL
);
```

## Formatting and parsing

### Supported pattern tokens

| Token | Meaning |
|---|---|
| `d` | Day of month (no padding) |
| `dd` | Day of month (zero-padded) |
| `M` | Month number |
| `MM` | Month number (zero-padded) |
| `MMM` | Month abbreviation |
| `MMMM` | Full month name |
| `y` | Year |
| `yyyy` | Year (4-digit) |
| `E` | Calendar system (`BD` / `WB`) |

### Format in English

```java
BengaliDate date = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1);
String text = date.format("dd MMMM yyyy E", BengaliLocale.ENGLISH);
// 01 Baishakh 1431 WB
```

### Format in Bengali script

```java
String textBn = date.format("dd MMMM yyyy E", BengaliLocale.BENGALI);
// ০১ বৈশাখ ১৪৩১ WB
```

### Parse text

```java
BengaliDate parsed = BengaliDateFormatter.parse(
    "01-01-1431",
    "dd-MM-yyyy",
    BengaliCalendarSystem.BANGLADESH_REVISED,
    BengaliLocale.ENGLISH
);
```

### Parse Bengali numerals

```java
BengaliDate parsedBn = BengaliDateFormatter.parse(
    "০১-০১-১৪৩১",
    "dd-MM-yyyy",
    BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL,
    BengaliLocale.BENGALI
);
```

## Date arithmetic

```java
BengaliDate date = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1);

BengaliDate plus10Days = date.plusDays(10);
BengaliDate plus1Month = date.plusMonths(1);
BengaliDate plus1Year  = date.plusYears(1);

BengaliDate minus5Days = date.minusDays(5);
```

## Year-month helper

```java
BengaliYearMonth ym = BengaliYearMonth.of(1431, BengaliMonth.BAISHAKH);
int length = ym.lengthOfMonth();
BengaliDate day12 = ym.atDay(12);
```

## Date range helper

```java
BengaliDate start = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1);
BengaliDate end = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 7);
BengaliDateRange range = BengaliDateRange.of(start, end);

boolean contains = range.contains(BengaliDate.of(1431, BengaliMonth.BAISHAKH, 3));
long days = range.lengthInDays();          // inclusive count
long streamCount = range.stream().count(); // 7
```

## Holidays

```java
BengaliDate d = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1);
d.getHoliday().ifPresent(h -> System.out.println(h.getDisplayName(BengaliLocale.ENGLISH)));
// Pohela Boishakh
```

Get all holidays for a year/system:

```java
Map<BengaliDate, BengaliHoliday> holidays =
    BengaliHoliday.forYear(1431, BengaliCalendarSystem.BANGLADESH_REVISED);
```

## `java.time` chronology integration

```java
import java.time.chrono.Chronology;

Chronology chronology = BengaliChronology.INSTANCE;
// Also resolvable by id in environments where service loading is enabled:
Chronology byId = Chronology.of("bengali");
```

## Build and test

```bash
export PATH=/opt/homebrew/bin:$PATH
cd /path/to/bengali-calendar
mvn test
mvn verify
```

## Release build (no publish)

```bash
mvn -Prelease verify
```

## Maven Central publishing

Publishing is configured for Sonatype Central Publisher Portal.

- local release/deploy command:

```bash
mvn -Prelease deploy
```

- final publication is completed in the Sonatype Central Portal UI (manual publish flow).

