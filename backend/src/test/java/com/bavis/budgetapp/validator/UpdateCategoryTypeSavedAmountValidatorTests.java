package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.UpdateCategoryTypeDto;
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
public class UpdateCategoryTypeSavedAmountValidatorTests {
    @Mock
    ConstraintValidatorContext context;

    @InjectMocks
    UpdateCategoryTypeDtoSavedAmountValidator validator;

    private UpdateCategoryTypeDto validUpdateCategoryTypeDto;

    private UpdateCategoryTypeDto invalidUpdateCategoryTypeDto;

    @BeforeEach
    void setup() {
        invalidUpdateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .savedAmount(-1.0)
                .build();

        validUpdateCategoryTypeDto = UpdateCategoryTypeDto.builder()
                .amountAllocated(0)
                .build();
    }

    @Test
    void testValidate_ValidUpdateCategoryTypeDto_Success() {
        assertTrue(validator.isValid(validUpdateCategoryTypeDto, context));
    }

    @Test
    void testValidate_InvalidUpdateCategoryTypeDto_Failure() {
        assertFalse(validator.isValid(invalidUpdateCategoryTypeDto, context));
    }
}
