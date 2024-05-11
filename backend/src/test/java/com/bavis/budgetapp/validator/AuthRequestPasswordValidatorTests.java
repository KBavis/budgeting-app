package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.AuthRequestDto;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.tomcat.util.bcel.Const;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class AuthRequestPasswordValidatorTests {

    @InjectMocks
    AuthRequestPasswordValidator validator;

    AuthRequestDto validDto;

    AuthRequestDto invalidDto;

    @BeforeEach
    void setup() {
        validDto = AuthRequestDto.builder()
                .passwordOne("TestPassword123!")
                .build();

        invalidDto = AuthRequestDto.builder()
                .passwordOne("pass")
                .build();

    }

    @Test
    void testIsValid_ValidPassword_Successful() {
        //Mock
        ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(validDto, constraintValidatorContext);

        //Assert
        assertTrue(valid);
    }

    @Test
    void testIsValid_InvalidPassword_Length_Failure() {
        //Mock
        ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(invalidDto, constraintValidatorContext);

        //Assert
        assertFalse(valid);
    }

    @Test
    void testIsValid_InvalidPassword_NoSpecialCharacter_Failure() {
        //Arrange
        invalidDto.setPasswordOne("TestPassword1");
        //Mock
        ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(invalidDto, constraintValidatorContext);

        //Assert
        assertFalse(valid);
    }

    @Test
    void testIsValid_EmptyPassword_Failure() {
        //Arrange
        invalidDto.setPasswordOne("");
        //Mock
        ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(invalidDto, constraintValidatorContext);

        //Assert
        assertFalse(valid);
    }

    @Test
    void testIsValid_NullPassword_Failure() {
        //Arrange
        invalidDto.setPasswordOne(null);
        //Mock
        ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(invalidDto, constraintValidatorContext);

        //Assert
        assertFalse(valid);
    }

}
