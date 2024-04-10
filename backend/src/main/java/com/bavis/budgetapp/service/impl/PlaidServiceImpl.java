package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.clients.PlaidClient;
import com.bavis.budgetapp.config.PlaidConfig;
import com.bavis.budgetapp.dto.PlaidUserDTO;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.request.ExchangeTokenRequest;
import com.bavis.budgetapp.request.LinkTokenRequest;
import com.bavis.budgetapp.request.RetrieveBalanceRequest;
import com.bavis.budgetapp.response.AccessTokenResponse;
import com.bavis.budgetapp.response.LinkTokenResponse;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.util.JsonUtil;
import feign.FeignException.FeignClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PlaidServiceImpl implements PlaidService{

    private static final Logger LOG = LoggerFactory.getLogger(PlaidServiceImpl.class);

    private final PlaidClient _plaidClient;
    private final PlaidConfig _plaidConfig;

    private final JsonUtil _jsonUtil;



    @Autowired
    public PlaidServiceImpl(PlaidClient _plaidClient, PlaidConfig _plaidConfig, JsonUtil _jsonUtil){
        this._plaidConfig = _plaidConfig;
        this._plaidClient = _plaidClient;
        this._jsonUtil = _jsonUtil;
    }

    /**
     * Generate a link token for a specified user
     *
     * @param userId
     *          - specified User ID pertaining to our application
     * @return
     *      - Link token specific to designated user used to access Plaid Link
     */
    @Override
    public String generateLinkToken(Long userId) throws PlaidServiceException {
        LinkTokenRequest linkTokenRequest = LinkTokenRequest.builder()
                .clientId(_plaidConfig.getClientId())
                .secretKey(_plaidConfig.getSecretKey())
                .clientName("Bavis Budget Application")
                .countryCodes(new String[]{"US"})
                .language("en")
                .user(new PlaidUserDTO(userId.toString()))
                .products(new String[]{"auth"})
                .build();

        LOG.debug("Link Token Request in `generateLinkToken()`: {}", linkTokenRequest.toString());

        ResponseEntity<LinkTokenResponse> responseEntity = _plaidClient.createLinkToken(linkTokenRequest);
        LOG.debug("Response Entity returned when generating link token: {}", responseEntity.getBody());

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            LOG.debug("Response Body From LinkToken Request: {}", responseEntity.getBody());
            LinkTokenResponse responseBody = responseEntity.getBody();
            if (responseBody != null) {
                if(responseBody.getLinkToken().isEmpty()){
                    LOG.error("Link Token returned from Plaid Client is NULL");
                    throw new PlaidServiceException("Link Token returned from Plaid Client is NULL");
                }
                return responseBody.getLinkToken();
            } else {
                LOG.error("Response Body From LinkTokenRequest is NULL.");
                throw new PlaidServiceException("Response Body from Plaid Client when Generating Link Token Is Null");
            }
        } else {
            LOG.error("Response Code From Plaid API is not successful. Status Code: {}", responseEntity.getStatusCode());
            throw new PlaidServiceException("Invalid Response Code When Generating Link Token Via PlaidClient: [" + responseEntity.getStatusCode() + "]");
        }
    }

    /**
     * Exchange a user's public token for an access token
     *
     * @param publicToken
     *          - public token retrieved from Plaid Link connection
     * @return
     *          - access token needed for subsequent requests to Plaid API
     * @throws PlaidServiceException
     *          - exception in the case of unsuccessful Plaid API response
     */
    @Override
    public String exchangeToken(String publicToken) throws PlaidServiceException{
        ExchangeTokenRequest exchangeTokenRequest = ExchangeTokenRequest.builder()
                .publicToken(publicToken)
                .clientId(_plaidConfig.getClientId())
                .secretKey(_plaidConfig.getSecretKey())
                .build();

        LOG.debug("ExchangeTokenRequest created in 'exchangeToken' in PlaidService: {}", exchangeTokenRequest);
        ResponseEntity<AccessTokenResponse> responseEntity;
        try{
            responseEntity = _plaidClient.createAccessToken(exchangeTokenRequest);
        } catch(FeignClientException e){
            String plaidClientExceptionMessage = _jsonUtil.extractErrorMessage(e);
            throw new PlaidServiceException(plaidClientExceptionMessage);
        }

        if(responseEntity.getStatusCode().is2xxSuccessful()){
            LOG.debug("Response Body From Exchange Token Request: {}", responseEntity.getBody());
            AccessTokenResponse responseBody = responseEntity.getBody();
            if(responseBody != null){
                if(responseBody.getAccessToken().isEmpty()){
                    LOG.error("Access Token returned from PlaidClient is NULL");
                    throw new PlaidServiceException("Access Token returned from Plaid Client is NULL");
                }
                return responseBody.getAccessToken();
            } else {
                LOG.error("Response Body From ExchangeTokenRequest is NULL.");
                throw new PlaidServiceException("Response Body from Plaid Client when Exchanging Public Token Is Null");
            }
        } else {
            LOG.error("Response Code From Plaid API is not successful. Status Code: {}", responseEntity.getStatusCode());
            throw new PlaidServiceException("Invalid Response Code When Exchanging Public Token Via Plaid Client: [" + responseEntity.getStatusCode() + "]");
        }
    }

    /**
     * Retrieve a balance for a particular bank account
     *
     * @param accountId
     *          - account ID to retrieve balance for
     * @param accessToken
     *          - access token needed to hit Plaid API endpoint
     * @return
     *          - balance of specified account
     * @throws PlaidServiceException
     *          - exception in the case of invalid response from Plaid API
     */
    @Override
    public double retrieveBalance(String accountId, String accessToken) throws PlaidServiceException{
        //Create RetrieveBalanceRequest
        RetrieveBalanceRequest retrieveBalanceRequest = RetrieveBalanceRequest.builder()
                .accessToken(accessToken)
                .clientId(_plaidConfig.getClientId())
                .secret(_plaidConfig.getSecretKey())
                .build();

        LOG.debug("RetrieveBalanceRequest created in 'retrieveBalance' in PlaidService: {}", retrieveBalanceRequest);

        ResponseEntity<String> responseEntity = _plaidClient.retrieveAccountBalance(retrieveBalanceRequest);
        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();

            Double balance = _jsonUtil.extractBalanceByAccountId(responseBody, accountId, "/balances/available");
            if(balance != null){
                return balance;
            }

            LOG.warn("No matching account found for account ID: {}", accountId);
            throw new PlaidServiceException("No Matching Account ID Found In Plaid Client Response When Attempting To Retrieve Balance");
        } else {
            LOG.error("Failed to retrieve balance. Status code: {}", responseEntity.getStatusCode());
            throw new PlaidServiceException("Invalid Response Code When Retrieving Balance Via Plaid Client: [" + responseEntity.getStatusCode() + "]");
        }
    }

}
