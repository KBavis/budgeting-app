package com.bavis.budgetapp.service;

import com.bavis.budgetapp.model.Account;

public interface AccountService {
	
	Account create(Account account);
	void delete(Long accountId);
	Account update(Account account, Long accountId);
	Account read(Long accountId);
}
