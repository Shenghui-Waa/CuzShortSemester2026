package com.cuzssp.campussecondhandtradingplatformbackend.common.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public final class UtcTime {

    private UtcTime() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public static LocalDate today() {
        return LocalDate.now(ZoneOffset.UTC);
    }
}
