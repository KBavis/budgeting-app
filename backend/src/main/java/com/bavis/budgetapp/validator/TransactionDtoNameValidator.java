package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.TransactionDtoValidName;
import com.bavis.budgetapp.dto.TransactionDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

/**
 * Validator class to ensure the TransactionDto has a valid name associated with it
 *
 * @author Kellen Bavis
 */
public class TransactionDtoNameValidator implements ConstraintValidator<TransactionDtoValidName, TransactionDto> {

    private final String REGEX = "^[\\p{L}\\p{N}\\p{P}\\p{S}\\s]{1,29}$";

    @Override
    public void initialize(TransactionDtoValidName constraintAnnotation) {
        //do nothing
    }

    @Override
    public boolean isValid(TransactionDto transactionDto, ConstraintValidatorContext constraintValidatorContext) {
        String name = transactionDto.getUpdatedName();

        if(name == null || name.isEmpty()) {
            return false;
        }

        return name.matches(REGEX);
    }
}
