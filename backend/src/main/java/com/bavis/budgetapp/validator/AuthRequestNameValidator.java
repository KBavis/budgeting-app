package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AuthRequestValidName;
import com.bavis.budgetapp.dto.AuthRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;

/**
 * @author Kellen Bavis
 *
 * Validation class for ensuring AuthRequest contains a valid name
 *      - Name must start with upper case letter
 *      - Consists of one or more parts (seperated by single space)
 *      - Can contain any combination of lowercase/uppercase letters and apostrophes
 */
@Log4j2
public class AuthRequestNameValidator implements ConstraintValidator<AuthRequestValidName, AuthRequestDto> {

    //Validates proper name format
    String nameRegex = "^[A-Z][a-zA-Z'-]+(\\s[A-Z][a-zA-Z'-]+)*\\s[A-Z][a-zA-Z'-]+$";
    @Override
    public void initialize(AuthRequestValidName constraintAnnotation) {
        //nothing to initalize
    }

    /**
     * Validates the format of the passed in 'Name' for our AuthRequestDto
     *
     * @param authRequestDto
     *          - AuthRequestDto to validate 'name' for
     * @param constraintValidatorContext
     *          - provides additional context information for our constraint
     * @return
     *          - validity of the 'name' attribute in the AuthRequestDto
     */
    @Override
    public boolean isValid(AuthRequestDto authRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        String name = authRequestDto.getName();

        if(name == null || name.isEmpty()) {
            return false;
        }

        boolean validName = name.matches(nameRegex);
        log.debug("Validity of the passed in name: [{}]", validName);
        return validName;
    }
}
