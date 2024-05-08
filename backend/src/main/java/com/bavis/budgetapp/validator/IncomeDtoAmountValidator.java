package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.IncomeDtoValidAmount;
import com.bavis.budgetapp.dto.IncomeDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

/**
 * @author Kellen Bavis
 *
 * Validation class for ensuring that the passed in Income amount is valid
 *          - not null
 *          - not a negative value
 */
@Log4j2
public class IncomeDtoAmountValidator implements ConstraintValidator<IncomeDtoValidAmount, IncomeDto> {

    @Override
    public void initialize(IncomeDtoValidAmount constraintAnnotation) {
        //nothing to initalize
    }

    /**
     * Validates the income amount passed in for IncomeDto
     *
     * @param incomeDto
     *          - IncomeDto to validate amount for
     * @param constraintValidatorContext
     *          - provides additional context information for our constraint
     * @return
     *          - validity of the passed in Income amount for IncomeDto
     */
    @Override
    public boolean isValid(IncomeDto incomeDto, ConstraintValidatorContext constraintValidatorContext) {
        boolean validity = incomeDto != null && incomeDto.getAmount() > 0;
        log.debug("Validity of the income amount passed in for IncomeDto: [{}]", validity);
        return validity;
    }
}
