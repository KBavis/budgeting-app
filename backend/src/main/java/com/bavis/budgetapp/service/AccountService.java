package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.AccountDTO;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.request.ConnectAccountRequest;

public interface AccountService {
	
	AccountDTO connectAccount(ConnectAccountRequest connectAccountRequest);
	void delete(Long accountId);
	Account update(Account account, Long accountId);
	Account read(Long accountId);
}
