package com.bavis.budgetapp.validator;


import com.bavis.budgetapp.annotation.TransactionDtoValidDate;
import com.bavis.budgetapp.dto.TransactionDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;

/**
 * Validator class to ensure a Transaction DTO has a valid date associated with it
 *
 * @author Kellen Bavis
 */
@Log4j2
public class TransactionDtoDateValidator implements ConstraintValidator<TransactionDtoValidDate, TransactionDto> {

    @Override
    public void initialize(TransactionDtoValidDate constraintAnnotation) {
        //do nothing
    }

    /**
     * Ensures that the passed in date is not null
     *
     * @param transactionDto
     *          - transactionDto to validate
     * @param constraintValidatorContext
     *          - constraintContext
     * @return
     *          - validity of date associated with TransactionDto
     */
    @Override
    public boolean isValid(TransactionDto transactionDto, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate localDate = transactionDto.getDate();
        LocalDate currentDate = LocalDate.now();

        if(localDate != null) {
            boolean dateValidity = !localDate.isAfter(currentDate);
            log.info("The provided TransactionDto date validity: {}", dateValidity);
            return dateValidity;
        }
        log.info("TransactionDto provided date is null, and thus invalid");
        return false;
    }
}
