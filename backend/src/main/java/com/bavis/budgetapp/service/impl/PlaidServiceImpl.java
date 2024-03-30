package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.clients.PlaidClient;
import com.bavis.budgetapp.config.PlaidConfig;
import com.bavis.budgetapp.dto.PlaidUserDTO;
import com.bavis.budgetapp.request.ExchangeTokenRequest;
import com.bavis.budgetapp.request.LinkTokenRequest;
import com.bavis.budgetapp.request.RetrieveBalanceRequest;
import com.bavis.budgetapp.response.AccessTokenResponse;
import com.bavis.budgetapp.response.LinkTokenResponse;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.util.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PlaidServiceImpl implements PlaidService{

    private static final Logger LOG = LoggerFactory.getLogger(PlaidServiceImpl.class);

    private final PlaidClient _plaidClient;
    private final PlaidConfig _plaidConfig;

    private final JsonUtil _jsonUtil;

    public PlaidServiceImpl(PlaidClient _plaidClient, PlaidConfig _plaidConfig, JsonUtil _jsonUtil){
        this._plaidConfig = _plaidConfig;
        this._plaidClient = _plaidClient;
        this._jsonUtil = _jsonUtil;
    }

    /**
     *
     *
     * @param userId
     *          - specified User ID pertaining to our application
     * @return
     *      - Link token specific to designated user used to access Plaid Link
     */
    @Override
    public String generateLinkToken(Long userId) {
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

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            LOG.debug("Response Body From LinkToken Request: {}", responseEntity.getBody());
            LinkTokenResponse responseBody = responseEntity.getBody();
            if (responseBody != null) {
                return responseBody.getLinkToken();
            } else {
                //TODO: Throw Exception Indicating Unsuccessful Attempt In Generating Link Token
                LOG.error("Response Body From LinkTokenRequest is NULL.");
            }
        } else {
            //TODO: Throw Exception Indicating Unsuccessful Attempt In Generating Link Token
            LOG.error("Response Code From Plaid API is not successful. Status Code: {}", responseEntity.getStatusCode());
        }
        return null;
    }

    @Override
    public String exchangeToken(String publicToken) {
        ExchangeTokenRequest exchangeTokenRequest = ExchangeTokenRequest.builder()
                .publicToken(publicToken)
                .clientId(_plaidConfig.getClientId())
                .secretKey(_plaidConfig.getSecretKey())
                .build();

        LOG.debug("ExchangeTokenRequest created in 'exchangeToken' in PlaidService: {}", exchangeTokenRequest);

        ResponseEntity<AccessTokenResponse> responseEntity = _plaidClient.createAccessToken(exchangeTokenRequest);

        if(responseEntity.getStatusCode().is2xxSuccessful()){
            LOG.debug("Response Body From Exchange Token Request: {}", responseEntity.getBody());
            AccessTokenResponse responseBody = responseEntity.getBody();
            if(responseBody != null){
                return responseBody.getAccessToken();
            } else {
                //TODO: Throw Exception Indicating Unsuccessful Attempt In Exchanging Public Token For Access Token
                LOG.error("Response Body From ExchangeTokenRequest is NULL!");
            }
        } else {
            //TODO: Throw Exception Indicating Unsuccessful Attempt In Exchanging Public Token For Access Token
            LOG.error("Response Code From Plaid API is not successful. Status Code: {}", responseEntity.getStatusCode());
        }
        return null;
    }

    @Override
    public double retrieveBalance(String accountId, String accessToken){
        //Create RetrieveBalanceRequest
        RetrieveBalanceRequest retrieveBalanceRequest = RetrieveBalanceRequest.builder()
                .accessToken(accessToken)
                .clientId(_plaidConfig.getClientId())
                .secret(_plaidConfig.getSecretKey())
                .build();

        LOG.debug("RetrieveBalanceRequest created in 'retrieveBalance' in PlaidService: {}", retrieveBalanceRequest);

        ResponseEntity<String> responseEntity = _plaidClient.retrieveAccountBalance(retrieveBalanceRequest);
        if(responseEntity.getStatusCode().is2xxSuccessful()){
            String responseBody = responseEntity.getBody();
            String currentBalance = extractCurrentBalance(responseBody, accountId);
            if(currentBalance != null){
                return Double.parseDouble(currentBalance);
            }
        }
        return 0.0;
    }

    //TODO: Finalize this implementation
    private String extractCurrentBalance(String responseBody, String accountId){
//        try{
//            String accountsJson = _jsonUtil.extractAttribute(responseBody, "acccounts");
//            if(accountsJson != null){
//                JsonNode accountsNode =
//            }
//        }
        return "100.00";
    }
}
