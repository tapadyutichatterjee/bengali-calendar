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

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Inclusive range of {@link BengaliDate} values.
 * <p>
 * The range is iterable and can also be consumed as a sequential stream. Both endpoints are included.
 * </p>
 */
public final class BengaliDateRange implements Iterable<BengaliDate> {
    private final BengaliDate start;
    private final BengaliDate end;

    /**
     * Creates an inclusive date range.
     *
     * @param start the first date in the range
     * @param end the last date in the range
     * @throws NullPointerException if either endpoint is {@code null}
     * @throws IllegalArgumentException if {@code start} is after {@code end}
     */
    public BengaliDateRange(BengaliDate start, BengaliDate end) {
        this.start = Objects.requireNonNull(start, "start");
        this.end = Objects.requireNonNull(end, "end");
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start must be before or equal to end");
        }
    }

    /**
     * Creates an inclusive date range.
     *
     * @param start the first date in the range
     * @param end the last date in the range
     * @return the range instance
     */
    public static BengaliDateRange of(BengaliDate start, BengaliDate end) {
        return new BengaliDateRange(start, end);
    }

    /**
     * Returns whether the supplied date lies within this inclusive range.
     *
     * @param date the date to test
     * @return {@code true} if the date is between the start and end, inclusive
     */
    public boolean contains(BengaliDate date) {
        return !date.isBefore(start) && !date.isAfter(end);
    }

    /**
     * Returns the inclusive length of the range in days.
     *
     * @return the number of dates contained in the range
     */
    public long lengthInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(start.toGregorian(), end.toGregorian()) + 1;
    }

    /**
     * Returns a sequential stream traversing the range from start to end.
     *
     * @return a stream of Bengali dates in ascending order
     */
    public Stream<BengaliDate> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED), false);
    }

    /**
     * Returns an iterator traversing the range from start to end.
     *
     * @return an iterator over the inclusive range
     */
    @Override
    public Iterator<BengaliDate> iterator() {
        return new Iterator<>() {
            private BengaliDate current = start;

            @Override
            public boolean hasNext() {
                return !current.isAfter(end);
            }

            @Override
            public BengaliDate next() {
                BengaliDate value = current;
                current = current.plusDays(1);
                return value;
            }
        };
    }
}
