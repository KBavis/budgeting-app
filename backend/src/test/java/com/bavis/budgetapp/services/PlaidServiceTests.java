package com.bavis.budgetapp.services;

import com.bavis.budgetapp.clients.PlaidClient;
import com.bavis.budgetapp.config.PlaidConfig;
import com.bavis.budgetapp.helper.TestHelper;
import com.bavis.budgetapp.request.ExchangeTokenRequest;
import com.bavis.budgetapp.request.LinkTokenRequest;
import com.bavis.budgetapp.request.RetrieveBalanceRequest;
import com.bavis.budgetapp.response.AccessTokenResponse;
import com.bavis.budgetapp.response.LinkTokenResponse;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.service.impl.PlaidServiceImpl;
import com.bavis.budgetapp.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlaidServiceTests {

    @Mock
    private PlaidClient plaidClient;

    @Mock
    private PlaidConfig plaidConfig;

    @Mock
    private JsonUtil jsonUtil;

    @InjectMocks
    private PlaidServiceImpl plaidService;

    private JsonUtil _jsonUtil;
    private TestHelper _testHelper;

    @BeforeEach
    public void setup() {
        ObjectMapper mapper = new ObjectMapper();
        _jsonUtil = new JsonUtil(mapper);
        _testHelper = new TestHelper();
    }


    /**
     * Validate PlaidService ability to generate link token successfully
     */
    @Test
    public void testGenerateLinkToken_Success() {
        //Arrange
        Long userId = 123L;
        String expectedLinkToken = "link-token";
        LinkTokenResponse linkTokenResponse = new LinkTokenResponse();
        linkTokenResponse.setLinkToken(expectedLinkToken);
        ResponseEntity<LinkTokenResponse> responseEntity = new ResponseEntity<>(linkTokenResponse, HttpStatus.OK);


        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createLinkToken(any(LinkTokenRequest.class))).thenReturn(responseEntity);

        //Act
        String actualLinkToken = plaidService.generateLinkToken(userId);

        //Assert
        assertEquals(expectedLinkToken, actualLinkToken);
    }

    /**
     * Validate ability of PlaidService to retrieve bank account balance successfully
     */
    @Test
    public void testRetrieveBalance_Success() {
        //Arrange
        String accountId = "account-id";
        String accessToken = "access-token";
        double expectedBalance = 1000.0;
        String responseBody = _jsonUtil.toJson(_testHelper.createBalanceResponse(accountId, expectedBalance));
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.retrieveAccountBalance(any(RetrieveBalanceRequest.class))).thenReturn(responseEntity);
        when(jsonUtil.extractBalanceByAccountId(responseBody, accountId, "/balances/available"))
                .thenReturn(_jsonUtil.extractBalanceByAccountId(responseBody, accountId, "/balances/available"));


        //Act
        double actualBalance = plaidService.retrieveBalance(accountId, accessToken);

        //Assert
        assertEquals(expectedBalance, actualBalance);
    }

    /**
     * Validate ability of PlaidService to exchange public token for access token successfully
     */
    @Test
    public void testExchangeToken_Success() {
        //Arrange
        String publicToken = "public-token";
        String expectedAccessToken = "access-token";
        AccessTokenResponse accessTokenResponse = AccessTokenResponse.builder()
                .accessToken(expectedAccessToken)
                .build();
        ResponseEntity<AccessTokenResponse> responseEntity = new ResponseEntity<>(accessTokenResponse, HttpStatus.OK);


        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createAccessToken(any(ExchangeTokenRequest.class))).thenReturn(responseEntity);

        //Act
        String actualAccessToken = plaidService.exchangeToken(publicToken);

        //Assert
        assertEquals(expectedAccessToken, actualAccessToken);
    }

    /**
     * Ensures our PlaidService will handle failed exchange token attempts gracefully
     */
    @Test
    public void testExchangeToken_Failed() {
        //TODO: Finish me
    }

    /**
     * Ensures our PlaidService will handle failed retrieval of balance attempts gracefully
     */
    @Test
    public void testRetrieveBalance_Failed() {
        //TODO: Finish me
    }

    /**
     * Ensures our PlaidService will handle failed generation of Link Token gracefully
     */
    @Test
    public void testGenerateLinkToken_Failed() {
        //TODO: Finsih me
    }
}
