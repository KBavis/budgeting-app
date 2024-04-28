package com.bavis.budgetapp.clients;


import com.bavis.budgetapp.request.ExchangeTokenRequest;
import com.bavis.budgetapp.request.LinkTokenRequest;
import com.bavis.budgetapp.request.RetrieveBalanceRequest;
import com.bavis.budgetapp.response.AccessTokenResponse;
import com.bavis.budgetapp.response.LinkTokenResponse;
import feign.FeignException.FeignClientException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "plaidClient", url = "https://development.plaid.com")
public interface PlaidClient {

    @PostMapping("/link/token/create")
    ResponseEntity<LinkTokenResponse> createLinkToken(@RequestBody LinkTokenRequest linkTokenRequest) throws FeignClientException;

    @PostMapping("/item/public_token/exchange")
    ResponseEntity<AccessTokenResponse> createAccessToken(@RequestBody ExchangeTokenRequest exchangeTokenRequest) throws FeignClientException;

    @PostMapping("/accounts/balance/get")
    ResponseEntity<String> retrieveAccountBalance(@RequestBody RetrieveBalanceRequest retrieveBalanceRequest) throws FeignClientException;

    //@PostMapping("/transactions/sync")
    //ResponseEntity<?> syncTransactions(@RequestBody SyncTransactionsRequest syncTransactionsRequest);
}
