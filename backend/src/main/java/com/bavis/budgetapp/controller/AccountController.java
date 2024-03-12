package com.bavis.budgetapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bavis.budgetapp.model.Account;
import com.bavis.budgetapp.service.AccountService;

import lombok.RequiredArgsConstructor;

/**
 * TODO: Look into necessary HTTP Status to be returned by each method
 */
@RestController
@RequestMapping("/account")
public class AccountController {
	private static final Logger LOG = LoggerFactory.getLogger(AccountController.class);
	private final AccountService _accountService;

    public AccountController(AccountService _accountService){
		this._accountService = _accountService;
    }

	@GetMapping("/{accountId}")
	public ResponseEntity<Account> read(@PathVariable(value = "accountId") Long accountId) {
		LOG.info("Received request to read account with ID {}", accountId);
		return ResponseEntity.ok(_accountService.read(accountId));
	}
	
	@PostMapping
	public ResponseEntity<Account> create(@RequestBody Account newAccount) {
		LOG.info("Received request to create new account: [{}]", newAccount);
		return ResponseEntity.ok(_accountService.create(newAccount));
	}
	
	@PutMapping("/{accountId}")
	public ResponseEntity<Account> update(@RequestBody Account newAccount, @PathVariable(value = "accountId") Long accountId) {
		LOG.info("Received request to update account with ID {} to be new Account: [{}]", accountId, newAccount);
		return ResponseEntity.ok(_accountService.update(newAccount, accountId));
	}

	@DeleteMapping("/{accountId}")
	public void delete(@PathVariable(value = "accountId") Long accountId) {
		LOG.info("Received request to delete account with ID {}", accountId);
		_accountService.delete(accountId);
	}
}
