package com.bavis.budgetapp.services;

import com.bavis.budgetapp.clients.PlaidClient;
import com.bavis.budgetapp.config.PlaidConfig;
import com.bavis.budgetapp.dto.*;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.TestHelper;
import com.bavis.budgetapp.model.LinkToken;
import com.bavis.budgetapp.service.PlaidService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private JsonUtil mockJsonUtil;

    @InjectMocks
    private PlaidServiceImpl plaidService;

    private JsonUtil _jsonUtil;

    @BeforeEach
    public void setup() {

        ObjectMapper mapper = new ObjectMapper();
        _jsonUtil = new JsonUtil(mapper);
    }

    @Test
    void testRemoveAccount_Success() {
        //Arrange
        String accessToken = "accessToken";
        ResponseEntity<Void> responseEntity = ResponseEntity.ok().build();

        //Mock
        when(plaidClient.removeAccount(any(AccountRemovalRequestDto.class))).thenReturn(responseEntity);

        //Act
        plaidService.removeAccount(accessToken);

        //Verify
        verify(plaidClient, times(1)).removeAccount(any(AccountRemovalRequestDto.class));
    }

    @Test
    void testRemoveAccount_ThrowsFeignClientException() {
        //Arrange
        String accessToken = "accessToken";
        Request request = Request.create(
                Request.HttpMethod.POST,
                "/item/remove",
                Collections.emptyMap(),
                "request_body".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8,
                null
        );
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(400, "Bad Request", request, null, null);

        //Mock
        when(plaidClient.removeAccount(any(AccountRemovalRequestDto.class))).thenThrow(feignClientException);
        when(mockJsonUtil.extractErrorMessage(feignClientException)).thenReturn("Bad Request");

        //Act
        PlaidServiceException plaidServiceException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.removeAccount(accessToken);
        });
        assertEquals("PlaidServiceException: [Bad Request]", plaidServiceException.getMessage());
    }

    @Test
    void testRemoveAccount_ThrowsException() {
        //Arrange
        String accessToken = "accessToken";
        RuntimeException exception = new RuntimeException("Error Message");


        //Mock
        when(plaidClient.removeAccount(any(AccountRemovalRequestDto.class))).thenThrow(exception);

        //Act
        PlaidServiceException plaidServiceException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.removeAccount(accessToken);
        });
        assertEquals("PlaidServiceException: [Error Message]", plaidServiceException.getMessage());
    }

    @Test
    void testRemoveAccount_InvalidStatus() {
        //Arrange
        String accessToken = "accessToken";
        ResponseEntity<Void> responseEntity = ResponseEntity.badRequest().build();

        //Mock
        when(plaidClient.removeAccount(any(AccountRemovalRequestDto.class))).thenReturn(responseEntity);

        //Act
        PlaidServiceException plaidServiceException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.removeAccount(accessToken);
        });
        assertEquals("PlaidServiceException: [Invalid Response Code When Removing Account via Plaid Client]", plaidServiceException.getMessage());
    }


    /**
     * Validate PlaidService ability to generate link token successfully
     */
    @Test
    public void testGenerateLinkToken_Success() {
        //Arrange
        Long userId = 123L;
        String expectedLinkToken = "link-token";
        LocalDateTime expectedExpiration = LocalDateTime.now();
        LinkTokenResponseDto linkTokenResponseDto = LinkTokenResponseDto.builder()
                .linkToken(expectedLinkToken)
                .expiration(expectedExpiration)
                .build();
        ResponseEntity<LinkTokenResponseDto> responseEntity = new ResponseEntity<>(linkTokenResponseDto, HttpStatus.OK);


        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.createLinkToken(any(LinkTokenRequestDto.class))).thenReturn(responseEntity);

        //Act
        LinkToken actualLinkToken = plaidService.generateLinkToken(userId);

        //Assert
        assertEquals(expectedLinkToken, actualLinkToken.getToken());
        assertEquals(expectedExpiration, actualLinkToken.getExpiration());

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
        String responseBody = _jsonUtil.toJson(TestHelper.createBalanceResponse(accountId, expectedBalance));
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK);

        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.retrieveAccountBalance(any(RetrieveBalanceRequestDto.class))).thenReturn(responseEntity);
        when(mockJsonUtil.extractBalanceByAccountId(responseBody, accountId, "/balances/available"))
                .thenReturn(_jsonUtil.extractBalanceByAccountId(responseBody, accountId, "/balances/available"));


        //Act
        double actualBalance = plaidService.retrieveBalance(accountId, accessToken);

        //Assert
        assertEquals(expectedBalance, actualBalance);

        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).retrieveAccountBalance(any(RetrieveBalanceRequestDto.class));
        verify(mockJsonUtil, times(1)).extractBalanceByAccountId(responseBody, accountId, "/balances/available");
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
        String responseBody = _jsonUtil.toJson(TestHelper.createBalanceResponse(accountId, expectedBalance));
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
        verify(mockJsonUtil, times(0)).extractBalanceByAccountId(responseBody, accountId, "/balances/available");
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
        String responseBody = _jsonUtil.toJson(TestHelper.createBalanceResponse(invalidAccountId, expectedBalance)); //create response with invalid account id
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, HttpStatus.OK); //response with invalid account id

        //Mock
        when(plaidConfig.getClientId()).thenReturn("client-id");
        when(plaidConfig.getSecretKey()).thenReturn("secret-key");
        when(plaidClient.retrieveAccountBalance(any(RetrieveBalanceRequestDto.class))).thenReturn(responseEntity);
        when(mockJsonUtil.extractBalanceByAccountId(responseBody, validAccountId, "/balances/available")).thenReturn(null);


        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            double actualBalance = plaidService.retrieveBalance(validAccountId, accessToken);
        });
        assertEquals(exception.getMessage(), "PlaidServiceException: [No Matching Account ID Found In Plaid Client Response When Attempting To Retrieve Balance]");


        //Verify
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
        verify(plaidClient, times(1)).retrieveAccountBalance(any(RetrieveBalanceRequestDto.class));
        verify(mockJsonUtil, times(1)).extractBalanceByAccountId(responseBody, validAccountId, "/balances/available");
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

        when(mockJsonUtil.extractErrorMessage(any(FeignException.FeignClientException.class)))
                .thenReturn(errorMessage);

        // Act & Assert
        PlaidServiceException thrownException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.exchangeToken("fake-public-token");
        });
        assertNotNull(thrownException);
        assertEquals(expectedExceptionMessage, thrownException.getMessage());

        //Verify
        verify(plaidClient, times(1)).createAccessToken(any(ExchangeTokenRequestDto.class));
        verify(mockJsonUtil, times(1)).extractErrorMessage(any(FeignException.FeignClientException.class));
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
        when(mockJsonUtil.extractErrorMessage(any(FeignException.FeignClientException.class)))
                .thenReturn(errorMessage);

        // Act & Assert
        PlaidServiceException thrownException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.generateLinkToken(userId);
        });
        assertNotNull(thrownException);
        assertEquals(expectedExceptionMessage, thrownException.getMessage());

        //Verify
        verify(plaidClient, times(1)).createLinkToken(any(LinkTokenRequestDto.class));
        verify(mockJsonUtil, times(1)).extractErrorMessage(any(FeignException.FeignClientException.class));
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
        when(mockJsonUtil.extractErrorMessage(any(FeignException.FeignClientException.class)))
                .thenReturn(errorMessage);

        // Act & Assert
        PlaidServiceException thrownException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.retrieveBalance(accountId, accessToken);
        });
        assertNotNull(thrownException);
        assertEquals(expectedExceptionMessage, thrownException.getMessage());

        //Verify
        verify(plaidClient, times(1)).retrieveAccountBalance(any(RetrieveBalanceRequestDto.class));
        verify(mockJsonUtil, times(1)).extractErrorMessage(any(FeignException.FeignClientException.class));
    }

    @Test
    void testSyncTransactions_Success() {
        //Arrange
        String clientId = "client-id";
        String secretKey = "secret-key";
        String nextCursor = "next-cursor";
        String previousCursor = "previous-cursor";
        LocalDateTime date = LocalDateTime.now();
        String accountId = "account-id";
        String accessToken = "access-token";

        PlaidTransactionDto.CounterpartyDto counterpartyDto = PlaidTransactionDto.CounterpartyDto.builder()
                .logo_url("logo_url")
                .name("Chase")
                .build();

        PlaidTransactionDto.PersonalFinanceCategoryDto personalFinanceCategoryDto = PlaidTransactionDto.PersonalFinanceCategoryDto.builder()
                .primary("PRIMARY")
                .detailed("DETAILED")
                .confidence_level("HIGH")
                .build();

        PlaidTransactionDto plaidTransactionDtoOne = PlaidTransactionDto.builder()
                .transaction_id("12345")
                .counterparties(List.of(counterpartyDto))
                .personal_finance_category(personalFinanceCategoryDto)
                .amount(1000.0)
                .datetime(date)
                .account_id(accountId)
                .build();

        PlaidTransactionDto plaidTransactionDtoTwo = PlaidTransactionDto.builder()
                .transaction_id("6789")
                .counterparties(List.of(counterpartyDto))
                .personal_finance_category(personalFinanceCategoryDto)
                .amount(2000.0)
                .datetime(date)
                .account_id(accountId)
                .build();


        PlaidTransactionDto plaidTransactionDtoThree = PlaidTransactionDto.builder()
                .transaction_id("6789")
                .counterparties(List.of(counterpartyDto))
                .personal_finance_category(personalFinanceCategoryDto)
                .amount(3000.0)
                .datetime(date)
                .account_id(accountId)
                .build();

        List<PlaidTransactionDto> addedTransactions = List.of(plaidTransactionDtoOne);
        List<PlaidTransactionDto> removedTransactions = List.of(plaidTransactionDtoTwo);
        List<PlaidTransactionDto> modifiedTransactions = List.of(plaidTransactionDtoThree);

        PlaidTransactionSyncResponseDto syncResponseDto = PlaidTransactionSyncResponseDto.builder()
                .added(addedTransactions)
                .modified(modifiedTransactions)
                .removed(removedTransactions)
                .next_cursor(nextCursor)
                .build();

        ResponseEntity<PlaidTransactionSyncResponseDto> responseEntity = new ResponseEntity<>(syncResponseDto, HttpStatus.OK);



        //Mock
        when(plaidConfig.getClientId()).thenReturn(clientId);
        when(plaidConfig.getSecretKey()).thenReturn(secretKey);
        when(plaidClient.syncTransactions(any(PlaidTransactionSyncRequestDto.class))).thenReturn(responseEntity);

        //Act
        PlaidTransactionSyncResponseDto actualSyncResponse = plaidService.syncTransactions(accessToken, previousCursor);

        //Assert
        assertNotNull(actualSyncResponse);
        assertEquals(addedTransactions, actualSyncResponse.getAdded());
        assertEquals(modifiedTransactions, actualSyncResponse.getModified());
        assertEquals(removedTransactions, actualSyncResponse.getRemoved());
        assertEquals(nextCursor, actualSyncResponse.getNext_cursor());

        //Verify
        verify(plaidClient, times(1)).syncTransactions(any(PlaidTransactionSyncRequestDto.class));
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
    }

    @Test
    void testSyncTransactions_FeignClientException_Failure() {
        // Arrange
        String errorMessage = "invalid argument at position[0].";
        String expectedExceptionMessage = "PlaidServiceException: [" + errorMessage + "]";
        String previousCursor = "previous-cursor";
        String accessToken = "access-token";


        Request request = Request.create(
                Request.HttpMethod.POST,
                "/transactions/sync",
                Collections.emptyMap(),
                "request_body".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8,
                null
        );
        FeignException.FeignClientException feignClientException = new FeignException.FeignClientException(
                400, "Bad Request", request, null, null);

        //Mock
        when(plaidClient.syncTransactions(any(PlaidTransactionSyncRequestDto.class)))
                .thenThrow(feignClientException);
        when(mockJsonUtil.extractErrorMessage(any(FeignException.FeignClientException.class)))
                .thenReturn(errorMessage);

        // Act & Assert
        PlaidServiceException thrownException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.syncTransactions(accessToken, previousCursor);
        });
        assertNotNull(thrownException);
        assertEquals(expectedExceptionMessage, thrownException.getMessage());

        //Verify
        verify(plaidClient, times(1)).syncTransactions(any(PlaidTransactionSyncRequestDto.class));
        verify(mockJsonUtil, times(1)).extractErrorMessage(any(FeignException.FeignClientException.class));
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
    }

    @Test
    void testSyncTransactions_Exception_Failure() {
        // Arrange
        String errorMessage = "NullPointerException at position[0].";
        String expectedExceptionMessage = "PlaidServiceException: [" + errorMessage + "]";
        String previousCursor = "previous-cursor";
        String accessToken = "access-token";

        //Mock
        when(plaidClient.syncTransactions(any(PlaidTransactionSyncRequestDto.class)))
                .thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        PlaidServiceException thrownException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.syncTransactions(accessToken, previousCursor);
        });
        assertNotNull(thrownException);
        assertEquals(expectedExceptionMessage, thrownException.getMessage());

        //Verify
        verify(plaidClient, times(1)).syncTransactions(any(PlaidTransactionSyncRequestDto.class));
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();

    }

    @Test
    void testSyncTransactions_InvalidResponseCode_Failure() {
        // Arrange
        String errorMessage = "Invalid Response Code When Retrieving Balance Via Plaid Client: [400 BAD_REQUEST]";
        String expectedExceptionMessage = "PlaidServiceException: [" + errorMessage + "]";
        String previousCursor = "previous-cursor";
        String accessToken = "access-token";
        String nextCursor = "next-cursor";
        PlaidTransactionSyncResponseDto syncResponseDto = PlaidTransactionSyncResponseDto.builder()
                .added(new ArrayList<>())
                .modified(new ArrayList<>())
                .removed(new ArrayList<>())
                .next_cursor(nextCursor)
                .build();


        ResponseEntity<PlaidTransactionSyncResponseDto> responseEntity = new ResponseEntity<>(syncResponseDto, HttpStatus.BAD_REQUEST);

        //Mock
        when(plaidClient.syncTransactions(any(PlaidTransactionSyncRequestDto.class)))
                .thenReturn(responseEntity);

        // Act & Assert
        PlaidServiceException thrownException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.syncTransactions(accessToken, previousCursor);
        });
        assertNotNull(thrownException);
        assertEquals(expectedExceptionMessage, thrownException.getMessage());

        //Verify
        verify(plaidClient, times(1)).syncTransactions(any(PlaidTransactionSyncRequestDto.class));
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
    }

    @Test
    void testSyncTransactions_NullResponseBody_Failure() {
        // Arrange
        String errorMessage = "Response Body when Syncing Transactions is Null";
        String expectedExceptionMessage = "PlaidServiceException: [" + errorMessage + "]";
        String previousCursor = "previous-cursor";
        String accessToken = "access-token";
        ResponseEntity<PlaidTransactionSyncResponseDto> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);

        //Mock
        when(plaidClient.syncTransactions(any(PlaidTransactionSyncRequestDto.class)))
                .thenReturn(responseEntity);

        // Act & Assert
        PlaidServiceException thrownException = assertThrows(PlaidServiceException.class, () -> {
            plaidService.syncTransactions(accessToken, previousCursor);
        });
        assertNotNull(thrownException);
        assertEquals(expectedExceptionMessage, thrownException.getMessage());

        //Verify
        verify(plaidClient, times(1)).syncTransactions(any(PlaidTransactionSyncRequestDto.class));
        verify(plaidConfig, times(1)).getClientId();
        verify(plaidConfig, times(1)).getSecretKey();
    }
}
