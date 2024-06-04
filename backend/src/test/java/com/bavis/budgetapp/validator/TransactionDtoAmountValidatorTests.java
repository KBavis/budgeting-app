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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class TransactionDtoAmountValidatorTests {

    @Mock
    ConstraintValidatorContext context;

    @InjectMocks
    TransactionDtoAmountValidator validator;

    TransactionDto validTransactionDto;

    TransactionDto invalidTransactionDto1;

    TransactionDto invalidTransactionDto2;

    @BeforeEach
    void setup() {
        validTransactionDto = TransactionDto.builder()
                .updatedAmount(1000)
                .build();

        invalidTransactionDto1 = TransactionDto.builder()
                .updatedAmount(0)
                .build();

        invalidTransactionDto2 = TransactionDto.builder()
                .updatedAmount(-1)
                .build();
    }

    @Test
    void testIsValid_ZeroAmount_Failure(){
        //Assert
        assertFalse(validator.isValid(invalidTransactionDto1, context));
    }

    @Test
    void testIsValid_NegativeAmount_Failure() {
        //Assert
        assertFalse(validator.isValid(invalidTransactionDto2, context));
    }

    @Test
    void testIsValid_PositiveAmount_Successful() {
        //Assert
        assertTrue(validator.isValid(validTransactionDto, context));
    }
}
