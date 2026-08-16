package com.bavis.budgetapp.constants;

import java.time.LocalDate;

/**
 * @author Kellen Bavis
 * 
 * Constants for Available and Effective Dating (Validity Time / Lifespan)
 */
public class TemporalConstants {
    public static final LocalDate BEGINNING_OF_TIME = LocalDate.of(1970, 1, 1);
    public static final LocalDate END_OF_TIME = LocalDate.of(9999, 12, 31);
}
