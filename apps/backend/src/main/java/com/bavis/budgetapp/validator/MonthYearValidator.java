package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.MonthYearValidMonthAndYear;
import com.bavis.budgetapp.model.MonthYear;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Validation class to ensure MonthYear is valid
 *
 * @author Kellen Bavis
 */
public class MonthYearValidator implements ConstraintValidator<MonthYearValidMonthAndYear, MonthYear> {

    private static final int MIN_YEAR = 1;
    private static final int MAX_YEAR = 9999;

    private static final Set<String> MONTHS = new HashSet<>();

    static {
        MONTHS.add("JANUARY");
        MONTHS.add("FEBRUARY");
        MONTHS.add("MARCH");
        MONTHS.add("APRIL");
        MONTHS.add("MAY");
        MONTHS.add("JUNE");
        MONTHS.add("JULY");
        MONTHS.add("AUGUST");
        MONTHS.add("SEPTEMBER");
        MONTHS.add("OCTOBER");
        MONTHS.add("NOVEMBER");
        MONTHS.add("DECEMBER");
    }


    @Override
    public void initialize(MonthYearValidMonthAndYear constraintAnnotation) {
        //do nothing
    }

    /**
     * Determine if our MonthYear is valid
     *
     * @param monthYear
     *          - MonthYear to validate
     * @param constraintValidatorContext
     *          - constraintValidatorContext
     * @return
     *          - boolean
     */
    @Override
    public boolean isValid(MonthYear monthYear, ConstraintValidatorContext constraintValidatorContext) {
        if(monthYear == null) { return false; }

        return isValidMonth(monthYear.getMonth()) && isValidYear(monthYear.getYear());
    }

    /**
     * Ensures month is valid and the value is fully uppercase
     *
     * @param month
     *          - Month to validate
     * @return
     *          - boolean
     */
    private static boolean isValidMonth(String month) {
        if(StringUtils.isBlank(month)) { return false; }

        if(!StringUtils.isAllUpperCase(month)) { return false; }

        return MONTHS.contains(month);
    }

    /**
     * Ensures year is within given range
     *
     * @param year
     *          - year to check
     * @return
     *          - boolean
     */
    private static boolean isValidYear(int year) {
        return year >= MIN_YEAR && year <= MAX_YEAR;
    }
}
