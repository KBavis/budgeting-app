package com.bavis.budgetapp.service;

import com.bavis.budgetapp.dto.AccountDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.dto.ConnectAccountRequestDto;

public interface AccountService {
	
	AccountDto connectAccount(ConnectAccountRequestDto connectAccountRequestDto);
	void delete(Long accountId);
	Account update(Account account, Long accountId);
	Account read(Long accountId);
}
