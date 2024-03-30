package com.bavis.budgetapp.service;


import com.bavis.budgetapp.dto.BalanceDTO;

public interface PlaidService {
    public String generateLinkToken(Long userId);

    public String exchangeToken(String publicToken);

    public double retrieveBalance(String accountId, String accessToken);
}
