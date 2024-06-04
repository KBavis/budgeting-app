package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.TransactionDtoValidAmount;
import com.bavis.budgetapp.dto.TransactionDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

/**
 * Validator class to ensure a Transaction DTO has a positive amount
 *
 * @author Kellen Bavis
 */
@Log4j2
public class TransactionDtoAmountValidator implements ConstraintValidator<TransactionDtoValidAmount, TransactionDto> {

    @Override
    public void initialize(TransactionDtoValidAmount constraintAnnotation) {
        //do nothing
    }

    @Override
    public boolean isValid(TransactionDto transactionDto, ConstraintValidatorContext constraintValidatorContext) {
        double amount = transactionDto.getUpdatedAmount();
        return amount > 0;
    }
}
