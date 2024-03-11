package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.service.PlaidService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PlaidServiceImpl implements PlaidService{

    private final String PLAID_CLIENT_ID;
    private final String PLAID_SECRET_KEY;

    private final RestTemplate _restTemplate;

    @Value("{plaid.api.base-url}")
    private String PLAID_API_BASE_URL;

    public PlaidServiceImpl(RestTemplate _restTemplate, Environment env) {
        this._restTemplate = _restTemplate;
        this.PLAID_CLIENT_ID = env.getProperty("PLAID_CLIENT_ID");
        this.PLAID_SECRET_KEY = env.getProperty("PLAID_SECRET_KEY");
    }

    /**
     *
     * TODO: Finalize this implementation and generate link token
     *
     * @param userId
     *          - specified User ID pertaining to our application
     * @return
     *      - Link token specific to designated user used to access Plaid Link
     */
    @Override
    public String generateLinkToken(Long userId) {
        String apiUrl = PLAID_API_BASE_URL + "/link/token/create";
        return null;
    }
}
