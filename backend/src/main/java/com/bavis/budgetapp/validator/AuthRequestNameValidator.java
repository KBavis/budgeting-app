package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestValidName;
import com.bavis.budgetapp.dto.AuthRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AuthRequestNameValidator implements ConstraintValidator<AuthRequestValidName, AuthRequestDto> {

    //Validates proper name format
    String nameRegex = "^[A-Z][a-zA-Z'-]+(\\s[A-Z][a-zA-Z'-]+)*\\s[A-Z][a-zA-Z'-]+$";
    @Override
    public void initialize(AuthRequestValidName constraintAnnotation) {
        //nothing to initalize
    }

    @Override
    public boolean isValid(AuthRequestDto authRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        String name = authRequestDto.getName();

        if(name == null || name.isEmpty()) {
            return false;
        }

        boolean validName = name.matches(nameRegex);
        log.debug("AuthRequest Name Passed In Validity: {}", validName);
        return validName;
    }
}
