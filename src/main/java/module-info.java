module com.tapadyuti.bengalicalendar {
    requires java.base;

    exports com.tapadyuti.bengalicalendar;
    exports com.tapadyuti.bengalicalendar.exception;

    provides java.time.chrono.Chronology
        with com.tapadyuti.bengalicalendar.BengaliChronology;
}
