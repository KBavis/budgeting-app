package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.IncomeDto;
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
public class IncomeDtoAmountValidatorTests {
    @Mock
    ConstraintValidatorContext context;

    @InjectMocks
    IncomeDtoAmountValidator validator;

    IncomeDto validDto;

    IncomeDto invalidDto;

    @BeforeEach
    void setup() {
        validDto = IncomeDto.builder()
                .amount(1000.0)
                .build();

        invalidDto = new IncomeDto();
    }

    @Test
    void testIsValid_ValidAmount_Successful() {
        assertTrue(validator.isValid(validDto, context));
    }

    @Test
    void testIsValid_NegativeAmount_Failure() {
        invalidDto.setAmount(-100.0);
        assertFalse(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_NullIncomeDto_Failure() {
        invalidDto = null;
        assertFalse(validator.isValid(invalidDto, context));
    }

    @Test
    void testIsValid_ZeroIncomeAmount_Failure() {
        invalidDto.setAmount(0);
        assertFalse(validator.isValid(invalidDto, context));
    }
}
