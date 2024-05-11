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

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class CategoryDtoBudgetAmountValidatorTests {
    @Mock
    ConstraintValidatorContext context;

    @InjectMocks
    CategoryDtoBudgetAmountValidator validator;

    CategoryDto validCategoryDto;

    CategoryDto invalidCategoryDto;

    @BeforeEach
    void setup() {
        validCategoryDto = CategoryDto.builder()
                .budgetAmount(1000.0)
                .build();

        invalidCategoryDto = new CategoryDto();
    }

    @Test
    void testIsValid_ValidRequest_Successful() {
        assertTrue(validator.isValid(validCategoryDto, context));
    }

    @Test
    void testIsValid_NegativeBudget_Failure() {
       invalidCategoryDto.setBudgetAmount(-100.0);
        assertFalse(validator.isValid(invalidCategoryDto, context));
    }

    @Test
    void testIsValid_ZeroBudget_Failure() {
        invalidCategoryDto.setBudgetAmount(0);
        assertFalse(validator.isValid(invalidCategoryDto, context));
    }

}
