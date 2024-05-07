package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.clients.PlaidClient;
import com.bavis.budgetapp.config.PlaidConfig;
import com.bavis.budgetapp.dto.PlaidUserDto;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.dto.ExchangeTokenRequestDto;
import com.bavis.budgetapp.dto.LinkTokenRequestDto;
import com.bavis.budgetapp.dto.RetrieveBalanceRequestDto;
import com.bavis.budgetapp.dto.AccessTokenResponseDto;
import com.bavis.budgetapp.dto.LinkTokenResponseDto;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.util.JsonUtil;
import feign.FeignException.FeignClientException;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author Kellen Bavis
 *
 * Implementation of our Plaid Service functionality
 */
@Service
@Log4j2
public class PlaidServiceImpl implements PlaidService{
    private final PlaidClient _plaidClient;
    private final PlaidConfig _plaidConfig;

    private final JsonUtil _jsonUtil;



    @Autowired
    public PlaidServiceImpl(PlaidClient _plaidClient, PlaidConfig _plaidConfig, JsonUtil _jsonUtil){
        this._plaidConfig = _plaidConfig;
        this._plaidClient = _plaidClient;
        this._jsonUtil = _jsonUtil;
    }

    @Override
    public String generateLinkToken(Long userId) throws PlaidServiceException {
        LinkTokenRequestDto linkTokenRequestDto = LinkTokenRequestDto.builder()
                .clientId(_plaidConfig.getClientId())
                .secretKey(_plaidConfig.getSecretKey())
                .clientName("Bavis Budget Application")
                .countryCodes(new String[]{"US"})
                .language("en")
                .user(new PlaidUserDto(userId.toString()))
                .products(new String[]{"transactions"})
                .build();

        log.info("Fetching Link Token for User with ID {} with following LinkTokenRequest: [{}]", userId, linkTokenRequestDto);

        //Validate & Handle FeignClientException
        ResponseEntity<LinkTokenResponseDto> responseEntity;
        try{
            responseEntity = _plaidClient.createLinkToken(linkTokenRequestDto);
        } catch (FeignClientException e){
            log.error("An error occurred while fetching Link Token from Plaid API: [{}]", e.getMessage());
            String plaidClientExceptionMessage = _jsonUtil.extractErrorMessage(e);
            throw new PlaidServiceException(plaidClientExceptionMessage);
        } catch (Exception ex){
            log.error(ex.getLocalizedMessage());
            throw new PlaidServiceException(ex.getMessage());
        }

        //Validate Successful Response from PlaidClient
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            LinkTokenResponseDto responseBody = responseEntity.getBody();
            if (responseBody != null) {
                if(responseBody.getLinkToken().isBlank()){
                    log.error("Link Token fetched from Plaid API was null/blank");
                    throw new PlaidServiceException("Link Token returned from Plaid Client is NULL");
                }
                return responseBody.getLinkToken();
            } else {
                log.error("Response from Plaid API when attempting to retrieve Link Token was null");
                throw new PlaidServiceException("Response Body from Plaid Client when Generating Link Token Is Null");
            }
        } else {
            log.error("Response Code From Plaid API when extracting Link Token was not successful. Status Code: {}", responseEntity.getStatusCode());
            throw new PlaidServiceException("Invalid Response Code When Generating Link Token Via PlaidClient: [" + responseEntity.getStatusCode() + "]");
        }
    }

    @Override
    public String exchangeToken(String publicToken) throws PlaidServiceException{
        ExchangeTokenRequestDto exchangeTokenRequestDto = ExchangeTokenRequestDto.builder()
                .publicToken(publicToken)
                .clientId(_plaidConfig.getClientId())
                .secretKey(_plaidConfig.getSecretKey())
                .build();

        log.info("Exchanging Plaid API Public Token [{}] for Access Token via the following ExchangeTokenRequest: [{}]", publicToken, exchangeTokenRequestDto);

        //Validate & Handle Feign Client Exceptions
        ResponseEntity<AccessTokenResponseDto> responseEntity;
        try{
            responseEntity = _plaidClient.createAccessToken(exchangeTokenRequestDto);
        } catch(FeignClientException e){
            log.error("An error occurred while exchanging Public Token for Access Token via Plaid API: [{}]", e.getMessage());
            String plaidClientExceptionMessage = _jsonUtil.extractErrorMessage(e);
            throw new PlaidServiceException(plaidClientExceptionMessage);
        } catch(Exception e){
            log.error(e.getLocalizedMessage());
            throw new PlaidServiceException(e.getMessage());
        }

        //Validate Successful Response from PlaidClient
        if(responseEntity.getStatusCode().is2xxSuccessful()){
            AccessTokenResponseDto responseBody = responseEntity.getBody();
            if(responseBody != null){
                if(responseBody.getAccessToken().isBlank()){
                    log.error("AccessToken retrieved from Plaid API is null/blank");
                    throw new PlaidServiceException("Access Token returned from Plaid Client is NULL");
                }
                return responseBody.getAccessToken();
            } else {
                log.error("Response from Plaid API while exchanging Public Token for Access Token is Null");
                throw new PlaidServiceException("Response Body from Plaid Client when Exchanging Public Token Is Null");
            }
        } else {
            log.error("Response Code From Plaid API while Exchanging Public Token is not successful. Status Code: {}", responseEntity.getStatusCode());
            throw new PlaidServiceException("Invalid Response Code When Exchanging Public Token Via Plaid Client: [" + responseEntity.getStatusCode() + "]");
        }
    }

    @Override
    public double retrieveBalance(String accountId, String accessToken) throws PlaidServiceException{
        //Create RetrieveBalanceRequest
        RetrieveBalanceRequestDto retrieveBalanceRequestDto = RetrieveBalanceRequestDto.builder()
                .accessToken(accessToken)
                .clientId(_plaidConfig.getClientId())
                .secret(_plaidConfig.getSecretKey())
                .build();

        log.info("Retrieving Account Balance via Plaid API for Account with ID {} and the following RetrieveBalanceRequest : [{}]", accountId, retrieveBalanceRequestDto);

        //Catch any FeignClientExceptions & Handle Properly
        ResponseEntity<String> responseEntity;
        try{
            responseEntity = _plaidClient.retrieveAccountBalance(retrieveBalanceRequestDto);
        } catch (FeignClientException e){
            log.error("An error occurred while retrieving account balance via Plaid API: [{}]", e.getMessage());
            String plaidClientExceptionMessage = _jsonUtil.extractErrorMessage(e);
            throw new PlaidServiceException(plaidClientExceptionMessage);
        } catch(Exception e){
            log.error(e.getLocalizedMessage());
            throw new PlaidServiceException(e.getMessage());
        }


        //Validate Successful Response From PlaidClient
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();

            Double balance = _jsonUtil.extractBalanceByAccountId(responseBody, accountId, "/balances/available");
            if(balance != null){
                return balance;
            }

            log.error("No matching account found for account ID {} while attempting to extract balance from Plaid API response", accountId);
            throw new PlaidServiceException("No Matching Account ID Found In Plaid Client Response When Attempting To Retrieve Balance");
        } else {
            log.error("Unsuccessful response from Plaid API while attempting to retrieve account balance. Status code: {}", responseEntity.getStatusCode());
            throw new PlaidServiceException("Invalid Response Code When Retrieving Balance Via Plaid Client: [" + responseEntity.getStatusCode() + "]");
        }
    }

}
