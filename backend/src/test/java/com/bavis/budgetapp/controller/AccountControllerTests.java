package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.AccountDTO;
import com.bavis.budgetapp.enumeration.AccountType;
import com.bavis.budgetapp.exception.AccountConnectionException;
import com.bavis.budgetapp.request.ConnectAccountRequest;
import com.bavis.budgetapp.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTests {

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    private ConnectAccountRequest connectAccountRequest;

    @BeforeEach
    public void setup() {
        //Arrange
        String plaidAccountId = "plaid-account-id";
        String publicToken = "public-token";
        String accountName = "account-name";
        double amount = 1000.0;

        connectAccountRequest = ConnectAccountRequest.builder()
                .accountType(AccountType.CHECKING)
                .plaidAccountId(plaidAccountId)
                .publicToken(publicToken)
                .accountName(accountName)
                .build();
    }

    @Test
    public void testConnectAccount_Successful() {
        AccountDTO expectedAccountDTO = AccountDTO.builder()
                .balance(1000.0)
                .accountType(AccountType.CHECKING)
                .accountName(connectAccountRequest.getAccountName())
                .build();

        //Mock
        when(accountService.connectAccount(connectAccountRequest)).thenReturn(expectedAccountDTO);

        //Act
        ResponseEntity<AccountDTO> responseEntity = accountController.connectAccount(connectAccountRequest);

        //Assert
        assertNotNull(responseEntity);
        assertEquals(expectedAccountDTO, responseEntity.getBody());
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        //Verify
        verify(accountService, times(1)).connectAccount(connectAccountRequest);
    }

    @Test
    public void testConnectAccount_Unsuccessful() {
        //Arrange
        String plaidErrorMsg = "Failed to retrieve balance. Status code: 404";
        String errorMsg = "An error occurred when creating an account: [" + plaidErrorMsg + "]";

        //Mock
        when(accountService.connectAccount(connectAccountRequest)).thenThrow(new AccountConnectionException(plaidErrorMsg));

        //Act & Assert
        AccountConnectionException exception = assertThrows(AccountConnectionException.class, () -> {
           accountController.connectAccount(connectAccountRequest);
        });
        assertNotNull(exception);
        assertEquals(errorMsg, exception.getMessage());
    }
}
