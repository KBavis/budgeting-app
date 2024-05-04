package com.bavis.budgetapp.services;

import com.bavis.budgetapp.clients.PlaidClient;
import com.bavis.budgetapp.config.PlaidConfig;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.helper.TestHelper;
import com.bavis.budgetapp.dto.ExchangeTokenRequestDto;
import com.bavis.budgetapp.dto.LinkTokenRequestDto;
import com.bavis.budgetapp.dto.RetrieveBalanceRequestDto;
import com.bavis.budgetapp.dto.AccessTokenResponseDto;
import com.bavis.budgetapp.dto.LinkTokenResponseDto;
import com.bavis.budgetapp.service.impl.PlaidServiceImpl;
import com.bavis.budgetapp.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
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
        LinkTokenResponseDto linkTokenResponseDto = LinkTokenResponseDto.builder()
                .linkToken(expectedLinkToken)
                .build();
        ResponseEntity<LinkTokenResponseDto> responseEntity = new ResponseEntity<>(linkTokenResponseDto, HttpStatus.OK);


        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createLinkToken(any(LinkTokenRequestDto.class))).thenReturn(responseEntity);

        //Act
        String actualLinkToken = plaidService.generateLinkToken(userId);

        //Assert
        assertEquals(expectedLinkToken, actualLinkToken);

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).createLinkToken(any(LinkTokenRequestDto.class));
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
        when(plaidClient.retrieveAccountBalance(any(RetrieveBalanceRequestDto.class))).thenReturn(responseEntity);
        when(jsonUtil.extractBalanceByAccountId(responseBody, accountId, "/balances/available"))
                .thenReturn(_jsonUtil.extractBalanceByAccountId(responseBody, accountId, "/balances/available"));


        //Act
        double actualBalance = plaidService.retrieveBalance(accountId, accessToken);

        //Assert
        assertEquals(expectedBalance, actualBalance);

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).retrieveAccountBalance(any(RetrieveBalanceRequestDto.class));
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
        AccessTokenResponseDto accessTokenResponseDto = AccessTokenResponseDto.builder()
                .accessToken(expectedAccessToken)
                .build();
        ResponseEntity<AccessTokenResponseDto> responseEntity = new ResponseEntity<>(accessTokenResponseDto, HttpStatus.OK);


        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createAccessToken(any(ExchangeTokenRequestDto.class))).thenReturn(responseEntity);

        //Act
        String actualAccessToken = plaidService.exchangeToken(publicToken);

        //Assert
        assertEquals(expectedAccessToken, actualAccessToken);

        // Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).createAccessToken(any(ExchangeTokenRequestDto.class));
    }

    /**
     * Ensures our PlaidService will handle null response bodies when attempting to exchange public tokens with Plaid API gracefully
     */
    @Test
    public void testExchangeToken_NullResponseBody_Failed() {
        //Arrange
        String publicToken = "public-token";
        ResponseEntity<AccessTokenResponseDto> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createAccessToken(any(ExchangeTokenRequestDto.class))).thenReturn(responseEntity);

        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            plaidService.exchangeToken(publicToken);
        });
        assertEquals(exception.getMessage(), "PlaidServiceException: [Response Body from Plaid Client when Exchanging Public Token Is Null]");

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).createAccessToken(any(ExchangeTokenRequestDto.class));
    }

    /**
     * Ensures our PlaidService will handle failed exchange token attempts gracefully
     */
    @Test
    public void testExchangeToken_InvalidResponseCode_Failed() {
        //Arrange
        String publicToken = "public-token";
        String expectedAccessToken = "access-token";
        AccessTokenResponseDto accessTokenResponseDto = AccessTokenResponseDto.builder()
                .accessToken(expectedAccessToken)
                .build();
        ResponseEntity<AccessTokenResponseDto> responseEntity = new ResponseEntity<>(accessTokenResponseDto, HttpStatus.BAD_REQUEST);

        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createAccessToken(any(ExchangeTokenRequestDto.class))).thenReturn(responseEntity);

        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            plaidService.exchangeToken(publicToken);
        });
        assertEquals(exception.getMessage(), "PlaidServiceException: [Invalid Response Code When Exchanging Public Token Via Plaid Client: [" + responseEntity.getStatusCode() + "]]");

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).createAccessToken(any(ExchangeTokenRequestDto.class));
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
        when(plaidClient.retrieveAccountBalance(any(RetrieveBalanceRequestDto.class))).thenReturn(responseEntity);


        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            double actualBalance = plaidService.retrieveBalance(accountId, accessToken);
        });
        assertEquals(exception.getMessage(), "PlaidServiceException: [Invalid Response Code When Retrieving Balance Via Plaid Client: [" + responseEntity.getStatusCode() + "]]");


        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).retrieveAccountBalance(any(RetrieveBalanceRequestDto.class));
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
        when(plaidClient.retrieveAccountBalance(any(RetrieveBalanceRequestDto.class))).thenReturn(responseEntity);
        when(jsonUtil.extractBalanceByAccountId(responseBody, validAccountId, "/balances/available")).thenReturn(null);


        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            double actualBalance = plaidService.retrieveBalance(validAccountId, accessToken);
        });
        assertEquals(exception.getMessage(), "PlaidServiceException: [No Matching Account ID Found In Plaid Client Response When Attempting To Retrieve Balance]");


        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).retrieveAccountBalance(any(RetrieveBalanceRequestDto.class));
        verify(jsonUtil, times(1)).extractBalanceByAccountId(responseBody, validAccountId, "/balances/available");
    }

    /**
     * Ensures our PlaidService will handle null response body returned by Plaid API gracefully when attempting to generate link token
     */
    @Test
    public void testGenerateLinkToken_NullResponseBody_Failed() {
        //Arrange
        Long userId = 123L;
        ResponseEntity<LinkTokenResponseDto> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);


        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createLinkToken(any(LinkTokenRequestDto.class))).thenReturn(responseEntity);

        //Act & Assert
         PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
             plaidService.generateLinkToken(userId);
         });
        assertEquals(exception.getMessage(), "PlaidServiceException: [Response Body from Plaid Client when Generating Link Token Is Null]");

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).createLinkToken(any(LinkTokenRequestDto.class));
    }

    /**
     * Ensures our PlaidService will handle invalid response codes returned by Plaid API gracefully when attempting to generate link token
     */
    @Test
    public void testGenerateLinkToken_InvalidResponseCode_Failed() {
        //Arrange
        Long userId = 123L;
        String linkToken = "link-token";
        LinkTokenResponseDto linkTokenResponseDto = LinkTokenResponseDto.builder()
                .linkToken(linkToken)
                .build();
        ResponseEntity<LinkTokenResponseDto> responseEntity = new ResponseEntity<>(linkTokenResponseDto, HttpStatus.BAD_REQUEST);


        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createLinkToken(any(LinkTokenRequestDto.class))).thenReturn(responseEntity);

        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            plaidService.generateLinkToken(userId);
        });
        assertEquals(exception.getMessage(), "PlaidServiceException: [Invalid Response Code When Generating Link Token Via PlaidClient: [" + responseEntity.getStatusCode() + "]]");

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).createLinkToken(any(LinkTokenRequestDto.class));
    }

    /**
     * Validates that our PlaidService properly handles FeignClientExceptions when Exchanging Public Tokens
     */
    @Test
    public void testExchangeToken_FeignClientException_Failed() {
        // Arrange
        String errorMessage = "provided public token is expired. Public tokens expire 30 minutes after creation at which point they can no longer be exchanged";
        String expectedExceptionMessage = "PlaidServiceException: [" + errorMessage + "]";

        Request request = Request.create(
                Request.HttpMethod.POST,
                "/item/public_token/exchange",
                Collections.emptyMap(),
                "request_body".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8,
                null
        );

        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                400, "Bad Request", request, null, null);

        when(plaidClient.createAccessToken(any(ExchangeTokenRequestDto.class)))
                .thenThrow(feignClientException);

        when(jsonUtil.extractErrorMessage(any(FeignException.FeignClientException.class)))
                .thenReturn(errorMessage);

        // Act & Assert
        PlaidServiceException thrownException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.exchangeToken("fake-public-token");
        });
        assertNotNull(thrownException);
        assertEquals(expectedExceptionMessage, thrownException.getMessage());

        //Verify
        verify(plaidClient, times(1)).createAccessToken(any(ExchangeTokenRequestDto.class));
        verify(jsonUtil, times(1)).extractErrorMessage(any(FeignException.FeignClientException.class));
    }

    /**
     * Validates that our PlaidService properly handles FeignClientExceptions when Creating Link Token
     */
    @Test
    public void testCreateLinkToken_FeignClientException_Failed() {
        // Arrange
        String errorMessage = "invalid user passed in as argument.";
        String expectedExceptionMessage = "PlaidServiceException: [" + errorMessage + "]";
        Long userId = 10L;

        Request request = Request.create(
                Request.HttpMethod.POST,
                "/link/token/create",
                Collections.emptyMap(),
                "request_body".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8,
                null
        );
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                400, "Bad Request", request, null, null);

        //Mock
        when(plaidClient.createLinkToken(any(LinkTokenRequestDto.class)))
                .thenThrow(feignClientException);
        when(jsonUtil.extractErrorMessage(any(FeignException.FeignClientException.class)))
                .thenReturn(errorMessage);

        // Act & Assert
        PlaidServiceException thrownException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.generateLinkToken(userId);
        });
        assertNotNull(thrownException);
        assertEquals(expectedExceptionMessage, thrownException.getMessage());

        //Verify
        verify(plaidClient, times(1)).createLinkToken(any(LinkTokenRequestDto.class));
        verify(jsonUtil, times(1)).extractErrorMessage(any(FeignException.FeignClientException.class));
    }


    /**
     * Validates that our PlaidService properly handles FeignClientExceptions when Retrieving Balance
     */
    @Test
    public void testRetrieveBalance_FeignClientException_Failed() {
        // Arrange
        String errorMessage = "invalid account id passed as argument.";
        String expectedExceptionMessage = "PlaidServiceException: [" + errorMessage + "]";
        String accountId = "account-id";
        String accessToken = "access-token";

        Request request = Request.create(
                Request.HttpMethod.POST,
                "/link/token/create",
                Collections.emptyMap(),
                "request_body".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8,
                null
        );
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                400, "Bad Request", request, null, null);

        //Mock
        when(plaidClient.retrieveAccountBalance(any(RetrieveBalanceRequestDto.class)))
                .thenThrow(feignClientException);
        when(jsonUtil.extractErrorMessage(any(FeignException.FeignClientException.class)))
                .thenReturn(errorMessage);

        // Act & Assert
        PlaidServiceException thrownException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.retrieveBalance(accountId, accessToken);
        });
        assertNotNull(thrownException);
        assertEquals(expectedExceptionMessage, thrownException.getMessage());

        //Verify
        verify(plaidClient, times(1)).retrieveAccountBalance(any(RetrieveBalanceRequestDto.class));
        verify(jsonUtil, times(1)).extractErrorMessage(any(FeignException.FeignClientException.class));
    }
}
