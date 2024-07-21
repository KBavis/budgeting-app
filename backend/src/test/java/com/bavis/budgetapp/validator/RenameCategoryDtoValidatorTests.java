package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.RenameCategoryDto;
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
public class RenameCategoryDtoValidatorTests {
    @Mock
    ConstraintValidatorContext context;

    @InjectMocks
    RenameCategoryDtoValidator validator;

    private RenameCategoryDto validDto;

    private RenameCategoryDto invalidDto;

    @BeforeEach
    void setup() {
        validDto = RenameCategoryDto.builder()
                .categoryId(10L)
                .categoryName("Valid Name")
                .build();

        invalidDto = RenameCategoryDto.builder()
                .categoryId(11L)
                .build();
    }

    @Test
    void testIsValid_ValidDto_Success() {
        assertTrue(validator.isValid(validDto, context));
    }

    @Test
    void testIsValid_NameTooLong_Fail() {
        invalidDto.setCategoryName("iiiiiiiiiiiiiiiiiiiiinnnnnnnnnnnnnnnnnvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvkkkkkkkkkkkkkkkkkkkkkkllll");
        assertFalse(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_NumbersInName_Fail() {
        invalidDto.setCategoryName("Name1");
        assertFalse(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_SymbolsInName_Fail() {
        invalidDto.setCategoryName("Hey$");
        assertFalse(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_NullDto_Fail() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void testIsValid_NullName_Fail() {
        invalidDto.setCategoryName(null);
        assertFalse(validator.isValid(invalidDto, context));
    }

}
