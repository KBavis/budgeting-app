package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.AuthRequestDto;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
public class AuthRequestNameValidatorTests {


    @InjectMocks
    AuthRequestNameValidator validator;

    AuthRequestDto validAuthRequestDto;
    AuthRequestDto emptyNameAuthRequestDto;

    AuthRequestDto nullNameAuthRequestDto;
    AuthRequestDto invalidNameAuthRequestDto;


    @BeforeEach
    void setup() {
        validAuthRequestDto = AuthRequestDto.builder()
                .name("Test User")
                .build();

        emptyNameAuthRequestDto = AuthRequestDto.builder()
                .name("")
                .build();

        nullNameAuthRequestDto = AuthRequestDto.builder()
                .name(null)
                .build();

        invalidNameAuthRequestDto = AuthRequestDto.builder()
                .name("124Test")
                .build();

    }

    @Test
    public void testIsValid_ValidName_Successful() {
        //Mock
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(validAuthRequestDto, context);

        //Assert
        assertTrue(valid);
    }

    @Test
    public void testIsValid_NullName_Failure() {
        //Mock
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(nullNameAuthRequestDto, context);

        //Assert
        assertFalse(valid);
    }

    @Test
    public void testIsValid_EmptyName_Failure() {
        //Mock
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(emptyNameAuthRequestDto, context);

        //Assert
        assertFalse(valid);
    }

    @Test
    public void testIsValid_InvalidName_Failure() {
        //Mock
        ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

        //Act
        boolean valid = validator.isValid(invalidNameAuthRequestDto, context);

        //Assert
        assertFalse(valid);
    }
}
