package com.bavis.budgetapp.clients;


import com.bavis.budgetapp.config.PlaidConfig;
import com.bavis.budgetapp.dto.*;
import feign.FeignException.FeignClientException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Kellen Bavis
 *
 * FeignClient utilized for simplicity of HTTP requests to PlaidAPI
 */
@FeignClient(name = "plaidClient", url = "${plaid.api.baseUrl}", configuration = PlaidConfig.class)
public interface PlaidClient {

    @PostMapping("/link/token/create")
    ResponseEntity<LinkTokenResponseDto> createLinkToken(@RequestBody LinkTokenRequestDto linkTokenRequestDto) throws FeignClientException;

    @PostMapping("/item/public_token/exchange")
    ResponseEntity<AccessTokenResponseDto> createAccessToken(@RequestBody ExchangeTokenRequestDto exchangeTokenRequestDto) throws FeignClientException;

    @PostMapping("/accounts/balance/get")
    ResponseEntity<String> retrieveAccountBalance(@RequestBody RetrieveBalanceRequestDto retrieveBalanceRequestDto) throws FeignClientException;

    @PostMapping("/transactions/sync")
    ResponseEntity<PlaidTransactionSyncResponseDto> syncTransactions(@RequestBody PlaidTransactionSyncRequestDto plaidTransactionSyncRequestDto) throws FeignClientException;

    @PostMapping("/item/remove")
    ResponseEntity<Void> removeAccount(@RequestBody AccountRemovalRequestDto accountRemovalRequestDto) throws FeignClientException;
}
