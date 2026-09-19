package com.bavis.budgetapp.service;


import com.bavis.budgetapp.dto.response.PlaidTransactionSyncResponseDto;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.model.LinkToken;


/**
 * @author Kellen Bavis
 *
 * Service to house business logic regarding working with Plaid API
 */
public interface PlaidService {
    /**
     * Functionality to generate a Link Token for a particular user, so they can access PlaidLink
     *
     * @param userId
     *          - User ID corresponding to User to generate Link Token for
     * @return
     *          - Generated Link Token and Expiration
     * @throws PlaidServiceException
     *          - Thrown in the case that an error occurs while generating our Link Token
     */
    LinkToken generateLinkToken(Long userId) throws PlaidServiceException;

    /**
     * Generate a Link Token in Plaid's "update mode", which allows a User to re-authenticate an existing Item
     * (i.e. when Plaid reports ITEM_LOGIN_REQUIRED) without creating a new Item/Account
     *
     * @param userId
     *          - ID of the User re-authenticating
     * @param accessToken
     *          - access token of the existing Item that requires re-authentication
     * @return
     *          - Link Token used to launch Plaid Link in update mode
     * @throws PlaidServiceException
     *          - thrown if Plaid is unable to create the Link Token
     */
    LinkToken generateUpdateModeLinkToken(Long userId, String accessToken) throws PlaidServiceException;

    /**
     * Functionality to generate an Access Token based on specified Public Token
     *
     * @param publicToken
     *          - Plaid API public token corresponding to connected financial institution
     * @return
     *          - Access Token for subsequent requests to Plaid API
     * @throws PlaidServiceException
     *          - Thrown in the case that an error occurs while generating access token
     */
    String exchangeToken(String publicToken) throws PlaidServiceException;

    /**
     * Functionality to retrieve the balance of a particular account
     *
     * @param accountId
     *          - Account ID corresponding to Account to fetch balance for
     * @param accessToken
     *          - Access Token needed to make the request to Plaid API
     * @return
     *          - Retrieved balance corresponding to account
     * @throws PlaidServiceException
     *          - Thrown in the case that an error occurs while fetching balance 
     */
    double retrieveBalance(String accountId, String accessToken) throws PlaidServiceException;


    /**
     * Functionality to retrieve all modified, added, or removed transactions from Plaid API
     *
     * @param accessToken
     *      - Access Token pertaining to specific Connection
     * @param previousCursor
     *      - Connection ID corresponding to Account to sync transactions for
     * @return
     *      - DTO containing all added, modified, and removed Transactions, and a cursor for subsequent requests
     */
    PlaidTransactionSyncResponseDto syncTransactions(String accessToken, String previousCursor);


    /**
     * Functionality to remove an account from Plaid API
     *
     * @param accessToken
     *          - access token corresponding to account to be removed
     */
    void removeAccount(String accessToken);
}
