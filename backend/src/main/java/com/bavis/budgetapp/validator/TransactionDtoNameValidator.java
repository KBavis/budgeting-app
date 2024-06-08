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
@Log4j2
public class TransactionDtoNameValidator implements ConstraintValidator<TransactionDtoValidName, TransactionDto> {

    private final String REGEX = "^(?=.{1,29}$)[A-Z0-9][a-zA-Z0-9'\\s-]+[a-zA-Z0-9'-]*$";


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

        boolean validName = name.matches(REGEX);
        log.debug("Name passed in via TransactionDto [{}] validity: {}",transactionDto, validName);
        return validName;
    }
}
