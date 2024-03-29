package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.clients.PlaidClient;
import com.bavis.budgetapp.config.PlaidConfig;
import com.bavis.budgetapp.dto.PlaidUserDTO;
import com.bavis.budgetapp.request.LinkTokenRequest;
import com.bavis.budgetapp.response.LinkTokenResponse;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PlaidServiceImpl implements PlaidService{

    private static final Logger LOG = LoggerFactory.getLogger(PlaidServiceImpl.class);

    private final PlaidClient _plaidClient;
    private final PlaidConfig _plaidConfig;



    public PlaidServiceImpl(PlaidClient _plaidClient, PlaidConfig _plaidConfig){
        this._plaidConfig = _plaidConfig;
        this._plaidClient = _plaidClient;
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
                LOG.error("Response Body From Plaid API is NULL.");
            }
        } else {
            LOG.error("Response Code From Plaid API is not successful. Status Code: {}", responseEntity.getStatusCode());
        }
        return null;
    }
}
