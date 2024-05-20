package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.PlaidTransactionDto;
import com.bavis.budgetapp.dto.PlaidTransactionSyncResponseDto;
import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.mapper.TransactionMapper;
import com.bavis.budgetapp.service.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class TransactionServiceTests {

    @Mock
    private PlaidServiceImpl plaidService;

    @Mock
    AccountServiceImpl accountService;

    @Mock
    UserServiceImpl userService;

    @Mock
    private ConnectionServiceImpl connectionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    /**
     * Plaid Service Response variables
     */
    PlaidTransactionDto.CounterpartyDto counterpartyDto;
    PlaidTransactionDto.PersonalFinanceCategoryDto personalFinanceCategoryDto;
    String nextCursor;
    String previousCursor;
    LocalDate date;
    String accountId;
    String accessToken;
    PlaidTransactionDto plaidTransactionDtoOne;

    PlaidTransactionDto plaidTransactionDtoTwo;

    PlaidTransactionDto plaidTransactionDtoThree;

    PlaidTransactionDto plaidTransactionDtoFour;

    PlaidTransactionDto plaidTransactionDtoFive;

    PlaidTransactionDto plaidTransactionDtoSix;

    PlaidTransactionSyncResponseDto syncResponseDto;

    List<PlaidTransactionDto> addedTransactions;
    List<PlaidTransactionDto> modifiedTransactions;
    List<PlaidTransactionDto> removedTransactions;

    @BeforeEach
    void setup() {
        nextCursor = "next-cursor";
        previousCursor = "previous-cursor";
        date = LocalDate.now();
        accountId = "account-id";
        accessToken = "access-token";

         counterpartyDto = PlaidTransactionDto.CounterpartyDto.builder()
                .logo_url("logo_url")
                .name("Chase")
                .build();

        personalFinanceCategoryDto = PlaidTransactionDto.PersonalFinanceCategoryDto.builder()
                .primary("PRIMARY")
                .detailed("DETAILED")
                .confidence_level("HIGH")
                .build();

         plaidTransactionDtoOne = PlaidTransactionDto.builder()
                .transaction_id("12345")
                .counterparties(List.of(counterpartyDto))
                .personal_finance_category(personalFinanceCategoryDto)
                .amount(1000.0)
                .datetime(date)
                .account_id(accountId)
                .build();

        plaidTransactionDtoTwo = PlaidTransactionDto.builder()
                .transaction_id("6789")
                .counterparties(List.of(counterpartyDto))
                .personal_finance_category(personalFinanceCategoryDto)
                .amount(2000.0)
                .datetime(date)
                .account_id(accountId)
                .build();


         plaidTransactionDtoThree = PlaidTransactionDto.builder()
                .transaction_id("6789")
                .counterparties(List.of(counterpartyDto))
                .personal_finance_category(personalFinanceCategoryDto)
                .amount(3000.0)
                .datetime(date)
                .account_id(accountId)
                .build();

        /**
         * Transactions that should be filtered out
         */

        plaidTransactionDtoFour = PlaidTransactionDto.builder()
                 .transaction_id("1956")
                 .counterparties(List.of(counterpartyDto))
                 .personal_finance_category(personalFinanceCategoryDto)
                 .amount(0)
                 .datetime(date)
                 .account_id(accountId)
                 .build();

         plaidTransactionDtoFive = PlaidTransactionDto.builder()
                 .transaction_id("1735")
                 .counterparties(List.of(counterpartyDto))
                 .personal_finance_category(personalFinanceCategoryDto)
                 .amount(-500.0)
                 .datetime(date)
                 .account_id(accountId)
                 .build();

         plaidTransactionDtoSix = PlaidTransactionDto.builder()
                 .transaction_id("17545")
                .counterparties(List.of(counterpartyDto))
                .personal_finance_category(personalFinanceCategoryDto)
                .amount(-500.0)
                .datetime(LocalDate.of(2024,4,19))
                .account_id(accountId)
                .build();

        addedTransactions = List.of(plaidTransactionDtoOne, plaidTransactionDtoFour, plaidTransactionDtoSix); //include transaction with 0 amount and date outside of current month
        removedTransactions = List.of(plaidTransactionDtoTwo, plaidTransactionDtoFive); //include transaction with negative amount
        modifiedTransactions = List.of(plaidTransactionDtoThree);

        syncResponseDto = PlaidTransactionSyncResponseDto.builder()
                .added(addedTransactions)
                .modified(modifiedTransactions)
                .removed(removedTransactions)
                .next_cursor(nextCursor)
                .build();
    }


    @Test
    void testSyncTransactions_Successful() {
        //Arrange
        String accountIdOne = "12345XYZ";
        String accountIdTwo = "6789ABCD";

        Connection accountConnectionOne = Connection.builder()
                .connectionId(5L)
                .accessToken(accessToken)
                .previousCursor(previousCursor)
                .build();

        Connection accountConnectionTwo = Connection.builder()
                .connectionId(10L)
                .accessToken(accessToken)
                .previousCursor(previousCursor)
                .build();

        Account accountOne = Account.builder()
                .accountId(accountIdOne)
                .connection(accountConnectionOne)
                .build();

        Account accountTwo = Account.builder()
                .accountId(accountIdTwo)
                .connection(accountConnectionTwo)
                .build();
        ArrayList<String> accountIds = new ArrayList<>(List.of(accountIdOne, accountIdTwo));
        TransactionSyncRequestDto transactionSyncRequestDto = TransactionSyncRequestDto.builder()
                .accounts(accountIds)
                .build();


        //Mock
        when(accountService.read(accountIdOne)).thenReturn(accountOne);
        when(accountService.read(accountIdTwo)).thenReturn(accountTwo);
        when(plaidService.syncTransactions(accessToken, previousCursor)).thenReturn(syncResponseDto);
        when(transactionMapper.toEntity(any(PlaidTransactionDto.class))).thenAnswer(invocationOnMock -> {
            PlaidTransactionDto dto = invocationOnMock.getArgument(0);
            return Transaction.builder()
                    .transactionId(dto.getTransaction_id())
                    .name(dto.getCounterparties().get(0).getName())
                    .amount(dto.getAmount())
                    .date(dto.getDatetime())
                    .logoUrl(dto.getCounterparties().get(0).getLogo_url())
                    .account(null)  //mapper shouldn't map Account or Category entities
                    .category(null)
                    .build();
        });
        when(transactionRepository.saveAllAndFlush(anyList())).thenAnswer(invocationOnMock ->{
            return invocationOnMock.<List<Transaction>>getArgument(0);
        });
        doNothing().when(transactionRepository).deleteAll(anyList());
        when(connectionService.update(any(Connection.class), any(Long.class))).thenAnswer(invocationOnMock -> {
            Connection connectionToUpdate = invocationOnMock.getArgument(0);
            connectionToUpdate.setPreviousCursor(previousCursor);
            return connectionToUpdate;
        });

        //Act
        List<Transaction> actualTransactions = transactionService.syncTransactions(transactionSyncRequestDto);

       //Assert
        assertNotNull(actualTransactions);
        assertEquals(4, actualTransactions.size()); //2 modified, 2 added

        //Ensure Each Modified/Added account is present
        assertTrue(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoOne.getTransaction_id())));
        assertTrue(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoThree.getTransaction_id())));

        //Ensure negative & 0 amounts, and dates outside the current month are filtered out
        assertFalse(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoFour.getTransaction_id())));
        assertFalse(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoFive.getTransaction_id())));
        assertFalse(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoSix.getTransaction_id())));

       //Verify
        verify(accountService, times(1)).read(accountIdOne);
        verify(accountService, times(1)).read(accountIdTwo);
        verify(connectionService, times(1)).update(accountConnectionOne, accountConnectionOne.getConnectionId());
        verify(connectionService, times(1)).update(accountConnectionTwo, accountConnectionTwo.getConnectionId());
        verify(transactionRepository, times(2)).saveAllAndFlush(anyList());
        verify(transactionRepository, times(2)).deleteAll(anyList());
        verify(transactionMapper, times(12)).toEntity(any(PlaidTransactionDto.class));
    }

    @Test
    void testSyncTransactions_AccountServiceException_Failure() {
        //Arrange
        String accountIdOne = "12345XYZ";
        String accountIdTwo = "6789ABCD";
        ArrayList<String> accountIds = new ArrayList<>(List.of(accountIdOne, accountIdTwo));
        TransactionSyncRequestDto transactionSyncRequestDto = TransactionSyncRequestDto.builder()
                .accounts(accountIds)
                .build();

        //Mock
        when(accountService.read(accountIdOne)).thenThrow(new RuntimeException("Unable to locate Account with ID 12345XYZ"));

        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.syncTransactions(transactionSyncRequestDto);
        });
        assertNotNull(exception);
        //TODO: modify the exception thrown to be our own Exception so we don't see java.lang.RuntimeExeption
        assertEquals("java.lang.RuntimeException: Unable to locate Account with ID 12345XYZ", exception.getMessage());

        //Verify
        verify(accountService, times(1)).read(accountIdOne);
    }
    @Test
    void testSyncTransactions_PlaidServiceException_Failure() {
        //Arrange
        String errorMsg = "The provided access token is invalid";
        String plaidServiceErrorMessage = "PlaidServiceException: [" + errorMsg + "]";

        String accountIdOne = "12345XYZ";
        String accountIdTwo = "6789ABCD";
        Connection accountConnectionOne = Connection.builder()
                .connectionId(5L)
                .accessToken(accessToken)
                .previousCursor(previousCursor)
                .build();
        Account accountOne = Account.builder()
                .accountId(accountIdOne)
                .connection(accountConnectionOne)
                .build();
        ArrayList<String> accountIds = new ArrayList<>(List.of(accountIdOne, accountIdTwo));

        TransactionSyncRequestDto transactionSyncRequestDto = TransactionSyncRequestDto.builder()
                .accounts(accountIds)
                .build();

        //Mock
        when(plaidService.syncTransactions(accountConnectionOne.getAccessToken(), accountConnectionOne.getPreviousCursor())).thenThrow(new PlaidServiceException(errorMsg));
        when(accountService.read(accountIdOne)).thenReturn(accountOne);

        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            transactionService.syncTransactions(transactionSyncRequestDto);
        });
        assertNotNull(exception);
        assertEquals(plaidServiceErrorMessage, exception.getMessage());

        //Verify
        verify(plaidService, times(1)).syncTransactions(accountConnectionOne.getAccessToken(), accountConnectionOne.getPreviousCursor());
        verify(accountService, times(1)).read(accountIdOne);
    }

    @Test
    void testSyncTransactions_ConnectionServiceException_Failure() {
        //Arrange
        String accountIdOne = "12345XYZ";
        String accountIdTwo = "6789ABCD";

        Connection accountConnectionOne = Connection.builder()
                .connectionId(5L)
                .accessToken(accessToken)
                .previousCursor(previousCursor)
                .build();

        Connection accountConnectionTwo = Connection.builder()
                .connectionId(10L)
                .accessToken(accessToken)
                .previousCursor(previousCursor)
                .build();

        Account accountOne = Account.builder()
                .accountId(accountIdOne)
                .connection(accountConnectionOne)
                .build();

        Account accountTwo = Account.builder()
                .accountId(accountIdTwo)
                .connection(accountConnectionTwo)
                .build();
        ArrayList<String> accountIds = new ArrayList<>(List.of(accountIdOne, accountIdTwo));
        TransactionSyncRequestDto transactionSyncRequestDto = TransactionSyncRequestDto.builder()
                .accounts(accountIds)
                .build();
        String errorMessage = "Unable to find Connection with ID 5 to update.";
        RuntimeException exception = new RuntimeException(errorMessage);


        //Mock
        when(accountService.read(accountIdOne)).thenReturn(accountOne);
        when(plaidService.syncTransactions(accessToken, previousCursor)).thenReturn(syncResponseDto);
        when(transactionMapper.toEntity(any(PlaidTransactionDto.class))).thenAnswer(invocationOnMock -> {
            PlaidTransactionDto dto = invocationOnMock.getArgument(0);
            return Transaction.builder()
                    .transactionId(dto.getTransaction_id())
                    .name(dto.getCounterparties().get(0).getName())
                    .logoUrl(dto.getCounterparties().get(0).getLogo_url())
                    .amount(dto.getAmount())
                    .date(dto.getDatetime())
                    .account(null)  //mapper shouldn't map Account or Category entities
                    .category(null)
                    .build();
        });
        when(transactionRepository.saveAllAndFlush(anyList())).thenAnswer(invocationOnMock -> invocationOnMock.<List<Transaction>>getArgument(0));
        doNothing().when(transactionRepository).deleteAll(anyList());
        when(connectionService.update(any(Connection.class), any(Long.class))).thenThrow(new RuntimeException(errorMessage));

        //Act
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            transactionService.syncTransactions(transactionSyncRequestDto);
        });
        assertNotNull(thrownException);
        //TODO: make this our own unique exception to remove java.lang.Runtime
        assertEquals("java.lang.RuntimeException: Unable to find Connection with ID 5 to update.", thrownException.getMessage());

        //Verify
        verify(accountService, times(1)).read(accountIdOne);
        verify(connectionService, times(1)).update(accountConnectionOne, accountConnectionOne.getConnectionId());
        verify(transactionRepository, times(1)).saveAllAndFlush(anyList());
        verify(transactionRepository, times(1)).deleteAll(anyList());
        verify(transactionMapper, times(6)).toEntity(any(PlaidTransactionDto.class));
    }

    @Test
    void testReadAll_Successful() {
        //Arrange
        LocalDate now = LocalDate.now();
        Account account = Account.builder()
                .accountId("account-1")
                .accountName("First Account")
                .build();

        User user = User.builder()
                .username("test-user")
                .userId(10L)
                .accounts(List.of(account))
                .build();

        Transaction transactionOne = Transaction.builder()
                .transactionId("transaction-1")
                .amount(1000.0)
                .date(now)
                .account(account)
                .build();

        Transaction transactionTwo = Transaction.builder()
                .transactionId("transaction-1")
                .amount(1000.0)
                .date(now)
                .account(account)
                .build();

        Transaction transactionThree = Transaction.builder()
                .transactionId("transaction-1")
                .amount(1000.0)
                .date(now)
                .account(account)
                .build();

        List<Transaction> expectedTransactions = List.of(transactionOne, transactionTwo, transactionThree);

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(transactionRepository.findByAccountIdsAndCurrentMonth(any(), any())).thenReturn(expectedTransactions);

        //Act
        List<Transaction> transactions = transactionService.readAll();

        //Assert
        assertNotNull(transactions);
        assertEquals(expectedTransactions, transactions);

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(transactionRepository, times(1)).findByAccountIdsAndCurrentMonth(any(), any());
    }

    @Test
    void testReadAll_NullAccounts_Failure() {
        //Arrange
        User user = User.builder()
                .accounts(null)
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);

        //Act
        List<Transaction> transactions = transactionService.readAll();

        //Assert
        assertTrue(transactions.isEmpty());

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
    }

    @Test
    void testReadById_ValidId_Success() {
        //Arrange
        Transaction expectedTransaction = Transaction.builder()
                .transactionId("ty124")
                .amount(1000.0)
                .name("Transaction")
                .build();

        //Mock
        when(transactionRepository.findById(expectedTransaction.getTransactionId())).thenReturn(Optional.of(expectedTransaction));

        //Act
        Transaction transaction = transactionService.readById(expectedTransaction.getTransactionId());

        //Assert
        assertNotNull(transaction);
        assertEquals(expectedTransaction, transaction);

        //Verify
        verify(transactionRepository, times(1)).findById(transaction.getTransactionId());
    }

    @Test
    void testReadById_InvalidId_Fail() {
        //Arrange
        String invalidTransactionId = "invalid-id";
        String exceptionMessage = "Transaction with the following ID not found: " + invalidTransactionId;

        //Mock
        when(transactionRepository.findById(invalidTransactionId)).thenReturn(Optional.empty());

        //Act & Assert
        RuntimeException exception  = assertThrows(RuntimeException.class, () -> {
            transactionService.readById(invalidTransactionId);
        });
        assertNotNull(exception);
        assertEquals(exceptionMessage, exception.getMessage());
    }
}
