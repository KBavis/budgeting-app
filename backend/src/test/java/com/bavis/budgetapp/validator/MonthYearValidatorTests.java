package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.model.MonthYear;
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
public class MonthYearValidatorTests {

    @Mock
    private ConstraintValidatorContext context;

    @InjectMocks
    private MonthYearValidator validator;

    private MonthYear validMonthYear;

    private MonthYear invalidMonthYear;

    @BeforeEach
    void setup() {
        validMonthYear = MonthYear.builder()
                .month("MARCH")
                .year(2002)
                .build();

        invalidMonthYear = null;
    }


    @Test
    void testNullYear_Invalid() {
        assertFalse(validator.isValid(invalidMonthYear, context));
    }

    @Test
    void testValidMonth_ValidYear_Valid() {
        assertTrue(validator.isValid(validMonthYear, context));
    }

    @Test
    void testNullMonth_Invalid() {
        invalidMonthYear = MonthYear.builder()
                        .month(null)
                        .year(2002)
                        .build();
        assertFalse(validator.isValid(invalidMonthYear, context));
    }

    @Test
    void testInvalidYear_BelowMin_Invalid() {
        invalidMonthYear = MonthYear.builder()
                .year(0)
                .month("MARCH")
                .build();
        assertFalse(validator.isValid(invalidMonthYear, context));
    }

    @Test
    void testInvalidYear_AboveMax_Invalid() {
        invalidMonthYear = MonthYear.builder()
                .year(10000)
                .month("MARCH")
                .build();
        assertFalse(validator.isValid(invalidMonthYear, context));
    }

    @Test
    void testInvalidMonth_NoCaps_Invalid() {
        invalidMonthYear = MonthYear.builder()
                .year(2001)
                .month("march")
                .build();
        assertFalse(validator.isValid(invalidMonthYear, context));
    }

    @Test
    void testInvalidMonth_NotValidMonth_Invalid() {
        invalidMonthYear = MonthYear.builder()
                .year(2001)
                .month("marchs")
                .build();
        assertFalse(validator.isValid(invalidMonthYear, context));
    }

}
