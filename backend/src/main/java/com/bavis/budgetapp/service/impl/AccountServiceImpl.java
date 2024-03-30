package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.clients.PlaidClient;
import com.bavis.budgetapp.dto.AccountDTO;
import com.bavis.budgetapp.request.ConnectAccountRequest;
import com.bavis.budgetapp.service.ConnectionService;
import com.bavis.budgetapp.service.PlaidService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.AccountRepository;
import com.bavis.budgetapp.model.Account;
import com.bavis.budgetapp.service.AccountService;

import lombok.RequiredArgsConstructor;

@Service
public class AccountServiceImpl implements AccountService{
	private static Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);

	private final ConnectionService _connectionService;

	private final AccountRepository _accountRepository;

	private final PlaidService _plaidService;

	public AccountServiceImpl(AccountRepository _accountRepository,
							  ConnectionService _connectionService,
	 						  PlaidService _plaidService) {
		this._accountRepository = _accountRepository;
		this._connectionService = _connectionService;
		this._plaidService = _plaidService;
	}

	@Override
	public AccountDTO connectAccount(ConnectAccountRequest connectAccountRequest) {
		/**
		 * Steps In Methodology:
		 * 		1) Use Plaid Service Implementation to Fetch Access Token
		 * 		2) Use Plaid Service To Fetch Account Balance
		 * 		2) Generate Account Entity and Persist In Database
		 * 		3) Generate Connection Entity and Persist in Database Using Connection Service
		 * 		4
		 */


		//Exchange Public Token With Access Token
		String accessToken = _plaidService.exchangeToken(connectAccountRequest.getPublicToken());

		//Retrieve Balance Pertaining To Account
		double balance = _plaidService.retrieveBalance(connectAccountRequest.getPlaidAccountId(), accessToken);
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
