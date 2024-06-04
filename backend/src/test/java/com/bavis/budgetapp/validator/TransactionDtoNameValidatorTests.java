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
public class TransactionDtoNameValidatorTests {
    @Mock
    ConstraintValidatorContext context;

    @InjectMocks
    TransactionDtoNameValidator validator;

    TransactionDto validTransactionDto;

    TransactionDto invalidTransactionDto1;

    TransactionDto invalidTransactionDto2;

    TransactionDto invalidTransactionDto3;

    @BeforeEach
    void setup() {
        validTransactionDto = TransactionDto.builder()
                .updatedName("Valid Transaction Name")
                .build();

        invalidTransactionDto1 = TransactionDto.builder()
                .updatedName("")
                .build();

        invalidTransactionDto2 = TransactionDto.builder()
                .updatedName("123456")
                .build();

        invalidTransactionDto3 = TransactionDto.builder()
                .updatedName("abcdeqeeprpweprpweprpewrpwerpweprpwerpweprweprpwerppwerpweprwpepwer")
                .build();
    }


    @Test
    void testIsValid_ValidName_Successful() {
        assertTrue(validator.isValid(validTransactionDto, context));
    }

    @Test
    void testIsValid_EmptyName_Failure() {
        assertFalse(validator.isValid(invalidTransactionDto1, context));
    }
    @Test
    void testIsValid_NumbersInName_Failure() {
        assertFalse(validator.isValid(invalidTransactionDto2, context));
    }
    @Test
    void testIsValid_TooLongName_Failure() {
        assertFalse(validator.isValid(invalidTransactionDto3, context));
    }

}
