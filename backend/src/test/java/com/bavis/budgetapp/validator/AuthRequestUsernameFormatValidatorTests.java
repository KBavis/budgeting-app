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
public class AuthRequestUsernameFormatValidatorTests {

    @Mock
    ConstraintValidatorContext context;

    @InjectMocks
    AuthRequestUsernameFormatValidator validator;

    AuthRequestDto invalidDto;

    AuthRequestDto validDto;

    @BeforeEach
    void setup() {
        validDto = AuthRequestDto.builder()
                .username("valid-username")
                .build();

       invalidDto = new AuthRequestDto();
    }

    @Test
    void testIsValid_ValidRequest_Successful() {
        boolean valid = validator.isValid(validDto, context);

        assertTrue(valid);
    }

    @Test
    void testIsValid_InvalidLength_Minimum_Failure() {
        invalidDto.setUsername("user");

        boolean valid = validator.isValid(invalidDto, context);

        assertFalse(valid);
    }

    @Test
    void testIsValid_InvalidLength_Maximum_Failure() {
        invalidDto.setUsername("user-maximum-length-way-too-many-characters");

        boolean valid = validator.isValid(invalidDto, context);

        assertFalse(valid);
    }

    @Test
    void testIsValid_InvalidCharacters_Failure() {
        invalidDto.setUsername("!1Ab&@0-@#9");

        boolean valid = validator.isValid(invalidDto, context);

        assertFalse(valid);
    }

    @Test
    void testIsValid_UsernameNull_Failure() {
        invalidDto.setUsername(null);

        boolean valid = validator.isValid(invalidDto, context);

        assertFalse(valid);
    }

    @Test
    void testIsValid_UsernameEmpty_Failure() {
        invalidDto.setUsername("");

        boolean valid = validator.isValid(invalidDto, context);

        assertFalse(valid);
    }


}
