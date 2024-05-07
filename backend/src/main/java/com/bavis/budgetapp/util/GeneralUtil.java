package com.bavis.budgetapp.util;

import com.bavis.budgetapp.constants.TimeType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 *
 */
public class GeneralUtil {

    public static LocalDateTime addTimeToDate(LocalDateTime dateTime, long amount, TimeType timeType){
        return switch (timeType) {
            case HOURS -> dateTime.plusHours(amount);
            case MINUTES -> dateTime.plusMinutes(amount);
            case SECONDS -> dateTime.plusSeconds(amount);
            default -> throw new IllegalArgumentException("Unsupported Time Type: " + timeType);
        };
    }

    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
