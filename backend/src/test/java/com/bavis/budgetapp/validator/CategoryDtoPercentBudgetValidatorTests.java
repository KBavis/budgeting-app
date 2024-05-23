package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.CategoryDto;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class CategoryDtoPercentBudgetValidatorTests {
    @Mock
    ConstraintValidatorContext context;
    @InjectMocks
    CategoryDtoPercentBudgetValidator validator;

    CategoryDto invalidDto;

    CategoryDto validDto;

    @BeforeEach
    void setup() {
        validDto = CategoryDto.builder()
                .budgetAllocationPercentage(.55)
                .build();

        invalidDto = new CategoryDto();
    }

    @Test
    void testIsValid_ValidRequest_Successful() {
        assertTrue(validator.isValid(validDto, context));
    }

    @Test
    void testIsValid_NullCategoryDto_Failure() {
        invalidDto= null;
        assertFalse(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_NegativePercent_Failure() {
        invalidDto.setBudgetAllocationPercentage(-1.0);
        assertFalse(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_HundredPercent_Successful(){
        invalidDto.setBudgetAllocationPercentage(1.0);
        assertTrue(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_ZeroPercent_Failure() {
        invalidDto.setBudgetAllocationPercentage(0);
        assertFalse(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_OverHundredPercent_Failure() {
        invalidDto.setBudgetAllocationPercentage(1.01);
        assertFalse(validator.isValid(invalidDto, context));
    }

}
