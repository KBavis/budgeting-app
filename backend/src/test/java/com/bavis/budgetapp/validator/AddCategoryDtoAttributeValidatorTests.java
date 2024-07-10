package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.AddCategoryDto;
import com.bavis.budgetapp.dto.CategoryDto;
import com.bavis.budgetapp.dto.UpdateCategoryDto;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class AddCategoryDtoAttributeValidatorTests {
    @Mock
    ConstraintValidatorContext context;

    private AddCategoryDto validAddCategoryDto;
    private AddCategoryDto invalidAddCategoryDto;
    private UpdateCategoryDto updateCategoryDto;

    private CategoryDto addedCategoryDto;

    private AddCategoryDtoAttributeValidator validator;

    @BeforeEach
    void setup() {
        addedCategoryDto = CategoryDto.builder()
                .categoryTypeId(10L)
                .name("Added Category")
                .build();

        updateCategoryDto = UpdateCategoryDto.builder()
                .categoryId(10L)
                .budgetAllocationPercentage(.5)
                .build();

        invalidAddCategoryDto = AddCategoryDto.builder()
                .addedCategory(null)
                .updatedCategories(null)
                .build();

        validAddCategoryDto = AddCategoryDto.builder()
                .addedCategory(addedCategoryDto)
                .updatedCategories(List.of(updateCategoryDto))
                .build();

        validator = new AddCategoryDtoAttributeValidator();

    }

    @Test
    void testIsValid_NullUpdatedCategories_Failure() {
        invalidAddCategoryDto.setAddedCategory(addedCategoryDto);
        assertFalse(validator.isValid(invalidAddCategoryDto, context));
    }

    @Test
    void testIsValid_NullCategoryDto_Failure() {
        invalidAddCategoryDto.setUpdatedCategories(List.of(updateCategoryDto));
        assertFalse(validator.isValid(invalidAddCategoryDto, context));
    }

    @Test
    void testIsValid_ValidAddCategoryDto_Success() {
        assertTrue(validator.isValid(validAddCategoryDto, context));
    }
}
