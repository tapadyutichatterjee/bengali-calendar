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
 * Supported Bengali calendar systems.
 */
public enum BengaliCalendarSystem {
    /**
     * Bangladesh Revised calendar system.
     * <p>
     * This is the modern arithmetic form used in Bangladesh, with a fixed new year on 14 April and deterministic month
     * lengths, including the leap-year adjustment applied to Falgun.
     * </p>
     */
    BANGLADESH_REVISED,

    /**
     * West Bengal Traditional calendar system.
     * <p>
     * This variant follows traditional sankranti boundaries derived from astronomical calculations and is commonly used
     * in West Bengal and related almanac traditions.
     * </p>
     */
    WEST_BENGAL_TRADITIONAL
}
