package com.bavis.budgetapp.services;

import com.bavis.budgetapp.clients.PlaidClient;
import com.bavis.budgetapp.config.PlaidConfig;
import com.bavis.budgetapp.exception.PlaidServiceException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        LinkTokenResponse linkTokenResponse = LinkTokenResponse.builder()
                .linkToken(expectedLinkToken)
                .build();
        ResponseEntity<LinkTokenResponse> responseEntity = new ResponseEntity<>(linkTokenResponse, HttpStatus.OK);


        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createLinkToken(any(LinkTokenRequest.class))).thenReturn(responseEntity);

        //Act
        String actualLinkToken = plaidService.generateLinkToken(userId);

        //Assert
        assertEquals(expectedLinkToken, actualLinkToken);

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).createLinkToken(any(LinkTokenRequest.class));
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

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).retrieveAccountBalance(any(RetrieveBalanceRequest.class));
        verify(jsonUtil, times(1)).extractBalanceByAccountId(responseBody, accountId, "/balances/available");
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

        // Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).createAccessToken(any(ExchangeTokenRequest.class));
    }

    /**
     * Ensures our PlaidService will handle null response bodies when attempting to exchange public tokens with Plaid API gracefully
     */
    @Test
    public void testExchangeToken_NullResponseBody_Failed() {
        //Arrange
        String publicToken = "public-token";
        ResponseEntity<AccessTokenResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createAccessToken(any(ExchangeTokenRequest.class))).thenReturn(responseEntity);

        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            plaidService.exchangeToken(publicToken);
        });
        assertEquals(exception.getMessage(), "Response Body from Plaid Client when Exchanging Public Token Is Null");

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).createAccessToken(any(ExchangeTokenRequest.class));
    }

    /**
     * Ensures our PlaidService will handle failed exchange token attempts gracefully
     */
    @Test
    public void testExchangeToken_InvalidResponseCode_Failed() {
        //Arrange
        String publicToken = "public-token";
        String expectedAccessToken = "access-token";
        AccessTokenResponse accessTokenResponse = AccessTokenResponse.builder()
                .accessToken(expectedAccessToken)
                .build();
        ResponseEntity<AccessTokenResponse> responseEntity = new ResponseEntity<>(accessTokenResponse, HttpStatus.BAD_REQUEST);

        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createAccessToken(any(ExchangeTokenRequest.class))).thenReturn(responseEntity);

        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            plaidService.exchangeToken(publicToken);
        });
        assertEquals(exception.getMessage(), "Invalid Response Code When Exchanging Public Token Via Plaid Client: [" + responseEntity.getStatusCode() + "]");

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).createAccessToken(any(ExchangeTokenRequest.class));
    }

    /**
     * Ensures our PlaidService will handle invalid response code when retrieving balance from PlaidAPI gracefully
     */
    @Test
    public void testRetrieveBalance_InvalidResponseCode_Failed() {
        //Arrange
        String accountId = "account-id";
        String accessToken = "access-token";
        double expectedBalance = 1000.0;
        String responseBody = _jsonUtil.toJson(_testHelper.createBalanceResponse(accountId, expectedBalance));
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);

        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.retrieveAccountBalance(any(RetrieveBalanceRequest.class))).thenReturn(responseEntity);


        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            double actualBalance = plaidService.retrieveBalance(accountId, accessToken);
        });
        assertEquals(exception.getMessage(), "Invalid Response Code When Retrieving Balance Via Plaid Client: [" + responseEntity.getStatusCode() + "]");


        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).retrieveAccountBalance(any(RetrieveBalanceRequest.class));
        verify(jsonUtil, times(0)).extractBalanceByAccountId(responseBody, accountId, "/balances/available");
    }


    /**
     * Ensures our PlaidService will handle invalid response code when retrieving balance from PlaidAPI gracefully
     */
    @Test
    public void testRetrieveBalance_AccountIdNotFound_Failed() {
        //Arrange
        String validAccountId = "valid-account-id";
        String invalidAccountId = "invalid-account-id";
        String accessToken = "access-token";
        double expectedBalance = 1000.0;
        String responseBody = _jsonUtil.toJson(_testHelper.createBalanceResponse(invalidAccountId, expectedBalance)); //create response with invalid account id
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK); //response with invalid account id

        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.retrieveAccountBalance(any(RetrieveBalanceRequest.class))).thenReturn(responseEntity);
        when(jsonUtil.extractBalanceByAccountId(responseBody, validAccountId, "/balances/available")).thenReturn(null);


        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            double actualBalance = plaidService.retrieveBalance(validAccountId, accessToken);
        });
        assertEquals(exception.getMessage(), "No Matching Account ID Found In Plaid Client Response When Attempting To Retrieve Balance");


        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).retrieveAccountBalance(any(RetrieveBalanceRequest.class));
        verify(jsonUtil, times(1)).extractBalanceByAccountId(responseBody, validAccountId, "/balances/available");
    }

    /**
     * Ensures our PlaidService will handle null response body returned by Plaid API gracefully when attempting to generate link token
     */
    @Test
    public void testGenerateLinkToken_NullResponseBody_Failed() {
        //Arrange
        Long userId = 123L;
        ResponseEntity<LinkTokenResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);


        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createLinkToken(any(LinkTokenRequest.class))).thenReturn(responseEntity);

        //Act & Assert
         PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
             plaidService.generateLinkToken(userId);
         });
        assertEquals(exception.getMessage(), "Response Body from Plaid Client when Generating Link Token Is Null");

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).createLinkToken(any(LinkTokenRequest.class));
    }

    /**
     * Ensures our PlaidService will handle invalid response codes returned by Plaid API gracefully when attempting to generate link token
     */
    @Test
    public void testGenerateLinkToken_InvalidResponseCode_Failed() {
        //Arrange
        Long userId = 123L;
        String linkToken = "link-token";
        LinkTokenResponse linkTokenResponse = LinkTokenResponse.builder()
                .linkToken(linkToken)
                .build();
        ResponseEntity<LinkTokenResponse> responseEntity = new ResponseEntity<>(linkTokenResponse, HttpStatus.BAD_REQUEST);


        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createLinkToken(any(LinkTokenRequest.class))).thenReturn(responseEntity);

        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            plaidService.generateLinkToken(userId);
        });
        assertEquals(exception.getMessage(), "Invalid Response Code When Generating Link Token Via PlaidClient: [" + responseEntity.getStatusCode() + "]");

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).createLinkToken(any(LinkTokenRequest.class));
    }
}
