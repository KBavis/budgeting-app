package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.AccountDto;
import com.bavis.budgetapp.dto.ConnectAccountRequestDto;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.service.AccountService;

import java.util.List;

/**
 * @author Kellen Bavis
 *
 *  Controller utilzied for working with an Account entity
 */
@RestController
@Log4j2
@RequestMapping("/account")
public class AccountController {
	private final AccountService _accountService;

    public AccountController(AccountService _accountService){
		this._accountService = _accountService;
    }

	/**
	 * Fetch a specified Account
	 *
	 * @param accountId
	 * 			- Account ID to read from our database
	 * @return
	 * 			- Fetched account corresponding to passed in AccountID
	 */
	@GetMapping("/{accountId}")
	public ResponseEntity<Account> read(@PathVariable(value = "accountId") String accountId) {
		log.info("Received request to read account with ID {}", accountId);
		return ResponseEntity.ok(_accountService.read(accountId));
	}

	/**
	 * Fetch all Accounts associated with authenticated user
	 *
	 * @return
	 * 		- all accounts associated with auth user
	 */
	@GetMapping
	public ResponseEntity<List<AccountDto>> readAll() {
		log.info("Received request to read all account associated with current authenticated user");
		return ResponseEntity.ok(_accountService.readAll());
	}

	/**
	 * Connect a user to a particular account/financial institution
	 *
	 * @param connectAccountRequestDto
	 * 			- Request to connect a users account
	 * @return
	 * 			- Account that was successfully connected
	 */
	@PostMapping
	public ResponseEntity<AccountDto> connectAccount(@Valid @RequestBody ConnectAccountRequestDto connectAccountRequestDto){
		log.info("Received request to connect new account: [{}]", connectAccountRequestDto);
		return ResponseEntity.ok( _accountService.connectAccount(connectAccountRequestDto));
	}

	/**
	 * Update an Account with new information
	 *
	 * @param newAccount
	 * 			- Account with updated information
	 * @param accountId
	 * 			- Account ID corresponding to Account needing updates
	 * @return
	 * 			- Updated Account
	 */
	@PutMapping("/{accountId}")
	public ResponseEntity<Account> update(@RequestBody Account newAccount, @PathVariable(value = "accountId") Long accountId) {
		log.info("Received request to update account with ID {} to be new Account: [{}]", accountId, newAccount);
		return ResponseEntity.ok(_accountService.update(newAccount, accountId));
	}

	/**
	 * Delete a specific Account
	 *
	 * @param accountId
	 * 			- Account ID pertaining to particular account needing to be deleted
	 */
	@DeleteMapping("/{accountId}")
	public void delete(@PathVariable(value = "accountId") Long accountId) {
		log.info("Received request to delete account with ID {}", accountId);
		_accountService.delete(accountId);
	}
}
