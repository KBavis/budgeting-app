package com.bavis.budgetapp.service;


import com.bavis.budgetapp.exception.PlaidServiceException;

public interface PlaidService {
    public String generateLinkToken(Long userId) throws PlaidServiceException;

    public String exchangeToken(String publicToken) throws PlaidServiceException;

    public double retrieveBalance(String accountId, String accessToken) throws PlaidServiceException;
}
