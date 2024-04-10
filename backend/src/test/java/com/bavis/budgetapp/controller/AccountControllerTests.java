package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.AccountDTO;
import com.bavis.budgetapp.enumeration.AccountType;
import com.bavis.budgetapp.request.ConnectAccountRequest;
import com.bavis.budgetapp.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class AccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private ConnectAccountRequest connectAccountRequest;
    private AccountDTO accountDTO;
    @BeforeEach
    void setup() {
        connectAccountRequest = ConnectAccountRequest.builder()
                .accountType(AccountType.CHECKING)
                .accountName("Test Account")
                .plaidAccountId("plaid-account-id")
                .publicToken("public-token")
                .build();

        accountDTO = AccountDTO.builder()
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
        when(accountService.connectAccount(any(ConnectAccountRequest.class))).thenReturn(accountDTO);

        //Act
        ResultActions resultActions = mockMvc.perform(post("/account")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(connectAccountRequest)));

        //Assert
        resultActions.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountName").value(accountDTO.getAccountName()))
                .andExpect(jsonPath("$.balance").value(accountDTO.getBalance()))
                .andExpect(jsonPath("$.accountType").value("CHECKING"));
    }
}
