package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.request.ConnectAccountRequestDto;
import com.bavis.budgetapp.dto.request.UpdateAccountDto;
import com.bavis.budgetapp.dto.response.AccountResponseDto;
import com.bavis.budgetapp.model.LinkToken;
import com.bavis.budgetapp.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Controller utilized for working with Account entities
 */
@RestController
@Log4j2
@RequestMapping("/account")
public class AccountController {
	private final AccountService _accountService;

	public AccountController(AccountService _accountService) {
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
	 * 			- Fetched AccountResponseDto corresponding to passed in AccountID
	 */
	@GetMapping("/{accountId}")
	public ResponseEntity<AccountResponseDto> read(@PathVariable(value = "accountId") String accountId,
												   @RequestParam(name = "asOf", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
		log.info("Received request to read account with ID {} asOf [{}]", accountId, asOf);
		return ResponseEntity.ok(_accountService.get(accountId, asOf));
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
	public ResponseEntity<List<AccountResponseDto>> readAll(@RequestParam(name = "asOf", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
		log.info("Received request to read all account associated with current authenticated user with asOf [{}]", asOf);
		return ResponseEntity.ok(_accountService.getAll(asOf));
	}

	/**
	 * Connect a user to a particular account/financial institution
	 *
	 * @param connectAccountRequestDto
	 * 			- Request to connect a users account
	 * @return
	 * 			- AccountResponseDto that was successfully connected
	 */
	@PostMapping
	public ResponseEntity<AccountResponseDto> connectAccount(@Valid @RequestBody ConnectAccountRequestDto connectAccountRequestDto){
		log.info("Received request to connect new account with Plaid account ID [{}]", connectAccountRequestDto.getPlaidAccountId());
		return ResponseEntity.ok(_accountService.connectAccount(connectAccountRequestDto));
	}

	/**
	 * Update an existing Account
	 *
	 * @param updateAccountDto
	 * 			- Request body containing updated Account attributes
	 * @return
	 * 			- AccountResponseDto of updated Account
	 */
	@PutMapping
	public ResponseEntity<AccountResponseDto> update(@Valid @RequestBody UpdateAccountDto updateAccountDto) {
		log.info("Received request to update account: [{}]", updateAccountDto);
		return ResponseEntity.ok(_accountService.update(updateAccountDto));
	}

	@PutMapping("/{accountId}")
	public ResponseEntity<AccountResponseDto> updateWithId(@PathVariable(value = "accountId") String accountId,
														   @Valid @RequestBody UpdateAccountDto updateAccountDto) {
		updateAccountDto.setAccountId(accountId);
		log.info("Received request to update account with ID {}: [{}]", accountId, updateAccountDto);
		return ResponseEntity.ok(_accountService.update(updateAccountDto));
	}

	/**
	 * Delete a specific Account
	 *
	 * @param accountId
	 * 			- Account ID pertaining to particular account needing to be deleted
	 */
	/**
	 * Generate a Plaid Link Token (update mode) so the User can log in to their financial institution again for an
	 * Account that Plaid reports requires re-authentication
	 *
	 * @param accountId
	 * 			- Account to re-authenticate
	 * @return
	 * 			- Link Token used to launch Plaid Link in update mode
	 */
	@PostMapping("/{accountId}/reauth-link-token")
	public ResponseEntity<LinkToken> generateReauthenticationLinkToken(@PathVariable(value = "accountId") String accountId) {
		log.info("Received request to generate a re-authentication Link Token for account with ID {}", accountId);
		return ResponseEntity.ok(_accountService.generateReauthenticationLinkToken(accountId));
	}

	/**
	 * Record that the User successfully re-authenticated an Account via Plaid Link (update mode); clears the Account's
	 * "needs login" state. Does not sync; the User syncs manually.
	 *
	 * @param accountId
	 * 			- Account that was re-authenticated
	 * @return
	 * 			- the Account, reflecting its now healthy connection
	 */
	@PostMapping("/{accountId}/reauth-complete")
	public ResponseEntity<AccountResponseDto> completeReauthentication(@PathVariable(value = "accountId") String accountId) {
		log.info("Received request to complete re-authentication for account with ID {}", accountId);
		return ResponseEntity.ok(_accountService.completeReauthentication(accountId));
	}

	@DeleteMapping("/{accountId}")
	public void delete(@PathVariable(value = "accountId") String accountId) {
		log.info("Received request to delete account with ID {}", accountId);
		_accountService.delete(accountId);
	}
}
