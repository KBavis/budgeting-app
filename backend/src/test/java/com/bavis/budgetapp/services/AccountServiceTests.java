package com.bavis.budgetapp.services;


import com.bavis.budgetapp.dao.AccountRepository;
import com.bavis.budgetapp.dto.AccountDto;
import com.bavis.budgetapp.constants.AccountType;
import com.bavis.budgetapp.constants.ConnectionStatus;
import com.bavis.budgetapp.exception.AccountConnectionException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.mapper.AccountMapper;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.dto.ConnectAccountRequestDto;
import com.bavis.budgetapp.service.ConnectionService;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles(profiles = "test")
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

    private ConnectAccountRequestDto connectAccountRequestDto;

    @BeforeEach
    public void setup() {
        connectAccountRequestDto = ConnectAccountRequestDto.builder()
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
        AccountDto accountDTO = AccountDto.builder()
                .accountName(connectAccountRequestDto.getAccountName())
                .balance(1000.0)
                .accountType(connectAccountRequestDto.getAccountType())
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
                .accountType(connectAccountRequestDto.getAccountType())
                .connection(connection)
                .balance(accountDTO.getBalance())
                .build();
        double balance = 1000.0;

        //Mock
        when(plaidService.exchangeToken(connectAccountRequestDto.getPublicToken())).thenReturn(accessToken);
        when(plaidService.retrieveBalance(connectAccountRequestDto.getPlaidAccountId(), accessToken)).thenReturn(balance);
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(connectionService.create(any(Connection.class))).thenReturn(connection);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toDTO(any(Account.class))).thenReturn(accountDTO);

        //Act
        AccountDto createdAccountDto = accountService.connectAccount(connectAccountRequestDto);

        //Assert
        assertNotNull(createdAccountDto);
        assertEquals(AccountType.CHECKING, accountDTO.getAccountType());
        assertEquals(balance, accountDTO.getBalance());
        assertEquals("account-name", accountDTO.getAccountName());

        //Verify
        verify(plaidService, times(1)).exchangeToken(connectAccountRequestDto.getPublicToken());
        verify(plaidService, times(1)).retrieveBalance(connectAccountRequestDto.getPlaidAccountId(), accessToken);
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
       when(plaidService.exchangeToken(connectAccountRequestDto.getPublicToken())).thenThrow(new PlaidServiceException("Invalid Response Code When Exchanging Public Token Via Plaid Client: [404]"));

       //Act & Assert
        AccountConnectionException runtimeException = assertThrows(AccountConnectionException.class, () -> {
            accountService.connectAccount(connectAccountRequestDto);
        });
        assertEquals(connectAccountExceptionMsg, runtimeException.getMessage());
    }

    /**
     * Validates our connect account method correctly handles invalid retrieval of balance
     */
    @Test
    public void testConnectAccount_PlaidServiceException_RetrieveBalance_Failure(){
        //Arrange
        String plaidServiceExceptionMsg = "Invalid Response Code When Retrieving Balance Via PlaidClient: [404]";
        String connectAccountExceptionMsg = "An error occurred when creating an account: [PlaidServiceException: [" + plaidServiceExceptionMsg + "]]";

        //Mock
        when(plaidService.exchangeToken(connectAccountRequestDto.getPublicToken())).thenThrow(new PlaidServiceException(plaidServiceExceptionMsg));

        //Act & Assert
        AccountConnectionException runtimeException = assertThrows(AccountConnectionException.class, () -> {
            accountService.connectAccount(connectAccountRequestDto);
        });
        assertNotNull(runtimeException);
        assertEquals(connectAccountExceptionMsg, runtimeException.getMessage());
    }

    @Test
    void testReadAll_Successful() {
        //Arrange
        Account accountOne = Account.builder()
                .accountId("123XYZ")
                .build();


        Account accountTwo= Account.builder()
                .accountId("123XYZ")
                .build();


        Account accountThree = Account.builder()
                .accountId("123XYZ")
                .build();

        List<Account> expectedAccounts = List.of(accountThree, accountOne, accountTwo);

        User authUser = User.builder()
                .userId(1L)
                .username("auth-user")
                .build();

        //Mock
        when(accountRepository.findByUserUserId(authUser.getUserId())).thenReturn(expectedAccounts);
        when(userService.getCurrentAuthUser()).thenReturn(authUser);
        when(accountMapper.toDTO(any(Account.class))).thenAnswer(invocationOnMock -> {
            Account account = invocationOnMock.getArgument(0);
            return AccountDto.builder()
                    .accountId(account.getAccountId())
                    .accountType(account.getAccountType())
                    .accountName(account.getAccountName())
                    .build();
        });

        //Act
        List<AccountDto> actualAccounts = accountService.readAll();

        //Assert
        assertNotNull(actualAccounts);
        assertEquals(3, actualAccounts.size());
        assertTrue(actualAccounts.stream().anyMatch(accountDto -> accountDto.getAccountId().equals(accountOne.getAccountId())));
        assertTrue(actualAccounts.stream().anyMatch(accountDto -> accountDto.getAccountId().equals(accountTwo.getAccountId())));
        assertTrue(actualAccounts.stream().anyMatch(accountDto -> accountDto.getAccountId().equals(accountThree.getAccountId())));
    }

    @Test
    void testReadAll_NoAccounts_Successful() {
        //Arrrange
        User user = User.builder()
                .userId(1L)
                .username("auth-user")
                .build();
       //Mock
       when(userService.getCurrentAuthUser()).thenReturn(user);
       when(accountRepository.findByUserUserId(user.getUserId())).thenReturn(new ArrayList<>());

       //Act
        List<AccountDto> actualAccounts = accountService.readAll();

        //Assert
        assertTrue(actualAccounts.isEmpty());
    }



}
