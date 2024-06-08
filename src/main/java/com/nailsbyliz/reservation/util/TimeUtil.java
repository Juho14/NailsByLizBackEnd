package com.nailsbyliz.reservation.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {

    private static final ZoneId HELSINKI_ZONE = ZoneId.of("Europe/Helsinki");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static String formatToHelsinkiTime(LocalDateTime gmtTime) {
        LocalDateTime helsinkiTime = convertToHelsinkiTime(gmtTime);
        return helsinkiTime.format(FORMATTER);
    }

    public static LocalDateTime convertToHelsinkiTime(LocalDateTime gmtTime) {
        ZonedDateTime helsinkiTime = gmtTime.atZone(ZoneId.of("GMT")).withZoneSameInstant(HELSINKI_ZONE);
        return helsinkiTime.toLocalDateTime();
    }
}