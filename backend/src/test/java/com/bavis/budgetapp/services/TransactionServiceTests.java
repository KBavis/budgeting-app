package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.*;
import com.bavis.budgetapp.entity.*;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.mapper.TransactionMapper;
import com.bavis.budgetapp.service.impl.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
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

    @Mock
    private CategoryServiceImpl categoryService;

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
                .datetime(LocalDate.of(2024, 4, 19))
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
                .has_more(false)
                .build();
    }

    @Test
    void testFetchCategoryTransactions_NotNull_Success() {
        //Arrange
        Transaction transaction = Transaction.builder()
                        .transactionId("tx-id")
                        .build();

        //Mock
        when(transactionRepository.findByCategoryCategoryId(1L)).thenReturn(List.of(transaction));

        //Act
        List<Transaction> actualTransactions = transactionService.fetchCategoryTransactions(1L);

        //Assert
        assertNotNull(actualTransactions);
        assertTrue(actualTransactions.contains(transaction));
        assertEquals(1, actualTransactions.size());
    }

    @Test
    void testFetchCategoryTransactions_Null_Success() {
        //Mock
        when(transactionRepository.findByCategoryCategoryId(1L)).thenReturn(Collections.emptyList());

        //Act
        List<Transaction> actualTransactions = transactionService.fetchCategoryTransactions(1L);

        //Assert
        assertEquals(Collections.emptyList(), actualTransactions);
    }

    @Test
    void testFetchCategoryTransactions_DataAccessException_Fail() {
        //Mock
        when(transactionRepository.findByCategoryCategoryId(1L)).thenThrow(new DataRetrievalFailureException("Failed to retrieve"));

        //Act & Assert
        DataRetrievalFailureException exception = assertThrows(DataRetrievalFailureException.class, () -> {
            transactionService.fetchCategoryTransactions(1L);
        });
        assertEquals("Failed to retrieve", exception.getMessage());
    }


    @Test
    void testSyncTransactions_SinglePage_Successful() {
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
        AccountsDto accountsDto = AccountsDto.builder()
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
        when(transactionRepository.saveAllAndFlush(anyList())).thenAnswer(invocationOnMock -> {
            return invocationOnMock.<List<Transaction>>getArgument(0);
        });
        when(transactionRepository.existsById(any())).thenReturn(true);
        when(transactionRepository.existsByTransactionIdAndUpdatedByUserIsTrue(any())).thenReturn(false);
        doNothing().when(transactionRepository).deleteAllById(anyList());
        when(connectionService.update(any(Connection.class), any(Long.class))).thenAnswer(invocationOnMock -> {
            Connection connectionToUpdate = invocationOnMock.getArgument(0);
            connectionToUpdate.setPreviousCursor(previousCursor);
            return connectionToUpdate;
        });

        //Act
        SyncTransactionsDto syncTransactionsDto = transactionService.syncTransactions(accountsDto);
        List<Transaction> actualTransactions = syncTransactionsDto.getAllModifiedOrAddedTransactions();
        List<String> removedTransactionIds = syncTransactionsDto.getRemovedTransactionIds();

        //Assert
        assertNotNull(syncTransactionsDto);
        assertEquals(4, actualTransactions.size()); //2 modified, 2 added
        assertEquals(4, removedTransactionIds.size()); //4 removed

        //Ensure Each Modified/Added account is present
        assertTrue(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoOne.getTransaction_id())));
        assertTrue(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoThree.getTransaction_id())));

        //Ensure negative & 0 amounts, and dates outside the current month are filtered out
        assertFalse(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoFour.getTransaction_id())));
        assertFalse(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoFive.getTransaction_id())));
        assertFalse(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoSix.getTransaction_id())));

        //Ensure Each Removed Transaction Id is present
        assertTrue(removedTransactionIds.stream().anyMatch(transactionId -> transactionId.equals(plaidTransactionDtoTwo.getTransaction_id())));
        assertTrue(removedTransactionIds.stream().anyMatch(transactionId -> transactionId.equals(plaidTransactionDtoFive.getTransaction_id())));

        //Verify
        verify(accountService, times(1)).read(accountIdOne);
        verify(accountService, times(1)).read(accountIdTwo);
        verify(connectionService, times(1)).update(accountConnectionOne, accountConnectionOne.getConnectionId());
        verify(connectionService, times(1)).update(accountConnectionTwo, accountConnectionTwo.getConnectionId());
        verify(transactionRepository, times(1)).saveAllAndFlush(anyList());
        verify(transactionRepository, times(1)).deleteAllById(anyList());
        verify(transactionMapper, times(8)).toEntity(any(PlaidTransactionDto.class));
        verify(transactionRepository, times(2)).existsByTransactionIdAndUpdatedByUserIsTrue(any());
        verify(transactionRepository, times(2)).existsById(any());
        verify(plaidService, times(2)).syncTransactions(accessToken, previousCursor);
    }

    @Test
    void testSyncTransactions_MultiplePages_Successful() {
        //Arrange
        PlaidTransactionSyncResponseDto syncResponseDto2 = PlaidTransactionSyncResponseDto.builder() //additional sync response to indicate multiple pages
                .added(addedTransactions)
                .modified(modifiedTransactions)
                .removed(removedTransactions)
                .next_cursor(nextCursor)
                .has_more(true)
                .build();

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
        AccountsDto accountsDto = AccountsDto.builder()
                .accounts(accountIds)
                .build();

        //Mock
        when(accountService.read(accountIdOne)).thenReturn(accountOne);
        when(accountService.read(accountIdTwo)).thenReturn(accountTwo);
        when(plaidService.syncTransactions(accessToken, previousCursor)).thenReturn(syncResponseDto2);
        when(plaidService.syncTransactions(accessToken, nextCursor)).thenReturn(syncResponseDto);
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
        when(transactionRepository.saveAllAndFlush(anyList())).thenAnswer(invocationOnMock -> {
            return invocationOnMock.<List<Transaction>>getArgument(0);
        });
        when(transactionRepository.existsById(any())).thenReturn(true);
        when(transactionRepository.existsByTransactionIdAndUpdatedByUserIsTrue(any())).thenReturn(false);
        doNothing().when(transactionRepository).deleteAllById(anyList());
        when(connectionService.update(any(Connection.class), any(Long.class))).thenAnswer(invocationOnMock -> {
            Connection connectionToUpdate = invocationOnMock.getArgument(0);
            connectionToUpdate.setPreviousCursor(nextCursor);
            connectionToUpdate.setOriginalCursor(previousCursor);
            return connectionToUpdate;
        });

        //Act
        SyncTransactionsDto syncTransactionsDto = transactionService.syncTransactions(accountsDto);
        List<Transaction> actualTransactions = syncTransactionsDto.getAllModifiedOrAddedTransactions();
        List<String> removedTransactionIds = syncTransactionsDto.getRemovedTransactionIds();

        //Assert
        assertNotNull(actualTransactions);
        assertEquals(8, actualTransactions.size());
        assertNotNull(removedTransactionIds);
        assertEquals(8, removedTransactionIds.size());

        //Ensure Each Modified/Added account is present
        assertTrue(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoOne.getTransaction_id())));
        assertTrue(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoThree.getTransaction_id())));

        //Ensure negative & 0 amounts, and dates outside the current month are filtered out
        assertFalse(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoFour.getTransaction_id())));
        assertFalse(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoFive.getTransaction_id())));
        assertFalse(actualTransactions.stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoSix.getTransaction_id())));

        //Ensure Each Removed Transaction Id is present
        assertTrue(removedTransactionIds.stream().anyMatch(transactionId -> transactionId.equals(plaidTransactionDtoTwo.getTransaction_id())));
        assertTrue(removedTransactionIds.stream().anyMatch(transactionId -> transactionId.equals(plaidTransactionDtoFive.getTransaction_id())));

        //Verify
        verify(accountService, times(1)).read(accountIdOne);
        verify(accountService, times(1)).read(accountIdTwo);
        verify(connectionService, times(1)).update(accountConnectionOne, accountConnectionOne.getConnectionId());
        verify(connectionService, times(1)).update(accountConnectionTwo, accountConnectionTwo.getConnectionId());
        verify(transactionRepository, times(1)).saveAllAndFlush(anyList());
        verify(transactionRepository, times(1)).deleteAllById(anyList());
        verify(transactionMapper, times(16)).toEntity(any(PlaidTransactionDto.class));
        verify(transactionRepository, times(4)).existsByTransactionIdAndUpdatedByUserIsTrue(any());
        verify(transactionRepository, times(4)).existsById(any());
        verify(plaidService, times(2)).syncTransactions(accessToken, previousCursor);
        verify(plaidService, times(2)).syncTransactions(accessToken, nextCursor);
    }

    @Test
    void testSyncTransactions_AccountServiceException_Failure() {
        //Arrange
        String accountIdOne = "12345XYZ";
        String accountIdTwo = "6789ABCD";
        ArrayList<String> accountIds = new ArrayList<>(List.of(accountIdOne, accountIdTwo));
        AccountsDto accountsDto = AccountsDto.builder()
                .accounts(accountIds)
                .build();

        //Mock
        when(accountService.read(accountIdOne)).thenThrow(new RuntimeException("Unable to locate Account with ID 12345XYZ"));

        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.syncTransactions(accountsDto);
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

        AccountsDto accountsDto = AccountsDto.builder()
                .accounts(accountIds)
                .build();

        //Mock
        when(plaidService.syncTransactions(accountConnectionOne.getAccessToken(), accountConnectionOne.getPreviousCursor())).thenThrow(new PlaidServiceException(errorMsg));
        when(accountService.read(accountIdOne)).thenReturn(accountOne);

        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            transactionService.syncTransactions(accountsDto);
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

        Connection accountConnectionOne = Connection.builder()
                .connectionId(5L)
                .accessToken(accessToken)
                .previousCursor(previousCursor)
                .build();


        Account accountOne = Account.builder()
                .accountId(accountIdOne)
                .connection(accountConnectionOne)
                .build();

        ArrayList<String> accountIds = new ArrayList<>(List.of(accountIdOne));
        AccountsDto accountsDto = AccountsDto.builder()
                .accounts(accountIds)
                .build();
        String errorMessage = "Unable to find Connection with ID 5 to update.";
        RuntimeException runtimeException = new RuntimeException(errorMessage);


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
        when(connectionService.update(accountConnectionOne, accountConnectionOne.getConnectionId())).thenThrow(runtimeException);

        //Act
        RuntimeException thrownException = assertThrows(RuntimeException.class, () -> {
            transactionService.syncTransactions(accountsDto);
        });
        assertNotNull(thrownException);
        //TODO: make this our own unique exception to remove java.lang.Runtime
        assertEquals("java.lang.RuntimeException: Unable to find Connection with ID 5 to update.", thrownException.getMessage());

        //Verify
        verify(accountService, times(1)).read(accountIdOne);
        verify(connectionService, times(1)).update(accountConnectionOne, accountConnectionOne.getConnectionId());
        verify(transactionMapper, times(4)).toEntity(any(PlaidTransactionDto.class));
    }


    @Test
    void testReadAll_Successful() {
        //Arrange
        LocalDate now = LocalDate.now();
        Account account = Account.builder()
                .accountId("account-1")
                .accountName("First Account")
                .build();

        Category category = Category.builder()
                .categoryId(10L)
                .build();

        User user = User.builder()
                .username("test-user")
                .userId(10L)
                .accounts(List.of(account))
                .categories(List.of(category))
                .build();

        Transaction transactionOne = Transaction.builder()
                .transactionId("transaction-3")
                .amount(1000.0)
                .date(now)
                .account(account)
                .build();

        Transaction transactionTwo = Transaction.builder()
                .transactionId("transaction-4")
                .amount(1000.0)
                .date(now)
                .account(account)
                .build();

        Transaction transactionThree = Transaction.builder()
                .transactionId("transaction-5")
                .amount(1000.0)
                .date(now)
                .account(account)
                .build();

        Transaction transactionFour = Transaction.builder()
                .transactionId("transaction-6")
                .category(category)
                .account(null)
                .build();


        List<Transaction> expectedAccountTransactions = List.of(transactionOne, transactionTwo, transactionThree);
        List<Transaction> expectedUserCreatedTransactions = List.of(transactionFour);

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(transactionRepository.findByAccountIdsAndCurrentMonth(any(), any())).thenReturn(expectedAccountTransactions);
        when(transactionRepository.findByCategoryIdsAndCurrentMonth(any(), any())).thenReturn(expectedUserCreatedTransactions);

        //Act
        List<Transaction> transactions = transactionService.readAll();

        //Assert
        assertNotNull(transactions);
        assertEquals(expectedAccountTransactions.size() + expectedUserCreatedTransactions.size(), transactions.size());
        assertTrue(transactions.containsAll(expectedAccountTransactions));
        assertTrue(transactions.containsAll(expectedUserCreatedTransactions));
        assertEquals(transactionOne, transactions.get(0));
        assertEquals(transactionTwo, transactions.get(1));
        assertEquals(transactionThree, transactions.get(2));
        assertEquals(transactionFour, transactions.get(3));


        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(transactionRepository, times(1)).findByAccountIdsAndCurrentMonth(any(), any());
        verify(transactionRepository, times(1)).findByCategoryIdsAndCurrentMonth(any(), any());
    }

    @Test
    void testReadAll_NullAccounts_NullCategories_Failure() {
        //Arrange
        User user = User.builder()
                .accounts(null)
                .categories(null)
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
    void testReadAll_NullAccounts_ValidCategories_Success() {
        //Arrange
        Category category = Category.builder()
                .categoryId(10L)
                .build();

        Transaction transaction = Transaction.builder()
                .category(category)
                .transactionId("transaction-id")
                .account(null)
                .amount(1000.0)
                .date(LocalDate.now())
                .build();

        List<Transaction> expectedUserCreatedTransactions = List.of(transaction);

        User user = User.builder()
                .accounts(null)
                .categories(List.of(category))
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(transactionRepository.findByCategoryIdsAndCurrentMonth(any(), any())).thenReturn(expectedUserCreatedTransactions);

        //Act
        List<Transaction> actualTransactions = transactionService.readAll();

        //Assert
        assertNotNull(actualTransactions);
        assertTrue(actualTransactions.containsAll(expectedUserCreatedTransactions));
        assertEquals(expectedUserCreatedTransactions.size(), actualTransactions.size());
        assertEquals(transaction, actualTransactions.get(0));

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(transactionRepository, times(1)).findByCategoryIdsAndCurrentMonth(any(), any());
    }

    @Test
    void testReadAll_NullCategories_ValidAccounts_Success() {
        //Arrange
        Account account = Account.builder()
                .accountId("account-id")
                .build();

        Transaction transaction = Transaction.builder()
                .category(null)
                .transactionId("transaction-id")
                .account(account)
                .amount(1000.0)
                .date(LocalDate.now())
                .build();

        List<Transaction> expectedUserAccountTransactions = List.of(transaction);

        User user = User.builder()
                .accounts(List.of(account))
                .categories(null)
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(transactionRepository.findByAccountIdsAndCurrentMonth(any(), any())).thenReturn(expectedUserAccountTransactions);

        //Act
        List<Transaction> actualTransactions = transactionService.readAll();

        //Assert
        assertNotNull(actualTransactions);
        assertTrue(actualTransactions.containsAll(expectedUserAccountTransactions));
        assertEquals(expectedUserAccountTransactions.size(), actualTransactions.size());
        assertEquals(transaction, actualTransactions.get(0));

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(transactionRepository, times(1)).findByAccountIdsAndCurrentMonth(any(), any());
    }

    @Test
    void testSplitTransactions_ValidTransactionId_Success() {
        //Arrange
        String transactionId = "transaction-id";
        String transactionName = "transaction-name";
        String transactionName2 = "transaction-name-2";
        String logoUrl = "logo-url";
        double amount = 100.0;
        double amount2 = 200.0;
        LocalDate localDate = LocalDate.now();
        Category category = Category.builder()
                .categoryId(10L)
                .build();
        Account account = Account.builder()
                .accountId("account-id")
                .build();
        Transaction originalTransaction = Transaction.builder()
                .transactionId(transactionId)
                .category(category)
                .account(account)
                .date(localDate)
                .logoUrl(logoUrl)
                .build();

        TransactionDto transactionDto1 = TransactionDto.builder()
                .updatedName(transactionName)
                .updatedAmount(amount)
                .build();
        TransactionDto transactionDto2 = TransactionDto.builder()
                .updatedAmount(amount2)
                .updatedName(transactionName2)
                .build();

        SplitTransactionDto splitTransactionDto = SplitTransactionDto.builder()
                .splitTransactions(List.of(transactionDto1, transactionDto2))
                .build();

        //Mock
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(originalTransaction));
        when(transactionMapper.toEntity(any(TransactionDto.class))).thenAnswer(invocationOnMock -> {
            TransactionDto transactionDto = invocationOnMock.getArgument(0);
            Transaction transaction = new Transaction();
            transaction.setName(transactionDto.getUpdatedName());
            transaction.setAmount(transactionDto.getUpdatedAmount());
            transaction.setLogoUrl(transactionDto.getLogoUrl());
            transaction.setDate(transactionDto.getDate());
            transaction.setAccount(account);
            transaction.setCategory(category);
            return transaction;
        });
        doNothing().when(transactionRepository).deleteById(transactionId);
        when(transactionRepository.saveAllAndFlush(anyList())).thenReturn(null);

        //Act
        List<Transaction> transactions = transactionService.splitTransaction(transactionId, splitTransactionDto);

        //Assert
        assertNotNull(transactions);
        assertEquals(2, transactions.size());

        Transaction transaction1 = transactions.get(0);
        Transaction transaction2 = transactions.get(1);
        assertEquals(transactionId + "_" + 1, transaction1.getTransactionId());
        assertEquals(account.getAccountId(), transaction1.getAccount().getAccountId());
        assertEquals(category.getCategoryId(), transaction1.getCategory().getCategoryId());
        assertEquals(localDate, transaction1.getDate());
        assertEquals(logoUrl, transaction1.getLogoUrl());
        assertEquals(amount, transaction1.getAmount());
        assertEquals(transactionName, transaction1.getName());


        assertEquals(transactionId + "_" + 2, transaction2.getTransactionId());
        assertEquals(category.getCategoryId(), transaction2.getCategory().getCategoryId());
        assertEquals(account.getAccountId(), transaction1.getAccount().getAccountId());
        assertEquals(localDate, transaction2.getDate());
        assertEquals(logoUrl, transaction2.getLogoUrl());
        assertEquals(amount2, transaction2.getAmount());
        assertEquals(transactionName2, transaction2.getName());
    }

    @Test
    void testSplitTransactions_InvalidTransactionId_Failure() {
        //Arrange
        String badTransactionId = "badTransactionId";

        //Mock
        when(transactionRepository.findById(badTransactionId)).thenReturn(Optional.empty());

        //Act & Assert
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            transactionService.splitTransaction(badTransactionId, null);
        });
        assertNotNull(runtimeException);
        assertEquals(runtimeException.getMessage(), "Transaction with the following ID not found: " + badTransactionId);

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
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.readById(invalidTransactionId);
        });
        assertNotNull(exception);
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void testAssignCategory_Successful() {
        //Arrange
        Category category = Category.builder()
                .categoryId(10L)
                .build();

        Transaction transaction = Transaction.builder()
                .transactionId("transaction-id")
                .build();

        AssignCategoryRequestDto categoryRequestDto = AssignCategoryRequestDto.builder()
                .categoryId(String.valueOf(category.getCategoryId()))
                .transactionId(transaction.getTransactionId())
                .build();

        Transaction updatedTransaction = Transaction.builder()
                .category(category)
                .transactionId("transaction-id")
                .build();

        //Mock
        when(categoryService.read(Long.parseLong(categoryRequestDto.getCategoryId()))).thenReturn(category);
        when(transactionRepository.findById(categoryRequestDto.getTransactionId())).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(updatedTransaction);

        //Act
        Transaction actualTransaction = transactionService.assignCategory(categoryRequestDto);

        //Assert
        assertNotNull(actualTransaction);
        assertEquals(updatedTransaction.getTransactionId(), actualTransaction.getTransactionId());
        assertEquals(updatedTransaction.getCategory(), actualTransaction.getCategory());
        ;

        //Verify
        verify(categoryService, times(1)).read(Long.parseLong(categoryRequestDto.getCategoryId()));
        verify(transactionRepository, times(1)).findById(categoryRequestDto.getTransactionId());
        verify(transactionRepository, times(1)).save(transaction);
    }

    @Test
    void testAssignCategory_InvalidTransactionId_Failure() {
        //Arrange
        Category category = Category.builder()
                .categoryId(10L)
                .build();

        AssignCategoryRequestDto categoryRequestDto = AssignCategoryRequestDto.builder()
                .categoryId("10")
                .transactionId("invalid-id")
                .build();
        //Mock
        when(categoryService.read(Long.parseLong(categoryRequestDto.getCategoryId()))).thenReturn(category);
        when(transactionRepository.findById(categoryRequestDto.getTransactionId())).thenReturn(Optional.empty());

        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.assignCategory(categoryRequestDto);
        });
        assertNotNull(exception);
        assertEquals("Transaction with the following ID not found: " + categoryRequestDto.getTransactionId(), exception.getMessage());
    }

    @Test
    void testAssignCategory_InvalidCategoryId_Failure() {
        //Arrange
        AssignCategoryRequestDto categoryRequestDto = AssignCategoryRequestDto.builder()
                .categoryId("10")
                .transactionId("valid-id")
                .build();
        //Mock
        when(categoryService.read(Long.parseLong(categoryRequestDto.getCategoryId()))).thenThrow(new RuntimeException("Invalid Category ID: " + categoryRequestDto.getCategoryId()));

        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.assignCategory(categoryRequestDto);
        });
        assertNotNull(exception);
        assertEquals("Invalid Category ID: " + categoryRequestDto.getCategoryId(), exception.getMessage());
    }

    @Test
    void testRemoveCategory_ValidTransactionId_Successful() {
        //Arrange
        String transactionId = "valid-transaction-id";
        Category category = Category.builder()
                .categoryId(10L)
                .budgetAmount(1000.0)
                .build();
        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .category(category)
                .build();

        //Mock
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenAnswer(invocationOnMock -> {
            return invocationOnMock.getArgument(0);
        });

        //Act
        transactionService.removeAssignedCategory(transactionId);

        //Nothing to Assert

        //Verify
        verify(transactionRepository, times(1)).save(transaction);
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void testRemoveCategory_InvalidTransactionId_Failure() {
        //Arrange
        String transactionId = "invalid-transaction-id";

        //Mock
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        //Act
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.removeAssignedCategory(transactionId);
        });
        assertNotNull(exception);
        assertEquals("Transaction with the following ID not found: " + transactionId, exception.getMessage());

        //Verify
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void testAddTransaction_Successful() {
        //Arrange
        double amount = 1000.0;
        String transactionName = "transaction-name";
        LocalDate localDate = LocalDate.now();
        TransactionDto transactionDto = TransactionDto.builder()
                .updatedAmount(amount)
                .updatedName(transactionName)
                .date(localDate)
                .build();

        //Mock
        when(transactionMapper.toEntity(any(TransactionDto.class))).thenAnswer(invocationOnMock -> {
            TransactionDto dto = invocationOnMock.getArgument(0);
            Transaction transaction = new Transaction();
            transaction.setName(dto.getUpdatedName());
            transaction.setAmount(dto.getUpdatedAmount());
            transaction.setLogoUrl(dto.getLogoUrl());
            transaction.setDate(dto.getDate());
            transaction.setAccount(null);
            transaction.setCategory(null);
            return transaction;
        });
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        //Act
        Transaction transaction = transactionService.addTransaction(transactionDto);

        //Assert
        assertNotNull(transaction);
        assertEquals(amount, transaction.getAmount());
        assertEquals(transactionName, transaction.getName());
        assertEquals(localDate, transaction.getDate());
        assertNotNull(transaction.getTransactionId());
        assertNull(transaction.getAccount());
        assertNull(transaction.getCategory());
        assertNull(transaction.getLogoUrl());
    }

    @Test
    void testReduceTransactionAmount_ValidTransactionId_ValidTransactionDto_Success() {
        //Arrange
        double amount = 1000.0;
        LocalDate date = LocalDate.now();
        String transactioName = "transaction-name";
        String transactionId = "transaction-id";

        Transaction transaction = Transaction.builder()
                .amount(amount)
                .transactionId(transactionId)
                .name(transactioName)
                .date(date)
                .build();

        TransactionDto transactionDto = TransactionDto.builder()
                .updatedAmount(999.0)
                .build();

        //Mock
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        //Act
        Transaction actualTransaction = transactionService.reduceTransactionAmount(transactionId, transactionDto);

        //Assert
        assertNotNull(actualTransaction);
        assertEquals(transactionDto.getUpdatedAmount(), actualTransaction.getAmount());
        assertEquals(date, actualTransaction.getDate());
        assertEquals(transactioName, actualTransaction.getName());
        assertEquals(transactionId, actualTransaction.getTransactionId());
        assertTrue(actualTransaction.isUpdatedByUser());
    }

    @Test
    void testReduceTransactionAmount_InvalidTransactionId_Failure() {
        //Arrange
        String transactionId = "transaction-id";
        TransactionDto transactionDto = TransactionDto.builder()
                .updatedAmount(3000.0)
                .build();

        //Mock
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.reduceTransactionAmount(transactionId, transactionDto);
        });
        assertNotNull(exception);
        assertEquals("Transaction with the following ID not found: " + transactionId, exception.getMessage());
    }

    @Test
    void testReduceTransactionAmount_InvalidAmount_Failure() {
        //Arrange
        String transactionId = "transaction-id";
        Transaction transaction = Transaction.builder()
                .amount(2000.0)
                .build();

        TransactionDto transactionDto = TransactionDto.builder()
                .updatedAmount(3000.0) //higher amount than original transaction
                .build();

        //Mock
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.reduceTransactionAmount(transactionId, transactionDto);
        });
        assertNotNull(exception);
        assertEquals("Invalid Transaction amount; The provided amount must be less than the original Transaction amount.", exception.getMessage());
    }

    @Test
    void testDeleteTransaction_ValidTransactionId_Success() {
        //Arrange
        String transactionId = "transaction-id";
        Transaction transaction = Transaction.builder()
                .amount(2000.0)
                .build();

        //Mock
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        doNothing().when(transactionRepository).delete(transaction);

        //Act
        transactionService.deleteTransaction(transactionId);

        //Verify
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, times(1)).delete(transaction);
    }

    @Test
    void testDeleteTransaction_InvalidTransactionId_Failure() {
        //Arrange
        String transactionId = "transaction-id";
        String errorMsg = "Transaction with the ID " + transactionId + " not found";
        RuntimeException expectedRuntimeException = new RuntimeException(errorMsg);

        //Mock
        when(transactionRepository.findById(transactionId)).thenThrow(expectedRuntimeException);

        //Act
        RuntimeException actualRuntimException = assertThrows(RuntimeException.class, () -> {
            transactionService.deleteTransaction(transactionId);
        });
        assertEquals(expectedRuntimeException, actualRuntimException);

        //Verify
        verify(transactionRepository, times(1)).findById(transactionId);
    }

    @Test
    void testUpdateTransactionName_InvalidTransactionId_Failure() {
        //Arrange
        String invalidTransactionId = "invalid-id";
        String originalName = "original-name";
        String errorMsg = "Transaction with the following ID not found: " + invalidTransactionId;

        //Mock
        when(transactionRepository.findById(invalidTransactionId)).thenReturn(Optional.empty());

        //Act & Assert
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            transactionService.updateTransactionName(invalidTransactionId, originalName);
        });
        assertNotNull(runtimeException);
        assertEquals(errorMsg, runtimeException.getMessage());
    }

    @Test
    void testUpdateTransactionName_ValidTransactionId_Success() {
        //Arrange
        String transactionId = "valid-id";
        String originalName = "original-name";
        String updatedName = "updated-name";
        Transaction originalTransaction = Transaction.builder()
                .transactionId(transactionId)
                .name(originalName)
                .build();
        Transaction updatedTransaction = Transaction.builder()
                .transactionId(transactionId)
                .name(updatedName)
                .build();

        //Mock
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(originalTransaction));
        when(transactionRepository.save(originalTransaction)).thenReturn(updatedTransaction);

        //Act & Assert
        Transaction actualTransaction = transactionService.updateTransactionName(transactionId, updatedName);

        assertNotNull(actualTransaction);
        assertEquals(updatedName, actualTransaction.getName());
        assertEquals(transactionId, actualTransaction.getTransactionId());
    }


    @Test
    void testRemoveAccountTransactions_RemovesTransactions() {
        //Arrange
        String accountId = "account-id";

        //Mock
        doNothing().when(transactionRepository).deleteByAccountAccountId(accountId);

        //Act
        transactionService.removeAccountTransactions(accountId);

        //Verify
        verify(transactionRepository, times(1)).deleteByAccountAccountId(accountId);
    }

}
