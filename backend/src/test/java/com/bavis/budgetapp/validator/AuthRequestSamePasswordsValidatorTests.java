package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.AuthRequestDto;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class AuthRequestSamePasswordsValidatorTests {
    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @InjectMocks
    AuthRequestSamePasswordsValidator validator;

    AuthRequestDto validRequestDto;

    AuthRequestDto invalidRequestDto;


    @BeforeEach
    void setup() {
        validRequestDto = AuthRequestDto.builder()
                .passwordOne("TestPassword1")
                .passwordTwo("TestPassword1")
                .build();

        invalidRequestDto = new AuthRequestDto();
    }

    @Test
    void testIsValid_ValidRequest_Successful() {
        boolean valid = validator.isValid(validRequestDto, constraintValidatorContext);

        assertTrue(valid);
    }

    @Test
    void testIsValid_PasswordMismatch_Failure() {
        invalidRequestDto.setPasswordOne("TestPassword1");
        invalidRequestDto.setPasswordTwo("TestPassword2");

        boolean valid = validator.isValid(invalidRequestDto, constraintValidatorContext);

        assertFalse(valid);
    }

    @Test
    void testIsValid_PasswordOneNull_Failure() {
        invalidRequestDto.setPasswordOne(null);
        invalidRequestDto.setPasswordTwo("password");

        boolean valid = validator.isValid(invalidRequestDto, constraintValidatorContext);

        assertFalse(valid);
    }

    @Test
    void testIsValid_PasswordTwoNull_Failure() {
        invalidRequestDto.setPasswordOne("password");
        invalidRequestDto.setPasswordTwo(null);

        boolean valid = validator.isValid(invalidRequestDto, constraintValidatorContext);

        assertFalse(valid);
    }

    @Test
    void testIsValid_PasswordOneEmpty_Failure() {
        invalidRequestDto.setPasswordOne("");
        invalidRequestDto.setPasswordTwo("password");

        boolean valid = validator.isValid(invalidRequestDto, constraintValidatorContext);

        assertFalse(valid);
    }

    @Test
    void testIsValid_PasswordTwoEmpty_Failure() {
        invalidRequestDto.setPasswordOne("password");
        invalidRequestDto.setPasswordTwo("");

        boolean valid = validator.isValid(invalidRequestDto, constraintValidatorContext);

        assertFalse(valid);
    }


}
