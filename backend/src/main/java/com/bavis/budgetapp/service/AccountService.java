package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.AccountDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.dto.ConnectAccountRequestDto;
import com.bavis.budgetapp.exception.AccountConnectionException;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Service to house the functionality of working with Account Entities
 */
public interface AccountService {

	/**
	 * Functionality to connect a user's financial institution
	 *
	 * @param connectAccountRequestDto
	 * 			- DTO to house needed information for connecting a User to a financial instituion
	 * @return
	 * 			- Relevant information regarding the connected account
	 * @throws AccountConnectionException
	 * 			- thrown in the case that an error occurs during account connection process
	 */
	AccountDto connectAccount(ConnectAccountRequestDto connectAccountRequestDto) throws AccountConnectionException;

	/**
	 * Functionality to delete a specific user Account
	 *
	 * @param accountId
	 * 			- Account ID of relevant Account to be deleted
	 */
	void delete(Long accountId);

	/**
	 * Functionality to update an existing user Account
	 *
	 * @param account
	 * 			- Account with updated attributes
	 * @param accountId
	 * 			- Account ID corresponding to Account needing updates
	 * @return
	 * 			- Updated Account
	 */
	Account update(Account account, Long accountId);

	/**
	 *  Functionality to fetch a specific Account
	 *
	 * @param accountId
	 * 			- Account ID corresponding to Account to be fetched
	 * @return
	 * 			- Fetched Account
	 */
	Account read(String accountId);

	/**
	 * Functionality to retrieve all accounts associated with authenticated user
	 *
	 * @return
	 * 		- all accounts associated with authenticated user
	 */
	List<AccountDto> readAll();
}
