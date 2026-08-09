package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.response.AccountResponseDto;
import com.bavis.budgetapp.dto.request.PlaidAccountDto;
import com.bavis.budgetapp.dto.request.ConnectAccountRequestDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.exception.AccountConnectionException;

import com.bavis.budgetapp.dto.request.UpdateAccountDto;

import java.time.LocalDate;
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
	AccountResponseDto connectAccount(ConnectAccountRequestDto connectAccountRequestDto) throws AccountConnectionException;

	/**
	 * Functionality to delete a specific user Account
	 *
	 * @param accountId
	 * 			- Account ID of relevant Account to be deleted
	 */
	void delete(String accountId);

	/**
	 * Functionality to fetch a specific Account as of a point-in-time date mapped to response DTO
	 *
	 * @param accountId
	 * 			- Account ID corresponding to Account to be fetched
	 * @param asOf
	 * 			- Point-in-time date (defaults to today if null)
	 * @return
	 * 			- AccountResponseDto
	 */
	AccountResponseDto get(String accountId, LocalDate asOf);

	/**
	 * Functionality to fetch a specific Account as of a point-in-time date
	 *
	 * @param accountId
	 * 			- Account ID corresponding to Account to be fetched
	 * @param asOf
	 * 			- Point-in-time date (defaults to today if null)
	 * @return
	 * 			- Fetched Account
	 */
	Account findEntity(String accountId, LocalDate asOf);

	/**
	 * Functionality to retrieve all account DTOs associated with authenticated user as of a point-in-time date
	 *
	 * @param asOf
	 * 			- Point-in-time date (defaults to today if null)
	 * @return
	 * 		- all account DTOs associated with authenticated user as of date
	 */
	List<AccountResponseDto> getAll(LocalDate asOf);

	/**
	 * Functionality to retrieve all Account entities associated with authenticated user as of a point-in-time date
	 *
	 * @param asOf
	 * 			- Point-in-time date (defaults to today if null)
	 * @return
	 * 		- all Account entities associated with authenticated user as of date
	 */
	List<Account> findAllEntities(LocalDate asOf);

	/**
	 * Functionality to update an existing user Account
	 *
	 * @param updateAccountDto
	 * 			- DTO containing updated Account attributes
	 * @return
	 * 			- AccountResponseDto containing updated Account data
	 */
	AccountResponseDto update(UpdateAccountDto updateAccountDto);
}
