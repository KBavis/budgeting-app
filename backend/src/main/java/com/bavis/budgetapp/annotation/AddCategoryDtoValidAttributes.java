package com.bavis.budgetapp.annotation;

import com.bavis.budgetapp.validator.AddCategoryDtoAttributeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = AddCategoryDtoAttributeValidator.class)
public @interface AddCategoryDtoValidAttributes {
    String message() default "The updated Categories or the created Category is null.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
