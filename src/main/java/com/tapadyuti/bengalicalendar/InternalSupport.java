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

import java.lang.reflect.Method;
import java.time.LocalDate;

final class InternalSupport {
    private static final Method BD_FROM = method("com.tapadyuti.bengalicalendar.internal.BangladeshRevisedConverter", "fromGregorian", LocalDate.class);
    private static final Method BD_TO = method("com.tapadyuti.bengalicalendar.internal.BangladeshRevisedConverter", "toGregorian", BengaliDate.class);
    private static final Method BD_MONTH_LENGTH = method("com.tapadyuti.bengalicalendar.internal.BangladeshRevisedConverter", "lengthOfMonth", int.class, BengaliMonth.class);
    private static final Method BD_YEAR_LENGTH = method("com.tapadyuti.bengalicalendar.internal.BangladeshRevisedConverter", "lengthOfYear", int.class);
    private static final Method WB_FROM = method("com.tapadyuti.bengalicalendar.internal.WestBengalConverter", "fromGregorian", LocalDate.class);
    private static final Method WB_TO = method("com.tapadyuti.bengalicalendar.internal.WestBengalConverter", "toGregorian", BengaliDate.class);
    private static final Method WB_MONTH_LENGTH = method("com.tapadyuti.bengalicalendar.internal.WestBengalConverter", "monthLength", int.class, BengaliMonth.class);
    private static final Method WB_YEAR_LENGTH = method("com.tapadyuti.bengalicalendar.internal.WestBengalConverter", "yearLength", int.class);

    private InternalSupport() {
    }

    static BengaliDate fromBangladeshRevised(LocalDate date) {
        return invoke(BD_FROM, date);
    }

    static LocalDate toBangladeshRevised(BengaliDate date) {
        return invoke(BD_TO, date);
    }

    static int bangladeshMonthLength(int year, BengaliMonth month) {
        return invoke(BD_MONTH_LENGTH, year, month);
    }

    static int bangladeshYearLength(int year) {
        return invoke(BD_YEAR_LENGTH, year);
    }

    static BengaliDate fromWestBengalTraditional(LocalDate date) {
        return invoke(WB_FROM, date);
    }

    static LocalDate toWestBengalTraditional(BengaliDate date) {
        return invoke(WB_TO, date);
    }

    static int westBengalMonthLength(int year, BengaliMonth month) {
        return invoke(WB_MONTH_LENGTH, year, month);
    }

    static int westBengalYearLength(int year) {
        return invoke(WB_YEAR_LENGTH, year);
    }

    @SuppressWarnings("unchecked")
    private static <T> T invoke(Method method, Object... args) {
        try {
            return (T) method.invoke(null, args);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to invoke internal converter", e);
        }
    }

    private static Method method(String className, String methodName, Class<?>... parameterTypes) {
        try {
            Class<?> type = Class.forName(className);
            Method method = type.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to access internal converter method " + className + "#" + methodName, e);
        }
    }
}
