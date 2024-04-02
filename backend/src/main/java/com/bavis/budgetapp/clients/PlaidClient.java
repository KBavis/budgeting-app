package com.bavis.budgetapp.clients;


import com.bavis.budgetapp.request.ExchangeTokenRequest;
import com.bavis.budgetapp.request.LinkTokenRequest;
import com.bavis.budgetapp.request.RetrieveBalanceRequest;
import com.bavis.budgetapp.response.AccessTokenResponse;
import com.bavis.budgetapp.response.LinkTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "plaidClient", url = "${plaid.api.baseUrl}")
public interface PlaidClient {

    @PostMapping("/link/token/create")
    ResponseEntity<LinkTokenResponse> createLinkToken(@RequestBody LinkTokenRequest linkTokenRequest);

    @PostMapping("/item/public_token/exchange")
    ResponseEntity<AccessTokenResponse> createAccessToken(@RequestBody ExchangeTokenRequest exchangeTokenRequest);

    @PostMapping("/accounts/balance/get")
    ResponseEntity<String> retrieveAccountBalance(@RequestBody RetrieveBalanceRequest retrieveBalanceRequest);

    //@PostMapping("/transactions/sync")
    //ResponseEntity<?> syncTransactions(@RequestBody SyncTransactionsRequest syncTransactionsRequest);
}
