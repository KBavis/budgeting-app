package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.config.PlaidConfig;
import com.bavis.budgetapp.dto.PlaidUserDTO;
import com.bavis.budgetapp.request.LinkTokenRequest;
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
    private final RestTemplate _restTemplate;
    private final JsonUtil _jsonUtil;
    private final String PLAID_API_BASE_URL;

    private final PlaidConfig _plaidConfig;


    public PlaidServiceImpl(@Value("${plaid.api.base-url}") String _plaidApiBaseUrl,
                            RestTemplate _restTemplate,
                            JsonUtil _jsonUtil,
                            PlaidConfig _plaidConfig) {
        this._restTemplate = _restTemplate;
        this.PLAID_API_BASE_URL = _plaidApiBaseUrl;
        this._jsonUtil = _jsonUtil;
        this._plaidConfig = _plaidConfig;
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
        String apiUrl = PLAID_API_BASE_URL + "/link/token/create";

        LOG.debug("Client Id: {}, Secret Key: {}",_plaidConfig.getClientId(), _plaidConfig.getSecretKey());



        LinkTokenRequest linkTokenRequest = LinkTokenRequest.builder()
                .clientId(_plaidConfig.getClientId())
                .secretKey(_plaidConfig.getSecretKey())
                .clientName("Bavis Budget Application")
                .countryCodes(new String[]{"US"})
                .language("en")
                .user(new PlaidUserDTO(userId.toString()))
                .products(new String[]{"auth"})
                .build();

        LOG.debug("Link Token in `generateLinkToken()`: {}", linkTokenRequest.toString());

        HttpEntity<LinkTokenRequest> requestEntity = new HttpEntity<>(linkTokenRequest);
        ResponseEntity<String> responseEntity = _restTemplate.postForEntity(apiUrl, requestEntity, String.class);

        //Validate Response
        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            String responseBody = responseEntity.getBody();
            if(responseBody != null){
                //Extract Link Token Attribute from Response
                return _jsonUtil.extractAttribute(responseBody, "link_token");
            } else {
                LOG.error("Response Body From Plaid API [URL: {}] is NULL.", apiUrl);
            }
        } else {
            LOG.error("Response Code From Plaid API [URL: {}] is not succesful.", apiUrl);
        }
        return null;
    }
}
