package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.AccountDto;
import com.bavis.budgetapp.constants.AccountType;
import com.bavis.budgetapp.exception.AccountConnectionException;
import com.bavis.budgetapp.dto.ConnectAccountRequestDto;
import com.bavis.budgetapp.service.AccountService;
import com.bavis.budgetapp.service.impl.AccountServiceImpl;
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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
@ActiveProfiles(profiles = "test")
public class AccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountServiceImpl accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private ConnectAccountRequestDto connectAccountRequestDto;
    private AccountDto accountDTO;
    @BeforeEach
    void setup() {
        connectAccountRequestDto = ConnectAccountRequestDto.builder()
                .accountType(AccountType.CHECKING)
                .accountName("Test Account")
                .plaidAccountId("plaid-account-id")
                .publicToken("public-token")
                .build();

        accountDTO = AccountDto.builder()
                .accountType(AccountType.CHECKING)
                .balance(1000.0)
                .accountName("Test Account")
                .build();
    }

    /**
     * Validate that our AccountController can properly handle successful connectAccount requests
     *
     * @throws Exception
     */
    @Test
    public void testConnectAccount_ValidRequest_Successful() throws Exception {
        //Mock
        when(accountService.connectAccount(any(ConnectAccountRequestDto.class))).thenReturn(accountDTO);

        //Act
        ResultActions resultActions = mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(connectAccountRequestDto)));

        //Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountName").value(accountDTO.getAccountName()))
                .andExpect(jsonPath("$.balance").value(accountDTO.getBalance()))
                .andExpect(jsonPath("$.accountType").value("CHECKING"));
    }

    @Test
    public void testConnectAccount_InvalidRequest_EmptyPlaidAccountId_Failure() throws Exception {
        //Arrange
        connectAccountRequestDto.setPlaidAccountId("");

        //Act
        ResultActions resultActions = mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(connectAccountRequestDto)));

        // Assert
        resultActions.andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.error").value("plaidAccountId must not be empty"));
    }

    @Test
    public void testConnectAccount_InvalidRequest_EmptyAccountName_Failure() throws Exception {
        //Arrange
        connectAccountRequestDto.setAccountName("");

        //Act
        ResultActions resultActions = mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(connectAccountRequestDto)));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("accountName must not be empty")));
    }


    @Test
    public void testConnectAccount_InvalidRequest_EmptyPublicToken_Failure() throws Exception {
        //Arrange
        connectAccountRequestDto.setPublicToken("");

        //Act
        ResultActions resultActions = mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(connectAccountRequestDto)));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("publicToken must not be empty")));
    }


    @Test
    public void testConnectAccount_InvalidRequest_EmptyAccountType_Failure() throws Exception {
        //Arrange
        connectAccountRequestDto.setAccountType(null);

        //Act
        ResultActions resultActions = mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(connectAccountRequestDto)));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("accountType must not be null")));
    }

    @Test
    public void testConnectAccount_InvalidRequest_InvalidAccountType_Failure() throws Exception {
        // Arrange
        String invalidAccountType = "FAILURE";

        // Act
        ResultActions resultActions = mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"plaidAccountId\":\"plaid-account-id\",\"accountName\":\"Test Account\",\"publicToken\":\"public-token\",\"accountType\":\"" + invalidAccountType + "\"}"));

        // Assert
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid AccountType value: '" + invalidAccountType + "'. Accepted values are: CREDIT, CHECKING, SAVING, INVESTMENT, LOAN")));
    }

    @Test
    public void testConnectAccount_AccountConnectionException_Failure() throws Exception {
        //Arrange
        String plaidErrorMsg = "PlaidServiceException: [failed to retrieve you account balance]";
        String expectedErrorMsg = "An error occurred when creating an account: [" + plaidErrorMsg+ "]";

        //Mock
        when(accountService.connectAccount(connectAccountRequestDto)).thenThrow(new AccountConnectionException(expectedErrorMsg));

        //Act
        ResultActions resultActions = mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(connectAccountRequestDto)));

        //Assert
        resultActions.andExpect(status().isConflict())
                .andExpect(content().string(containsString(expectedErrorMsg)));
    }
}
