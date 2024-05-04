package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestSamePasswords;
import com.bavis.budgetapp.dto.AuthRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class AuthRequestSamePasswordsValidator implements ConstraintValidator<AuthRequestSamePasswords, AuthRequestDto> {

    @Override
    public void initialize(AuthRequestSamePasswords constraintAnnotation) {
        //nothing to initalize
    }

    @Override
    public boolean isValid(AuthRequestDto authRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        String passwordOne = authRequestDto.getPasswordOne();
        String passwordTwo = authRequestDto.getPasswordTwo();

        //Password fields must be filled out
        if(passwordOne == null || passwordOne.isEmpty() || passwordTwo == null || passwordTwo.isEmpty()){
            return false;
        }

        boolean passwordsMatch = passwordTwo.equals(passwordOne);
        log.debug("AuthRequest Passwords Match: {}", passwordsMatch);
        return passwordsMatch;
    }
}
