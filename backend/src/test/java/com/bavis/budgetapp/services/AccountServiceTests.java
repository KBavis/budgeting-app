package com.bavis.budgetapp.services;


import com.bavis.budgetapp.dao.AccountRepository;
import com.bavis.budgetapp.dto.AccountDTO;
import com.bavis.budgetapp.enumeration.AccountType;
import com.bavis.budgetapp.enumeration.ConnectionStatus;
import com.bavis.budgetapp.exception.AccountConnectionException;
import com.bavis.budgetapp.exception.ConnectionCreationException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.mapper.AccountMapper;
import com.bavis.budgetapp.model.Account;
import com.bavis.budgetapp.model.Connection;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.request.ConnectAccountRequest;
import com.bavis.budgetapp.service.ConnectionService;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.service.impl.AccountServiceImpl;
import com.bavis.budgetapp.service.impl.ConnectionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTests {

    @Mock
    AccountRepository accountRepository;

    @Mock
    PlaidService plaidService;

    @Mock
    UserService userService;

    @Mock
    AccountMapper accountMapper;

    @Mock
    ConnectionService connectionService;

    @InjectMocks
    AccountServiceImpl accountService;

    private ConnectAccountRequest connectAccountRequest;

    @BeforeEach
    public void setup(){
        connectAccountRequest = ConnectAccountRequest.builder()
                .plaidAccountId("plaid-account-id")
                .accountName("account-name")
                .publicToken("public-token")
                .accountType(AccountType.CHECKING)
                .build();
    }

    /**
     * Validate that connect account works successfully
     */
    @Test
    public void testConnectAccount_Success() {
        //Arrange
        AccountDTO accountDTO = AccountDTO.builder()
                .accountName(connectAccountRequest.getAccountName())
                .balance(1000.0)
                .accountType(connectAccountRequest.getAccountType())
                .build();
        User user = User.builder()
                .userId(10L)
                .username("username")
                .password("password")
                .build();
        String accessToken = "access-token";
        Connection connection = Connection.builder()
                .connectionId(1L)
                .connectionStatus(ConnectionStatus.CONNECTED)
                .accessToken(accessToken)
                .lastSyncTime(LocalDateTime.now())
                .build();
        Account account = Account.builder()
                .accountId("account-id")
                .accountName("account-name")
                .accountType(connectAccountRequest.getAccountType())
                .connection(connection)
                .balance(accountDTO.getBalance())
                .build();
        double balance = 1000.0;

        //Mock
        when(plaidService.exchangeToken(connectAccountRequest.getPublicToken())).thenReturn(accessToken);
        when(plaidService.retrieveBalance(connectAccountRequest.getPlaidAccountId(), accessToken)).thenReturn(balance);
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(connectionService.create(any(Connection.class))).thenReturn(connection);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toDTO(any(Account.class))).thenReturn(accountDTO);

        //Act
        AccountDTO createdAccountDto = accountService.connectAccount(connectAccountRequest);

        //Assert
        assertNotNull(createdAccountDto);
        assertEquals(AccountType.CHECKING, accountDTO.getAccountType());
        assertEquals(balance, accountDTO.getBalance());
        assertEquals("account-name", accountDTO.getAccountName());

        //Verify
        verify(plaidService, times(1)).exchangeToken(connectAccountRequest.getPublicToken());
        verify(plaidService, times(1)).retrieveBalance(connectAccountRequest.getPlaidAccountId(), accessToken);
        verify(userService, times(1)).getCurrentAuthUser();
        verify(connectionService, times(1)).create(any(Connection.class));
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(accountMapper, times(1)).toDTO(any(Account.class));
    }

    /**
     * Validates our connect account method correctly handles invalid exchange of token
     */
    @Test
    public void testConnectAccount_PlaidServiceException_ExchangeToken_Failure(){
        //Arrange
        String plaidServiceExceptionMsg = "PlaidServiceException: [Invalid Response Code When Exchanging Public Token Via Plaid Client: [404]]";
        String connectAccountExceptionMsg = "An error occurred when creating an account: [" + plaidServiceExceptionMsg + "]";
       //Mock
       when(plaidService.exchangeToken(connectAccountRequest.getPublicToken())).thenThrow(new PlaidServiceException("Invalid Response Code When Exchanging Public Token Via Plaid Client: [404]"));

       //Act & Assert
        AccountConnectionException runtimeException = assertThrows(AccountConnectionException.class, () -> {
            accountService.connectAccount(connectAccountRequest);
        });
        assertEquals(connectAccountExceptionMsg, runtimeException.getMessage());
    }

    /**
     * Validates our connect account method correctly handles invalid retrieval of balance
     */
    public void testConnectAccount_PlaidServiceException_RetrieveBalance_Failure(){
        //Arrange
        String plaidServiceExceptionMsg = "Invalid Response Code When Retrieving Balance Via PlaidClient: [404]";
        String connectAccountExceptionMsg = "An error occurred when creating an account: [" + plaidServiceExceptionMsg + "]";

        //Mock
        when(plaidService.exchangeToken(connectAccountRequest.getPublicToken())).thenThrow(new PlaidServiceException(plaidServiceExceptionMsg));

        //Act & Assert
        AccountConnectionException runtimeException = assertThrows(AccountConnectionException.class, () -> {
            accountService.connectAccount(connectAccountRequest);
        });
        assertNotNull(runtimeException);
        assertEquals(connectAccountExceptionMsg, runtimeException.getMessage());
    }
}
