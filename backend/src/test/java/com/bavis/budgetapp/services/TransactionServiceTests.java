package com.bavis.budgetapp.services;

import com.bavis.budgetapp.clients.SuggestionEngineClient;
import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.*;
import com.bavis.budgetapp.entity.*;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.filter.TransactionFilters;
import com.bavis.budgetapp.mapper.TransactionMapper;
import com.bavis.budgetapp.model.PlaidConfidenceLevel;
import com.bavis.budgetapp.model.PlaidDetailedCategory;
import com.bavis.budgetapp.model.PlaidPrimaryCategory;
import com.bavis.budgetapp.service.impl.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

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

    @Mock
    private TransactionFilters transactionFilters;

    @Mock
    private SuggestionEngineClient suggestionEngineClient;

    @Spy
    @InjectMocks
    private TransactionServiceImpl transactionService;

    /**
     * Plaid Service Response variables
     */
    PlaidTransactionDto.CounterpartyDto counterpartyDto;
    PlaidTransactionDto.PersonalFinanceCategoryDto personalFinanceCategoryDto;
    String nextCursor;
    String previousCursor;
    LocalDateTime date;
    String accountId;
    String accessToken;
    PlaidTransactionDto plaidTransactionDtoOne;

    PlaidTransactionDto plaidTransactionDtoTwo;

    PlaidTransactionDto plaidTransactionDtoThree;

    PlaidTransactionSyncResponseDto syncResponseDto;

    List<PlaidTransactionDto> addedTransactions;
    List<PlaidTransactionDto> modifiedTransactions;
    List<PlaidTransactionDto> removedTransactions;

    @BeforeEach
    void setup() {
        nextCursor = "next-cursor";
        previousCursor = "previous-cursor";
        date = LocalDateTime.now();
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
    void testSyncTransactions_returnsExpectedAllOrModifiedTransactions() {

        // arrange
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
        ArrayList<String> accountIds = new ArrayList<>(Collections.singletonList(accountIdOne));
        AccountsDto accountsDto = AccountsDto.builder()
                .accounts(accountIds)
                .build();
        // set up added transactions for Plaid Service response
        addedTransactions = Collections.singletonList(plaidTransactionDtoOne);
        modifiedTransactions = Collections.singletonList(plaidTransactionDtoTwo);
        syncResponseDto = PlaidTransactionSyncResponseDto.builder()
                .added(addedTransactions)
                .modified(modifiedTransactions)
                .removed(new ArrayList<>())
                .next_cursor(nextCursor)
                .has_more(false)
                .build();

        // mocks
        configureSyncTransactionMocks_addedModified();
        when(accountService.read(accountIdOne)).thenReturn(accountOne);
        when(transactionRepository.findById(any())).thenReturn(Optional.of(new Transaction()));
        doNothing().when(transactionService).predictCategories(any(), any());

        // act
        SyncTransactionsDto syncTransactionsDto = transactionService.syncTransactions(accountsDto);


        // assert
        assertNotNull(syncTransactionsDto.getAllModifiedOrAddedTransactions());
        assertEquals(2, syncTransactionsDto.getAllModifiedOrAddedTransactions().size());
        assertTrue(syncTransactionsDto.getAllModifiedOrAddedTransactions().stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoOne.getTransaction_id())));
        assertTrue(syncTransactionsDto.getAllModifiedOrAddedTransactions().stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoTwo.getTransaction_id())));

        // verify
        verify(transactionRepository, times(1)).saveAllAndFlush(anyList());
    }

    @Test
    void testSyncTransactions_returnsExpectedRemovedTransactions_noPendingTransactions() {
        // arrange
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
        ArrayList<String> accountIds = new ArrayList<>(Collections.singletonList(accountIdOne));
        AccountsDto accountsDto = AccountsDto.builder()
                .accounts(accountIds)
                .build();
        // set up remove transactions for Plaid Service response
        removedTransactions = Collections.singletonList(plaidTransactionDtoTwo);
        syncResponseDto = PlaidTransactionSyncResponseDto.builder()
                .added(new ArrayList<>())
                .modified(new ArrayList<>())
                .removed(removedTransactions)
                .next_cursor(nextCursor)
                .has_more(false)
                .build();

        // mocks
        configureSyncTransactionMocks_removed(false);
        when(accountService.read(accountIdOne)).thenReturn(accountOne);

        // act
        SyncTransactionsDto syncTransactionsDto = transactionService.syncTransactions(accountsDto);


        // assert
        assertNotNull(syncTransactionsDto.getRemovedTransactionIds());
        assertEquals(1, syncTransactionsDto.getRemovedTransactionIds().size());
        assertEquals(plaidTransactionDtoTwo.getTransaction_id(), syncTransactionsDto.getRemovedTransactionIds().get(0));

        //verify
        verify(transactionRepository, times(1)).deleteAllById(anyList());
    }


    @Test
    void testSyncTransactions_returnsExpectedRemovedTransactions_hasPendingTransactionsMarkedForRemoval() {
        // arrange
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
        ArrayList<String> accountIds = new ArrayList<>(Collections.singletonList(accountIdOne));
        AccountsDto accountsDto = AccountsDto.builder()
                .accounts(accountIds)
                .build();
        // set up remove transactions for Plaid Service response
        removedTransactions = Collections.singletonList(plaidTransactionDtoTwo);
        syncResponseDto = PlaidTransactionSyncResponseDto.builder()
                .added(new ArrayList<>())
                .modified(new ArrayList<>())
                .removed(removedTransactions)
                .next_cursor(nextCursor)
                .has_more(false)
                .build();

        // mocks
        configureSyncTransactionMocks_removed(true);
        when(accountService.read(accountIdOne)).thenReturn(accountOne);

        // act
        SyncTransactionsDto syncTransactionsDto = transactionService.syncTransactions(accountsDto);


        // assert transaction ID was filtered out
        assertNotNull(syncTransactionsDto.getRemovedTransactionIds());
        assertEquals(0, syncTransactionsDto.getRemovedTransactionIds().size());
    }

    @Test
    void testSyncTransactions_savesModifiedTransactionsCategory() {
        // arrange
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
        Category transactionCategory = Category.builder().categoryId(1L).build();
        ArrayList<String> accountIds = new ArrayList<>(Collections.singletonList(accountIdOne));
        AccountsDto accountsDto = AccountsDto.builder()
                .accounts(accountIds)
                .build();
        Transaction transaction = new Transaction();
        transaction.setTransactionId("123");
        transaction.setCategory(transactionCategory);

        // set up added transactions for Plaid Service response
        modifiedTransactions = Collections.singletonList(plaidTransactionDtoTwo);
        syncResponseDto = PlaidTransactionSyncResponseDto.builder()
                .added(new ArrayList<>())
                .modified(modifiedTransactions)
                .removed(new ArrayList<>())
                .next_cursor(nextCursor)
                .has_more(false)
                .build();

        // mocks
        configureSyncTransactionMocks_addedModified();
        when(accountService.read(accountIdOne)).thenReturn(accountOne);
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        doNothing().when(transactionService).predictCategories(any(), any());

        // act
        SyncTransactionsDto syncTransactionsDto = transactionService.syncTransactions(accountsDto);


        // assert
        assertNotNull(syncTransactionsDto.getAllModifiedOrAddedTransactions());
        assertEquals(1, syncTransactionsDto.getAllModifiedOrAddedTransactions().size());
        assertEquals(transactionCategory, syncTransactionsDto.getAllModifiedOrAddedTransactions().get(0).getCategory()); //ensure category is still persisted

        // verify
        verify(transactionRepository, times(1)).saveAllAndFlush(anyList());
    }

    @Test
    void testSyncTransactions_invokesPredictionFlow() {
        // arrange
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
        Category transactionCategory = Category.builder().categoryId(1L).build();
        ArrayList<String> accountIds = new ArrayList<>(Collections.singletonList(accountIdOne));
        AccountsDto accountsDto = AccountsDto.builder()
                .accounts(accountIds)
                .build();
        Transaction transaction = new Transaction();
        transaction.setTransactionId("123");
        transaction.setCategory(transactionCategory);

        // set up added transactions for Plaid Service response
        modifiedTransactions = Collections.singletonList(plaidTransactionDtoTwo);
        syncResponseDto = PlaidTransactionSyncResponseDto.builder()
                .added(new ArrayList<>())
                .modified(modifiedTransactions)
                .removed(new ArrayList<>())
                .next_cursor(nextCursor)
                .has_more(false)
                .build();

        // mocks
        configureSyncTransactionMocks_addedModified();
        when(accountService.read(accountIdOne)).thenReturn(accountOne);
        when(transactionRepository.findById(any())).thenReturn(Optional.of(transaction));
        doNothing().when(transactionService).predictCategories(any(), any());

        // act
        SyncTransactionsDto syncTransactionsDto = transactionService.syncTransactions(accountsDto);


        // assert
        assertNotNull(syncTransactionsDto.getAllModifiedOrAddedTransactions());
        assertEquals(1, syncTransactionsDto.getAllModifiedOrAddedTransactions().size());
        assertEquals(transactionCategory, syncTransactionsDto.getAllModifiedOrAddedTransactions().get(0).getCategory());

        // verify
        verify(transactionService, times(2)).predictCategories(any(), any()); //predicts added/modified and prev month transactions
    }

    @Test
    void testSyncTransactions_returnsExpectedPrevMonthTransactions() {
        // arrange
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
        ArrayList<String> accountIds = new ArrayList<>(Collections.singletonList(accountIdOne));
        AccountsDto accountsDto = AccountsDto.builder()
                .accounts(accountIds)
                .build();

        // set up added transactions for Plaid Service response
        addedTransactions = Collections.singletonList(plaidTransactionDtoOne);
        modifiedTransactions = Collections.singletonList(plaidTransactionDtoTwo);
        syncResponseDto = PlaidTransactionSyncResponseDto.builder()
                .added(addedTransactions)
                .modified(modifiedTransactions)
                .removed(new ArrayList<>())
                .next_cursor(nextCursor)
                .has_more(false)
                .build();

        // mocks
        configureSyncTransactionMocks_prevMonth();
        when(accountService.read(accountIdOne)).thenReturn(accountOne);
        when(transactionRepository.findById(any())).thenReturn(Optional.of(new Transaction()));
        doNothing().when(transactionService).predictCategories(any(), any());



        // act
        SyncTransactionsDto syncTransactionsDto = transactionService.syncTransactions(accountsDto);


        // assert expected prev month transactions are present
        assertNotNull(syncTransactionsDto.getPreviousMonthTransactions());
        assertEquals(2, syncTransactionsDto.getPreviousMonthTransactions().size());
        assertTrue(syncTransactionsDto.getPreviousMonthTransactions().stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoOne.getTransaction_id())));
        assertTrue(syncTransactionsDto.getPreviousMonthTransactions().stream().anyMatch(transaction -> transaction.getTransactionId().equals(plaidTransactionDtoTwo.getTransaction_id())));

        // validate all or modified were filtered out given they were for previous month
        assertTrue(syncTransactionsDto.getAllModifiedOrAddedTransactions().isEmpty());

        // verify
        verify(transactionRepository, times(1)).saveAllAndFlush(anyList());
    }

    @Test
    void testSyncTransactions_updatesConnectionWithNewCursor(){
        // set up argument captor
        ArgumentCaptor<Connection> connectionCaptor = ArgumentCaptor.forClass(Connection.class);

        // arrange
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
        ArrayList<String> accountIds = new ArrayList<>(Collections.singletonList(accountIdOne));
        AccountsDto accountsDto = AccountsDto.builder()
                .accounts(accountIds)
                .build();
        // set up added transactions for Plaid Service response
        addedTransactions = Collections.singletonList(plaidTransactionDtoOne);
        modifiedTransactions = Collections.singletonList(plaidTransactionDtoTwo);
        syncResponseDto = PlaidTransactionSyncResponseDto.builder()
                .added(addedTransactions)
                .modified(modifiedTransactions)
                .removed(new ArrayList<>())
                .next_cursor(nextCursor)
                .has_more(false)
                .build();

        // mocks
        configureSyncTransactionMocks_addedModified();
        when(accountService.read(accountIdOne)).thenReturn(accountOne);
        when(transactionRepository.findById(any())).thenReturn(Optional.of(new Transaction()));
        doNothing().when(transactionService).predictCategories(any(), any());

        // act
        SyncTransactionsDto syncTransactionsDto = transactionService.syncTransactions(accountsDto);

        // verify connection updated with expected
        verify(connectionService, times(1)).update(connectionCaptor.capture(), any());

        Connection capturedConnection = connectionCaptor.getValue();
        assertEquals(nextCursor, capturedConnection.getOriginalCursor()); // original cursor also set since there is not one persisted for mock conection
        assertEquals(nextCursor, capturedConnection.getPreviousCursor());

    }

    @Test
    void testSyncTransactions_accountsForPaginatedResponse() {
        // arrange
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
        ArrayList<String> accountIds = new ArrayList<>(Collections.singletonList(accountIdOne));
        AccountsDto accountsDto = AccountsDto.builder()
                .accounts(accountIds)
                .build();

        // First page
        List<PlaidTransactionDto> addedPage1 = List.of(plaidTransactionDtoOne);
        List<PlaidTransactionDto> modifiedPage1 = List.of(plaidTransactionDtoTwo);

        PlaidTransactionSyncResponseDto page1Response = PlaidTransactionSyncResponseDto.builder()
                .added(addedPage1)
                .modified(modifiedPage1)
                .removed(new ArrayList<>())
                .next_cursor("cursor-page-2")
                .has_more(true)
                .build();

        // Second page
        List<PlaidTransactionDto> addedPage2 = List.of(plaidTransactionDtoThree);
        List<PlaidTransactionDto> modifiedPage2 = List.of(plaidTransactionDtoOne);

        PlaidTransactionSyncResponseDto page2Response = PlaidTransactionSyncResponseDto.builder()
                .added(addedPage2)
                .modified(modifiedPage2)
                .removed(new ArrayList<>())
                .next_cursor("cursor-final")
                .has_more(false)
                .build();

        // Mocks
        when(accountService.read(accountIdOne)).thenReturn(accountOne);
        configureSyncTransactions_multiplePagesMocks(page1Response, page2Response);
        doNothing().when(transactionService).predictCategories(any(), any());

        // act
        SyncTransactionsDto result = transactionService.syncTransactions(accountsDto);

        // verify plaid service was called twice (pagination)
        verify(plaidService, times(2)).syncTransactions(eq(accessToken), any());

        // verify repository saved 2 + 2 = 4 transactions
        ArgumentCaptor<List<Transaction>> captor = ArgumentCaptor.forClass(List.class);
        verify(transactionRepository, times(1)).saveAllAndFlush(captor.capture());

        List<List<Transaction>> allSavedBatches = captor.getAllValues();
        int totalSaved = allSavedBatches.stream().mapToInt(List::size).sum();

        assertEquals(4, totalSaved, "Expected total 4 transactions saved across paginated responses");
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
        // Arrange
        String transactionId = "transaction-id";
        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .amount(2000.0)
                .isDeleted(false)
                .build();

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);

        // Mock
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        doAnswer(invocationOnMock -> invocationOnMock.getArgument(0))
                .when(transactionRepository).save(any(Transaction.class));

        // Act
        transactionService.deleteTransaction(transactionId);

        // Verify
        verify(transactionRepository, times(1)).findById(transactionId);
        verify(transactionRepository, times(1)).save(captor.capture());

        Transaction capturedTransaction = captor.getValue();

        // Assert soft deletion
        assertTrue(capturedTransaction.isDeleted(), "Transaction should be marked as deleted");
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

    @Test
    void testPredictCategories_updatesSuggestedCategory() throws JsonProcessingException {
        Transaction transaction = Transaction.builder()
                .amount(1000.00)
                .dateTime(LocalDateTime.now())
                .personalFinanceCategory(
                        new Transaction.PersonalFinanceCategory(
                                PlaidConfidenceLevel.HIGH,
                                PlaidPrimaryCategory.FOOD_AND_DRINK,
                                PlaidDetailedCategory.FOOD_AND_DRINK_COFFEE
                        )
                )
                .merchantName("Dunkin'")
                .build();
        List<Transaction> transactions = List.of(transaction);
        Category category = new Category();

        // mocks
        when(suggestionEngineClient.predictCategory(any())).thenReturn(1L);
        when(categoryService.read(1L)).thenReturn(category);

        // act
        transactionService.predictCategories(transactions, 1L);

        // verify
        verify(suggestionEngineClient, times(1)).predictCategory(any());

        // assert
        assertEquals(transaction.getSuggestedCategory(), category);
    }

    @Test
    void testPredictCategories_handlesNullCategory() throws JsonProcessingException {
        Transaction transaction = Transaction.builder()
                .amount(1000.00)
                .dateTime(LocalDateTime.now())
                .personalFinanceCategory(
                        new Transaction.PersonalFinanceCategory(
                                PlaidConfidenceLevel.HIGH,
                                PlaidPrimaryCategory.FOOD_AND_DRINK,
                                PlaidDetailedCategory.FOOD_AND_DRINK_COFFEE
                        )
                )
                .merchantName("Dunkin'")
                .build();
        List<Transaction> transactions = List.of(transaction);
        Category category = new Category();

        // mocks
        when(suggestionEngineClient.predictCategory(any())).thenReturn(null);

        // act
        transactionService.predictCategories(transactions, 1L);

        // verify
        verify(suggestionEngineClient, times(1)).predictCategory(any());

        // assert
        assertNull(transaction.getSuggestedCategory());
    }

    private void configureSyncTransactionMocks_addedModified() {
        when(plaidService.syncTransactions(accessToken, previousCursor)).thenReturn(syncResponseDto);
        when(transactionMapper.toEntity(any(PlaidTransactionDto.class))).thenAnswer(invocationOnMock -> {
            PlaidTransactionDto dto = invocationOnMock.getArgument(0);
            return Transaction.builder()
                    .transactionId(dto.getTransaction_id())
                    .name(dto.getCounterparties().get(0).getName())
                    .amount(dto.getAmount())
                    .date(dto.getDate())
                    .logoUrl(dto.getCounterparties().get(0).getLogo_url())
                    .account(null)
                    .category(null)
                    .build();
        });
        when(transactionRepository.saveAllAndFlush(anyList())).thenAnswer(invocationOnMock -> invocationOnMock.<List<Transaction>>getArgument(0));
        when(connectionService.update(any(Connection.class), any(Long.class))).thenAnswer(invocationOnMock -> {
            return invocationOnMock.<Connection>getArgument(0);
        });
        when(transactionFilters.addedTransactionFilters()).thenReturn(t -> true);
        when(transactionFilters.modifiedTransactionFilters()).thenReturn(t -> true);
        when(transactionFilters.isPendingAndUserModified(any())).thenReturn(t -> true);
        when(transactionFilters.prevMonthTransactionFilters(any())).thenReturn(t -> false);
    }

    private void configureSyncTransactionMocks_prevMonth() {
        when(plaidService.syncTransactions(accessToken, previousCursor)).thenReturn(syncResponseDto);
        when(transactionMapper.toEntity(any(PlaidTransactionDto.class))).thenAnswer(invocationOnMock -> {
            PlaidTransactionDto dto = invocationOnMock.getArgument(0);
            return Transaction.builder()
                    .transactionId(dto.getTransaction_id())
                    .name(dto.getCounterparties().get(0).getName())
                    .amount(dto.getAmount())
                    .date(dto.getDate())
                    .logoUrl(dto.getCounterparties().get(0).getLogo_url())
                    .account(null)
                    .category(null)
                    .build();
        });
        when(transactionRepository.saveAllAndFlush(anyList())).thenAnswer(invocationOnMock -> invocationOnMock.<List<Transaction>>getArgument(0));
        when(connectionService.update(any(Connection.class), any(Long.class))).thenAnswer(invocationOnMock -> {
            Connection connectionToUpdate = invocationOnMock.getArgument(0);
            connectionToUpdate.setPreviousCursor(previousCursor);
            return connectionToUpdate;
        });
        when(transactionFilters.addedTransactionFilters()).thenReturn(t -> true);
        when(transactionFilters.modifiedTransactionFilters()).thenReturn(t -> true);
        when(transactionFilters.isPendingAndUserModified(any())).thenReturn(t -> true);
        when(transactionFilters.prevMonthTransactionFilters(any())).thenReturn(t -> true);
    }

    private void configureSyncTransactionMocks_removed(boolean isFiltered) {
        when(plaidService.syncTransactions(accessToken, previousCursor)).thenReturn(syncResponseDto);
        when(transactionFilters.modifiedTransactionFilters()).thenReturn(t -> false);
        when(transactionFilters.prevMonthTransactionFilters(any())).thenReturn(t -> false);
        when(transactionFilters.addedTransactionFilters()).thenReturn(t -> false);


        // account for pending transaction IDs being filtered out of ids to be removed
        if(!isFiltered) {
            doNothing().when(transactionRepository).deleteAllById(anyList());
            when(transactionFilters.isPendingAndUserModified(any())).thenReturn(t -> false);
        } else {
            // modify set passed as arg with ID to filter out
            doAnswer(invocation -> {
                Set<String> setArg = invocation.getArgument(0);
                setArg.add(plaidTransactionDtoTwo.getTransaction_id());

                return (Predicate<PlaidTransactionDto>) dto -> true;
            }).when(transactionFilters).isPendingAndUserModified(anySet());

        }
    }

    private void configureSyncTransactions_multiplePagesMocks(PlaidTransactionSyncResponseDto page1Response, PlaidTransactionSyncResponseDto page2Response) {
        when(transactionRepository.findById(any())).thenReturn(Optional.of(new Transaction()));
        when(plaidService.syncTransactions(eq(accessToken), eq(previousCursor))).thenReturn(page1Response);
        when(plaidService.syncTransactions(eq(accessToken), argThat(cursor -> !previousCursor.equals(cursor))))
                .thenReturn(page2Response);
        when(transactionFilters.addedTransactionFilters()).thenReturn(t -> true);
        when(transactionFilters.modifiedTransactionFilters()).thenReturn(t -> true);
        when(transactionFilters.isPendingAndUserModified(any())).thenReturn(t -> true);
        when(transactionFilters.prevMonthTransactionFilters(any())).thenReturn(t -> false);
        when(transactionRepository.saveAllAndFlush(anyList())).thenAnswer(invocationOnMock -> invocationOnMock.<List<Transaction>>getArgument(0));
        when(connectionService.update(any(Connection.class), any(Long.class))).thenAnswer(invocationOnMock -> {
            return invocationOnMock.<Connection>getArgument(0);
        });
        when(transactionMapper.toEntity(any(PlaidTransactionDto.class))).thenAnswer(invocationOnMock -> {
            PlaidTransactionDto dto = invocationOnMock.getArgument(0);
            return Transaction.builder()
                    .transactionId(dto.getTransaction_id())
                    .name(dto.getCounterparties().get(0).getName())
                    .amount(dto.getAmount())
                    .date(dto.getDate())
                    .logoUrl(dto.getCounterparties().get(0).getLogo_url())
                    .account(null)
                    .category(null)
                    .build();
        });
    }

}
