# Bengali Calendar

[![Build](https://github.com/tapadyutichatterjee/bengali-calendar/actions/workflows/ci.yml/badge.svg?branch=master&event=push)](https://github.com/tapadyutichatterjee/bengali-calendar/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.tapadyuti/bengali-calendar?label=Maven%20Central)](https://central.sonatype.com/artifact/com.tapadyuti/bengali-calendar)
[![Javadocs](https://javadoc.io/badge2/com.tapadyuti/bengali-calendar/javadoc.svg)](https://javadoc.io/doc/com.tapadyuti/bengali-calendar)
[![Java 21](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![License](https://img.shields.io/github/license/tapadyutichatterjee/bengali-calendar)](https://github.com/tapadyutichatterjee/bengali-calendar/blob/master/LICENSE)

A Java 21 library for converting between Gregorian and Bengali (Bangla) dates. It supports both
the Bangladesh Revised and West Bengal Traditional calendar systems while fitting naturally into
Java's modern date and time APIs.

## Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Calendar systems and defaults](#calendar-systems-and-defaults)
- [Quick start](#quick-start)
- [Usage guide](#usage-guide)
- [Documentation](#documentation)
- [Calendar accuracy and scope](#calendar-accuracy-and-scope)
- [Build from source](#build-from-source)
- [Contributing](#contributing)
- [Support](#support)
- [License](#license)

## Features

- Bidirectional conversion between Gregorian and Bengali dates
- Bangladesh Revised and West Bengal Traditional calendar systems
- Formatting and parsing in English and Bengali script, including Bengali numerals
- Date arithmetic, inclusive date ranges, year-month helpers, weekdays, seasons, and selected holidays
- `java.time.chrono` integration through `BengaliChronology` and `ChronoLocalDate`
- Java Platform Module System support through `com.tapadyuti.bengalicalendar`
- No runtime dependencies outside the Java standard library

## Requirements

- JDK 21 or newer
- Maven, Gradle, or another build tool that can resolve dependencies from Maven Central

## Installation

### Maven

```xml
<dependency>
  <groupId>com.tapadyuti</groupId>
  <artifactId>bengali-calendar</artifactId>
  <version>1.0.0</version>
</dependency>
```

### Gradle (Kotlin DSL)

```kotlin
dependencies {
    implementation("com.tapadyuti:bengali-calendar:1.0.0")
}
```

The current release and all published versions are available on
[Maven Central](https://central.sonatype.com/artifact/com.tapadyuti/bengali-calendar).

## Calendar systems and defaults

| Calendar system | Behavior |
|---|---|
| `BANGLADESH_REVISED` | Modern arithmetic calendar used in Bangladesh, with a fixed Bengali New Year on 14 April and a leap-year adjustment for Falgun |
| `WEST_BENGAL_TRADITIONAL` | Traditional calendar based on Sankranti boundaries derived from the library's astronomical calculation |

By default, no-arg/system-omitting APIs use:

`BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL`

That means these use the West Bengal Traditional system by default:

```java
BengaliCalendar.today();
BengaliCalendar.from(LocalDate.now());
BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1);
BengaliDateFormatter.parse("01-01-1431", "dd-MM-yyyy");
```

### Change the process-wide default

```java
BengaliCalendar.setDefault(BengaliCalendarSystem.BANGLADESH_REVISED);
```

The default is stored process-wide. Applications that work with more than one Bengali calendar
system should prefer the overloads that accept a `BengaliCalendarSystem` explicitly.

### Configure the default with a JVM property

```bash
java -Dbengalicalendar.default.system=BANGLADESH_REVISED ...
```

Accepted property values:

- `BANGLADESH_REVISED` or `BD`
- `WEST_BENGAL_TRADITIONAL` or `WB`

### Select a system per operation

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

## Usage guide

### Conversion examples

#### Bangladesh Revised conversion

```java
BengaliDate bd = BengaliCalendar.from(
    LocalDate.of(2024, 4, 14),
    BengaliCalendarSystem.BANGLADESH_REVISED
);
// Bengali year 1431, Baishakh 1
```

#### West Bengal Traditional conversion

```java
BengaliDate wb = BengaliCalendar.from(
    LocalDate.of(2024, 4, 14),
    BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL
);
```

### Formatting and parsing

#### Supported pattern tokens

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

#### Format in English

```java
BengaliDate date = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1);
String text = date.format("dd MMMM yyyy E", BengaliLocale.ENGLISH);
// 01 Baishakh 1431 WB
```

#### Format in Bengali script

```java
String textBn = date.format("dd MMMM yyyy E", BengaliLocale.BENGALI);
// ০১ বৈশাখ ১৪৩১ WB
```

#### Parse text

```java
BengaliDate parsed = BengaliDateFormatter.parse(
    "01-01-1431",
    "dd-MM-yyyy",
    BengaliCalendarSystem.BANGLADESH_REVISED,
    BengaliLocale.ENGLISH
);
```

#### Parse Bengali numerals

```java
BengaliDate parsedBn = BengaliDateFormatter.parse(
    "০১-০১-১৪৩১",
    "dd-MM-yyyy",
    BengaliCalendarSystem.WEST_BENGAL_TRADITIONAL,
    BengaliLocale.BENGALI
);
```

### Date arithmetic

```java
BengaliDate date = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1);

BengaliDate plus10Days = date.plusDays(10);
BengaliDate plus1Month = date.plusMonths(1);
BengaliDate plus1Year  = date.plusYears(1);

BengaliDate minus5Days = date.minusDays(5);
```

### Year-month helper

```java
BengaliYearMonth ym = BengaliYearMonth.of(1431, BengaliMonth.BAISHAKH);
int length = ym.lengthOfMonth();
BengaliDate day12 = ym.atDay(12);
```

### Date range helper

```java
BengaliDate start = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 1);
BengaliDate end = BengaliDate.of(1431, BengaliMonth.BAISHAKH, 7);
BengaliDateRange range = BengaliDateRange.of(start, end);

boolean contains = range.contains(BengaliDate.of(1431, BengaliMonth.BAISHAKH, 3));
long days = range.lengthInDays();          // inclusive count
long streamCount = range.stream().count(); // 7
```

### Holidays

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

### `java.time` chronology integration

```java
import java.time.chrono.Chronology;

Chronology chronology = BengaliChronology.INSTANCE;
// Also resolvable by id in environments where service loading is enabled:
Chronology byId = Chronology.of("Bengali");
```

Chronology discovery identifiers:

- Chronology ID: `Bengali`
- Calendar type: `bengali`
- Lookup: `Chronology.of("Bengali")`

## Documentation

- [API documentation](https://javadoc.io/doc/com.tapadyuti/bengali-calendar)
- [Maven Central artifact](https://central.sonatype.com/artifact/com.tapadyuti/bengali-calendar)
- [Releases](https://github.com/tapadyutichatterjee/bengali-calendar/releases)
- [Source repository](https://github.com/tapadyutichatterjee/bengali-calendar)

## Calendar accuracy and scope

- Bangladesh Revised conversions use the deterministic modern calendar rules implemented by the library.
- West Bengal Traditional conversions use calculated Sankranti boundaries. Panjika traditions and
  authoritative almanacs can differ, particularly for historical, future, religious, or ceremonial dates.
- The bundled holiday data is intentionally selective and is not a statutory or exhaustive regional
  holiday calendar.

For culturally or legally significant use cases, validate results against an appropriate authoritative source.

## Build from source

Clone the repository, make sure JDK 21 and Maven are available, and run:

```bash
mvn verify
```

To generate the release artifacts locally without publishing them:

```bash
mvn -Prelease verify
```

## Contributing

Contributions are welcome. Before opening a pull request:

1. Search the [existing issues](https://github.com/tapadyutichatterjee/bengali-calendar/issues)
   and open one for substantial changes.
2. Fork the repository and create a focused branch.
3. Add or update tests for behavioral changes.
4. Run `mvn verify`.
5. Open a pull request that explains the problem, the approach, and any calendar system affected.

Changes to conversion rules should include authoritative references and representative Gregorian/Bengali
date pairs so the behavior can be reviewed and tested.

## Support

Use [GitHub Issues](https://github.com/tapadyutichatterjee/bengali-calendar/issues) for bug reports,
feature requests, and documentation problems. Include the library version, Java version, calendar system,
sample input, expected result, and actual result whenever possible.

## License

Licensed under the [Apache License 2.0](LICENSE).
