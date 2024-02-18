package com.bavis.budgetapp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.AccountRepository;
import com.bavis.budgetapp.model.Account;
import com.bavis.budgetapp.service.AccountService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AccountServiceImpl implements AccountService{
	private static Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);
	private final AccountRepository repository;

	@Override
	public Account create(Account account) {
		/**
		 * TODO: Creating An Account
		 * 			1a) Establish A Connection with An Account (TODO: Do this if able to work with finacial insitituions API's)
		 * 			1b) Rather Than Connect To Account, We Can Simply Have User Add A File When Connecting An Account
		 * 						-- 1) Click Add Account
		 * 						-- 2) Click Add Transaction Data 
		 * 						-- 3) Click Selected Format Of The Data Your Spending
		 * 						-- 4) Enter The Names Of Columns Containing Relevant Information (Amount, Name, Category(? maybe have this automatically create category), Transaction Date)
		 * 						-- 5) Select Specified File, And Click Fetch Transactions
		 * 			2) Transactions Currently Associated With Account Should Be Fetched 	
		 * 			3) Categorize Transactions Currently Associated With Account 
		 * 				- If Categories Are Present, Users Should Be Given Option to Assign Cateogry To Transaction (Or, Have Our Application Suggest Category For Them)
		 * 				- If No Categories Are Present, The Category For Each Transaction Should Be Desingated to The Pre-Created Category, "Misc."
		 * 
		 * 			
		 */
		return null;
	}

	@Override
	public void delete(Long accountId) {
		
	}

	@Override
	public Account update(Account account, Long accountId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Account read(Long accountId) {
		// TODO Auto-generated method stub
		return null;
	}

}
