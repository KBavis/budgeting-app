package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.CategoryDto;
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

@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
public class CategoryDtoNameValidatorTests {


    @Mock
    ConstraintValidatorContext context;

    @InjectMocks
    CategoryDtoNameValidator validator;

    CategoryDto validDto;

    CategoryDto invalidDto;

    @BeforeEach
    void setup() {
        validDto = CategoryDto.builder()
                .name("My New Category")
                .build();

        invalidDto = new CategoryDto();
    }

    @Test
    void testIsValid_ValidRequest_Successful() {
        assertTrue(validator.isValid(validDto, context));
    }

    @Test
    void testIsValid_NullDto_Failure() {
        invalidDto = null;
        assertFalse(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_NullName_Failure() {
        invalidDto .setName(null);
        assertFalse(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_EmptyName_Failure() {
        invalidDto .setName("");
        assertFalse(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_TooLong_Failure() {
       invalidDto.setName("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
       assertFalse(validator.isValid(invalidDto, context));
    }

}
