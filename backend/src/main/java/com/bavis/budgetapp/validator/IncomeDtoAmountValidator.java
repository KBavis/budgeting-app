package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.IncomeDtoValidAmount;
import com.bavis.budgetapp.dto.IncomeDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IncomeDtoAmountValidator implements ConstraintValidator<IncomeDtoValidAmount, IncomeDto> {

    @Override
    public void initialize(IncomeDtoValidAmount constraintAnnotation) {
        //nothing to initalize
    }

    @Override
    public boolean isValid(IncomeDto incomeDto, ConstraintValidatorContext constraintValidatorContext) {
        return incomeDto != null && incomeDto.getAmount() > 0;
    }
}
