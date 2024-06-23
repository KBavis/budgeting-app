package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.AccountsDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.impl.AccountServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class TransactionSyncRequestAccountsValidatorTests {

    @Mock
    private AccountServiceImpl accountService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    ConstraintValidatorContext context;

    @InjectMocks
    private TransactionSyncRequestAccountsValidator validator;

    private AccountsDto invalidDto;
    private AccountsDto validDto;

    private String accountIdOne;
    private String accountIdTwo;

    @BeforeEach
    void setup() {
        accountIdOne = "ABCDE12345";
        accountIdTwo = "WZYZ789";

        validDto = AccountsDto.builder()
                .accounts(List.of(accountIdOne, accountIdTwo))
                .build();

        invalidDto = AccountsDto.builder()
                .accounts(null)
                .build();
    }

    @Test
    void testIsValid_ValidRequest_Successful() {
        //Arrange
        User authUser = User.builder()
                .userId(12345L)
                .build();

        Account accountOne = Account.builder()
                .accountId(accountIdOne)
                .user(authUser)
                .build();

        Account accountTwo = Account.builder()
                .accountId(accountIdTwo)
                .user(authUser)
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(authUser);
        when(accountService.read(accountIdOne)).thenReturn(accountOne);
        when(accountService.read(accountIdTwo)).thenReturn(accountTwo);

        //Act
        boolean valid = validator.isValid(validDto, context);

        //Assert
        assertTrue(valid);
    }

    @Test
    void testIsValid_InvalidRequest_NullAccountIds_Failure() {
        //Act
        boolean valid = validator.isValid(invalidDto, context);

        //Assert
        assertFalse(valid);
    }

    @Test
    void testIsValid_InvalidRequest_AccountNotFound_Failure() {
        //Arrange
        invalidDto.setAccounts(List.of(accountIdOne, accountIdTwo));

        User authUser = User.builder()
                .userId(12345L)
                .build();

        Account accountOne = Account.builder()
                .accountId(accountIdOne)
                .user(authUser)
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(authUser);
        when(accountService.read(accountIdOne)).thenReturn(accountOne);
        when(accountService.read(accountIdTwo)).thenThrow(new RuntimeException("Unable to located Account with ID " + accountOne));

        //Act
        boolean valid = validator.isValid(invalidDto, context);

        //Assert
        assertFalse(valid);
    }

    @Test
    void testIsValid_InvalidRequest_UnauthorizedAccess_Failure() {
        //Arrange
        invalidDto.setAccounts(List.of(accountIdOne));

        User authUser = User.builder()
                .userId(12345L)
                .build();

        User nonAuthUser = User.builder()
                .userId(98765L)
                .build();

        Account accountOne = Account.builder()
                .accountId(accountIdOne)
                .user(nonAuthUser)
                .build();


        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(authUser);
        when(accountService.read(accountIdOne)).thenReturn(accountOne);

        //Act
        boolean valid = validator.isValid(invalidDto, context);

        //Assert
        assertFalse(valid);
    }

}
