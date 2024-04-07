package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.AccountDTO;
import com.bavis.budgetapp.model.Account;
import com.bavis.budgetapp.request.ConnectAccountRequest;

public interface AccountService {
	
	AccountDTO connectAccount(ConnectAccountRequest connectAccountRequest) throws Exception;
	void delete(Long accountId);
	Account update(Account account, Long accountId);
	Account read(Long accountId);
}
