package com.bavis.budgetapp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
	private static Logger LOG = LoggerFactory.getLogger(AccountController.class);
	private final AccountService accountService;
	
	@GetMapping("/{accountId}")
	public Account read(@PathVariable(value = "accountId") Long accountId) {
		LOG.info("Recieved request to read account with ID {}", accountId);
		return accountService.read(accountId);
	}
	
	@PostMapping
	public Account create(@RequestBody Account newAccount) {
		LOG.info("Recieved request to create new account: [{}]", newAccount);
		return accountService.create(newAccount);
	}
	
	@PutMapping("/{accountId}")
	public Account update(@RequestBody Account newAccount, @PathVariable(value = "accountId") Long accountId) {
		LOG.info("Recieved request to update account with ID {} to be new Account: [{}]", accountId, newAccount);
		return accountService.update(newAccount, accountId);
	}

	@DeleteMapping("/{accountId}")
	public void delete(@PathVariable(value = "accountId") Long accountId) {
		LOG.info("Recieved request to delete account with ID {}", accountId);
		accountService.delete(accountId);
	}
}
