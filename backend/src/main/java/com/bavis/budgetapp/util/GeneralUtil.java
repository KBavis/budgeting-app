package com.bavis.budgetapp.util;

import com.bavis.budgetapp.constants.TimeType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Kellen Bavis
 *
 * Utility class for our general operations used through codebase
 */
public class GeneralUtil {

    /**
     * Functionality to add specific measure of time to a Date
     *
     * @param dateTime
     *          - Date/Time object to add a measure of time to
     * @param amount
     *          - The amount of time to be added on
     * @param timeType
     *          - The type of time to be added (HOURS, MINUTES, SECONDS, etc)
     * @return
     *          - New LocalDateTime with the added time
     */
    public static LocalDateTime addTimeToDate(LocalDateTime dateTime, long amount, TimeType timeType){
        return switch (timeType) {
            case HOURS -> dateTime.plusHours(amount);
            case MINUTES -> dateTime.plusMinutes(amount);
            case SECONDS -> dateTime.plusSeconds(amount);
        };
    }

    /**
     * Functionality to transform a LocalDateTime object into a plain Date object
     *
     * @param dateTime
     *              - LocalDateTime object to be transformed
     * @return
     *              - Newly created Date object from LocalDateTime object
     */
    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Functionality to determine if a date is within the current month a year
     *
     * @param dateToCheck
     *          - the date to validate
     * @return
     *      - validly of date
     */
    public static boolean isDateInCurrentMonth(LocalDate dateToCheck){
        if(dateToCheck == null) {
            return false;
        }

        LocalDate currentDate = LocalDate.now();

        //Current Month & Year
        int currentYear = currentDate.getYear();
        int currentMonth =  currentDate.getMonthValue();

        return currentYear == dateToCheck.getYear() && currentMonth == dateToCheck.getMonthValue();
    }


    public static String nullSafeToLowerCaseOrEmpty(String input) {
        return input != null ? input.toLowerCase() : "";
    }

}
