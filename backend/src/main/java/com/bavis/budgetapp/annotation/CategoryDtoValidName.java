package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.CategoryDtoNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kellen Bavis
 *
 * Annotation for validating that a valid name was passed for a Category
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CategoryDtoNameValidator.class)
public @interface CategoryDtoValidName {

    String message() default "Category name must be a valid combination of letters between 1 and 50 characters.";


    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
