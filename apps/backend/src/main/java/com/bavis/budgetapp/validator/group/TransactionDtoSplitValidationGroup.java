package com.bavis.budgetapp.validator.group;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Kellen Bavis
 *
 * Validation group for the association of validation annotations for our TransactionDto
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionDtoSplitValidationGroup {
}
