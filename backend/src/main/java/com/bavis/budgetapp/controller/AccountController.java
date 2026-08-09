package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.request.ConnectAccountRequestDto;
import com.bavis.budgetapp.dto.response.AccountResponseDto;
import com.bavis.budgetapp.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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
	 * @param asOf
	 * 			- Optional point-in-time date to evaluate Account state history
	 * @return
	 * 			- Fetched account corresponding to passed in AccountID
	 */
	@GetMapping("/{accountId}")
	public ResponseEntity<Account> read(@PathVariable(value = "accountId") String accountId,
										@RequestParam(name = "asOf", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
		log.info("Received request to read account with ID {} asOf [{}]", accountId, asOf);
		Account account = _accountService.read(accountId, asOf);
		return ResponseEntity.ok(account);
	}

	/**
	 * Fetch all Accounts associated with authenticated user
	 *
	 * @param asOf
	 * 			- Optional point-in-time date to evaluate Account state history
	 * @return
	 * 		- all accounts associated with auth user
	 */
	@GetMapping
	public ResponseEntity<List<AccountDto>> readAll(@RequestParam(name = "asOf", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
		log.info("Received request to read all account associated with current authenticated user with asOf [{}]", asOf);
		return ResponseEntity.ok(_accountService.readAll(asOf));
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
	 * Delete a specific Account
	 *
	 * @param accountId
	 * 			- Account ID pertaining to particular account needing to be deleted
	 */
	@DeleteMapping("/{accountId}")
	public void delete(@PathVariable(value = "accountId") String accountId) {
		log.info("Received request to delete account with ID {}", accountId);
		_accountService.delete(accountId);
	}
}
