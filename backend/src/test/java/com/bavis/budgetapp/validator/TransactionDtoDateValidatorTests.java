package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.TransactionDto;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class TransactionDtoDateValidatorTests {
    @Mock
    ConstraintValidatorContext context;

    @InjectMocks
    TransactionDtoDateValidator validator;

    TransactionDto validTransactionDto;

    TransactionDto invalidTransactionDto;

    LocalDate localDate;

    @BeforeEach
    void setup() {
        localDate = LocalDate.now();

        validTransactionDto = TransactionDto.builder()
                .date(localDate)
                .build();

        invalidTransactionDto = TransactionDto.builder()
                .date(null)
                .build();

    }


    @Test
    void testIsValid_ValidDate_Successful() {
        assertTrue(validator.isValid(validTransactionDto, context));
    }

    @Test
    void testIsValid_NullDate_Failure() {
        assertFalse(validator.isValid(invalidTransactionDto, context));
    }

}

