package com.bavis.budgetapp.service;


public interface PlaidService {
    public String generateLinkToken(Long userId);

    public void exchangeToken(String publicToken);
}
