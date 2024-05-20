package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.AssignCategoryRequestDto;
import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.*;
import com.bavis.budgetapp.service.impl.AccountServiceImpl;
import com.bavis.budgetapp.service.impl.CategoryServiceImpl;
import com.bavis.budgetapp.service.impl.TransactionServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles(profiles = "test")
@WithMockUser
public class TransactionControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionServiceImpl transactionService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private AccountServiceImpl accountService;

    @MockBean
    private CategoryServiceImpl categoryService;


    @Autowired
    private ObjectMapper objectMapper;

    private TransactionSyncRequestDto validTransactionSyncRequest;

    private TransactionSyncRequestDto invalidTransactionSyncRequest;

    private List<String> validAccountIds;

    private List<String> invalidAccountIds;

    private List<Transaction> validTransactions;

    private Account accountOne;

    private Account accountTwo;

    private User authUser;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper();

        validAccountIds = List.of("12345", "6789");
        invalidAccountIds = List.of("ABCDE");
        LocalDate transactionDate = LocalDate.now();

        validTransactionSyncRequest = TransactionSyncRequestDto.builder()
                .accounts(validAccountIds)
                .build();

        invalidTransactionSyncRequest = TransactionSyncRequestDto.builder()
                .accounts(invalidAccountIds)
                .build();

        authUser = User.builder()
                .userId(12345L)
                .build();

        accountOne = Account.builder()
                .accountId(validAccountIds.get(0))
                .user(authUser)
                .build();

        accountTwo = Account.builder()
                .accountId(validAccountIds.get(1))
                .user(authUser)
                .build();


        Transaction transactionOne = Transaction.builder()
                .transactionId("ABCDE12345")
                .date(transactionDate)
                .amount(1000.0)
                .account(accountOne)
                .category(null) //TODO: update this to be a category when we intelligently assign Category in future
                .name("Chase Bank")
                .logoUrl("https://chase-bank-logo.com")
                .build();

        Transaction transactionTwo = Transaction.builder()
                .transactionId("54321EDCBA")
                .date(transactionDate)
                .amount(2000.0)
                .account(accountTwo)
                .category(null) //TODO: update this to be a category when we intelligently assign Category in future
                .name("Wegmans")
                .logoUrl("https://wegmans-logo.com")
                .build();

        validTransactions = List.of(transactionOne, transactionTwo);
    }

    @Test
    void testSyncTransactions_ValidRequest_Successful() throws Exception{
        //Arrange
        Transaction transactionOne = validTransactions.get(0);
        Transaction transactionTwo = validTransactions.get(1);
        String accountOneId = validAccountIds.get(0);
        String accountTwoId = validAccountIds.get(1);

        //Mock
        when(transactionService.syncTransactions(validTransactionSyncRequest)).thenReturn(validTransactions);
        when(userService.getCurrentAuthUser()).thenReturn(authUser);
        when(accountService.read(accountOneId)).thenReturn(accountOne);
        when(accountService.read(accountTwoId)).thenReturn(accountTwo);

        //Act
        ResultActions resultActions = mockMvc.perform(post("/transactions/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validTransactionSyncRequest)));

        //Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].transactionId").value(transactionOne.getTransactionId()))
                .andExpect(jsonPath("$[0].date").value(transactionOne.getDate().toString()))
                .andExpect(jsonPath("$[0].amount").value(transactionOne.getAmount()))
                .andExpect(jsonPath("$[0].name").value(transactionOne.getName()))
                .andExpect(jsonPath("$[0].category").value(transactionOne.getCategory()))
                .andExpect(jsonPath("$[0].logoUrl").value(transactionOne.getLogoUrl()))
                .andExpect(jsonPath("$[1].transactionId").value(transactionTwo.getTransactionId()))
                .andExpect(jsonPath("$[1].date").value(transactionTwo.getDate().toString()))
                .andExpect(jsonPath("$[1].amount").value(transactionTwo.getAmount()))
                .andExpect(jsonPath("$[1].category").value(transactionTwo.getCategory()))
                .andExpect(jsonPath("$[1].logoUrl").value(transactionTwo.getLogoUrl()))
                .andExpect(jsonPath("$[1].name").value(transactionTwo.getName()));


        //Verify
        verify(transactionService, times(1)).syncTransactions(validTransactionSyncRequest);
        verify(userService, times(2)).getCurrentAuthUser();
        verify(accountService, times(1)).read(accountOneId);
        verify(accountService, times(1)).read(accountTwoId);
    }

    @Test
    void testReadAll_ValidAccounts_Successful() throws Exception {
        //Arrange
        Transaction transactionOne = validTransactions.get(0);
        Transaction transactionTwo = validTransactions.get(1);
        List<Transaction> expectedTransactions = List.of(transactionOne, transactionTwo);

        //Mock
        when(transactionService.readAll()).thenReturn(expectedTransactions);

        //Act
        ResultActions resultActions = mockMvc.perform(get("/transactions"));

        //Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].transactionId").value(transactionOne.getTransactionId()))
                .andExpect(jsonPath("$[0].date").value(transactionOne.getDate().toString()))
                .andExpect(jsonPath("$[0].amount").value(transactionOne.getAmount()))
                .andExpect(jsonPath("$[0].name").value(transactionOne.getName()))
                .andExpect(jsonPath("$[0].category").value(transactionOne.getCategory()))
                .andExpect(jsonPath("$[0].logoUrl").value(transactionOne.getLogoUrl()))
                .andExpect(jsonPath("$[1].transactionId").value(transactionTwo.getTransactionId()))
                .andExpect(jsonPath("$[1].date").value(transactionTwo.getDate().toString()))
                .andExpect(jsonPath("$[1].amount").value(transactionTwo.getAmount()))
                .andExpect(jsonPath("$[1].category").value(transactionTwo.getCategory()))
                .andExpect(jsonPath("$[1].logoUrl").value(transactionTwo.getLogoUrl()))
                .andExpect(jsonPath("$[1].name").value(transactionTwo.getName()));
    }

    @Test
    void testReadAll_InvalidAccounts_Successful() throws Exception {
        //Mock
        when(transactionService.readAll()).thenReturn(new ArrayList<>());

        //Act
        ResultActions resultActions = mockMvc.perform(get("/transactions"));

        //Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));
    }

    @Test
    void testSyncTransaction_InvalidAccount_Failure() throws Exception{
        //Arrange
        String invalidAccountIdOne = invalidAccountIds.get(0);

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(authUser);
        when(accountService.read(invalidAccountIdOne)).thenThrow(new RuntimeException("Unable to locate Account with ID " + invalidAccountIdOne));

        //Act
        ResultActions resultActions = mockMvc.perform(post("/transactions/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTransactionSyncRequest)));

        //Assert
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The provided list of Account ID's to sync transactions for contains at least one invalid entry"));

        //Verify
        verify(accountService, times(1)).read(invalidAccountIdOne);
    }

    @Test
    void testSyncTransactions_InvalidAuthorization_Failure() throws Exception{
        //Arrange
        User nonAuthUser = User.builder()
                .userId(12L)
                .build();
        String invalidAccountIdOne = invalidAccountIds.get(0);
        Account accountWithNonAuthUser = Account.builder()
                .accountId(invalidAccountIdOne)
                .user(nonAuthUser)
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(authUser);
        when(accountService.read(invalidAccountIdOne)).thenReturn(accountWithNonAuthUser);

        //Act
        ResultActions resultActions = mockMvc.perform(post("/transactions/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidTransactionSyncRequest)));

        //Assert
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The provided list of Account ID's to sync transactions for contains at least one invalid entry"));

        //Verify
        verify(accountService, times(1)).read(invalidAccountIdOne);
        verify(userService, times(1)).getCurrentAuthUser();
    }

    @Test
    void testAssignCategory_ValidRequest_Success() throws Exception {
        //Arrange
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(new ArrayList<>())
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.5)
                .build();

        Category category = Category.builder()
                .categoryType(categoryType)
                .user(authUser)
                .name("Category")
                .budgetAllocationPercentage(.5)
                .budgetAmount(1000.0)
                .categoryId(10L)
                .build();

        Transaction transactionOne = validTransactions.get(0);
        transactionOne.setCategory(category);
        AssignCategoryRequestDto assignCategoryRequestDto = AssignCategoryRequestDto.builder()
                .categoryId("10")
                .transactionId(transactionOne.getTransactionId())
                .build();

        //Mock
        when(transactionService.assignCategory(assignCategoryRequestDto)).thenReturn(transactionOne);
        when(userService.getCurrentAuthUser()).thenReturn(authUser);
        when(categoryService.read(Long.parseLong(assignCategoryRequestDto.getCategoryId()))).thenReturn(category);
        when(transactionService.readById(assignCategoryRequestDto.getTransactionId())).thenReturn(transactionOne);

        //Act
        ResultActions resultActions = mockMvc.perform(put("/transactions/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignCategoryRequestDto)));

        // Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.transactionId").value(transactionOne.getTransactionId()))
                .andExpect(jsonPath("$.date").value(transactionOne.getDate().toString()))
                .andExpect(jsonPath("$.amount").value(transactionOne.getAmount()))
                .andExpect(jsonPath("$.name").value(transactionOne.getName()))
                .andExpect(jsonPath("$.logoUrl").value(transactionOne.getLogoUrl()))
                .andExpect(jsonPath("$.category.name").value(category.getName()))
                .andExpect(jsonPath("$.category.budgetAllocationPercentage").value(category.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$.category.budgetAmount").value(category.getBudgetAmount()))
                .andExpect(jsonPath("$.category.categoryType.name").value(categoryType.getName()))
                .andExpect(jsonPath("$.category.categoryType.budgetAllocationPercentage").value(categoryType.getBudgetAllocationPercentage()))
                .andExpect(jsonPath("$.category.categoryType.budgetAmount").value(categoryType.getBudgetAmount()))
                .andExpect(jsonPath("$.category.categoryType.categoryTypeId").value(categoryType.getCategoryTypeId()));
    }

    @Test
    void testAssignCategory_InvalidRequest_Failure() throws Exception {
        //Arrange
        User unAuthUser = User.builder()
                .userId(10L)
                .username("username")
                .build();

        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(10L)
                .categories(new ArrayList<>())
                .budgetAmount(1000.0)
                .budgetAllocationPercentage(.5)
                .build();

        Category category = Category.builder()
                .categoryType(categoryType)
                .user(unAuthUser)
                .name("Category")
                .budgetAllocationPercentage(.5)
                .budgetAmount(1000.0)
                .categoryId(10L)
                .build();

        Transaction transactionOne = validTransactions.get(0);
        transactionOne.setCategory(category);
        AssignCategoryRequestDto assignCategoryRequestDto = AssignCategoryRequestDto.builder()
                .categoryId("10")
                .transactionId(transactionOne.getTransactionId())
                .build();

        //Mock
        when(transactionService.assignCategory(assignCategoryRequestDto)).thenReturn(transactionOne);
        when(userService.getCurrentAuthUser()).thenReturn(authUser);
        when(categoryService.read(Long.parseLong(assignCategoryRequestDto.getCategoryId()))).thenReturn(category);
        when(transactionService.readById(assignCategoryRequestDto.getTransactionId())).thenReturn(transactionOne);

        //Act
        ResultActions resultActions = mockMvc.perform(put("/transactions/category")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignCategoryRequestDto)));

        // Assert
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("The provided Category ID and Transaction ID do not correspond to Authenticated User."));
    }
}
