package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestSamePasswords;
import com.bavis.budgetapp.dto.AuthRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;


/**
 * @author Kellen Bavis
 *
 * Validation class for ensuring AuthRequest correctly confirm their password
 *      - Each password specified in AuthRequest are the same
 */
@Log4j2
public class AuthRequestSamePasswordsValidator implements ConstraintValidator<AuthRequestSamePasswords, AuthRequestDto> {

    @Override
    public void initialize(AuthRequestSamePasswords constraintAnnotation) {
        //nothing to initalize
    }

    /**
     * Validates each of the passed in 'password' attributes for our AuthRequestDto match
     *
     * @param authRequestDto
     *          - AuthRequestDto to validate 'password' attributes for
     * @param constraintValidatorContext
     *          - provides additional context information for our constraint
     * @return
     *          - validity of the 'password' attribute's in the AuthRequestDto
     */
    @Override
    public boolean isValid(AuthRequestDto authRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        String passwordOne = authRequestDto.getPasswordOne();
        String passwordTwo = authRequestDto.getPasswordTwo();

        //Password fields must be filled out
        if(passwordOne == null || passwordOne.isEmpty() || passwordTwo == null || passwordTwo.isEmpty()){
            return false;
        }

        boolean passwordsMatch = passwordTwo.equals(passwordOne);
        log.debug("Validity of the confirmation of user's registered password: [{}]", passwordsMatch);
        return passwordsMatch;
    }
}
