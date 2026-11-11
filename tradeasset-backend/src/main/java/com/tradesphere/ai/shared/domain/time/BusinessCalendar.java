package com.tradesphere.ai.shared.domain.time;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

/**
 * Business calendar for the GCC (REQ-TIM-001). Instants are UTC; calendar dates such as "today"
 * for an expiry check are evaluated in Asia/Riyadh. The banking week runs Sunday to Thursday;
 * Friday and Saturday are not banking days. Public holidays are not modelled.
 */
public final class BusinessCalendar {

    public static final ZoneId ZONE = ZoneId.of("Asia/Riyadh");

    private BusinessCalendar() {
    }

    public static LocalDate today(Clock clock) {
        return LocalDate.ofInstant(clock.instant(), ZONE);
    }

    public static LocalDate dateOf(Instant instant) {
        return LocalDate.ofInstant(instant, ZONE);
    }

    public static boolean isBankingDay(LocalDate date) {
        DayOfWeek d = date.getDayOfWeek();
        return d != DayOfWeek.FRIDAY && d != DayOfWeek.SATURDAY;
    }

    /** The date that is {@code days} banking days after {@code start} (UCP 600 art. 14(b), 16(d)). */
    public static LocalDate plusBankingDays(LocalDate start, int days) {
        LocalDate date = start;
        int added = 0;
        while (added < days) {
            date = date.plusDays(1);
            if (isBankingDay(date)) {
                added++;
            }
        }
        return date;
    }

    /** Calendar days from {@code from} to {@code to}; negative when {@code to} is earlier. */
    public static long daysBetween(LocalDate from, LocalDate to) {
        return ChronoUnit.DAYS.between(from, to);
    }
}
