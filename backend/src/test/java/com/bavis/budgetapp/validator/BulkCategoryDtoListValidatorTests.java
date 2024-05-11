package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.BulkCategoryDto;
import com.bavis.budgetapp.dto.CategoryDto;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
public class BulkCategoryDtoListValidatorTests {

    @Mock
    ConstraintValidatorContext context;

    @InjectMocks
    BulkCategoryDtoListValidator validator;

    BulkCategoryDto validDto;

    BulkCategoryDto invalidDto;

    @BeforeEach
    void setup() {
        CategoryDto categoryDto = CategoryDto.builder()
                .name("New Category")
                .budgetAmount(1000.0)
                .build();

       validDto = BulkCategoryDto.builder()
               .categories(List.of(categoryDto))
               .build();

       invalidDto = new BulkCategoryDto();
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
    void testIsValid_NullCategories_Failure() {
        invalidDto.setCategories(null);

        assertFalse(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_MissingCategory_Failure() {
        invalidDto.setCategories(new ArrayList<>());

        assertFalse(validator.isValid(invalidDto, context));
    }
}
