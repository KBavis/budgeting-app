package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.clients.PlaidClient;
import com.bavis.budgetapp.dto.AccountDTO;
import com.bavis.budgetapp.enumeration.ConnectionStatus;
import com.bavis.budgetapp.mapper.AccountMapper;
import com.bavis.budgetapp.model.Connection;
import com.bavis.budgetapp.request.ConnectAccountRequest;
import com.bavis.budgetapp.service.ConnectionService;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.AccountRepository;
import com.bavis.budgetapp.model.Account;
import com.bavis.budgetapp.service.AccountService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
public class AccountServiceImpl implements AccountService{


	private static final Logger LOG = LoggerFactory.getLogger(AccountServiceImpl.class);

	private final AccountRepository _accountRepository;

	private final PlaidService _plaidService;

	private final UserService _userService;

	private final AccountMapper _accountMapper;

	private final ConnectionService _connectionService;

	public AccountServiceImpl(AccountRepository _accountRepository,
	 						  PlaidService _plaidService,
							  AccountMapper _accountMapper,
							  ConnectionService _connectionService,
							  UserService _userService) {
		this._accountRepository = _accountRepository;
		this._plaidService = _plaidService;
		this._accountMapper = _accountMapper;
		this._userService = _userService;
		this._connectionService = _connectionService;
	}

	@Override
	@Transactional
	public AccountDTO connectAccount(ConnectAccountRequest connectAccountRequest) {

		LOG.debug("Attempting To ConnectAccount via ConnectAccountRequest: [{}]", connectAccountRequest);

		//Exchange Public Token With Access Token
		String accessToken = _plaidService.exchangeToken(connectAccountRequest.getPublicToken());
		LOG.debug("Access Token Retrieved From Plaid Service: [{}]", accessToken);

		//Retrieve Balance Pertaining To Account
		double balance = _plaidService.retrieveBalance(connectAccountRequest.getPlaidAccountId(), accessToken);
		LOG.debug("Balance Retrieved From Plaid Service: [{}]", balance);


		// Initialize Account to be persisted
		Account newAccount = Account.builder()
				.accountId(connectAccountRequest.getPlaidAccountId())
				.accountName(connectAccountRequest.getAccountName())
				.balance(balance)
				.accountType(connectAccountRequest.getAccountType())
				.user(_userService.getCurrentAuthUser())
				.build();


		// Initialize Connection to be persisted
		Connection newConnection = Connection.builder()
				.connectionStatus(ConnectionStatus.CONNECTED)
				.accessToken(accessToken)
				.institutionName(connectAccountRequest.getAccountName())
				.lastSyncTime(LocalDateTime.now())
				.build();

		//Set Connection in the Account
		newAccount.setConnection(newConnection);

		//Save Entities
		Connection savedConnection = _connectionService.create(newConnection);
		Account savedAccount = _accountRepository.save(newAccount);

		//Log Saved Connection/Account
		LOG.debug("Saved Connection: [{}]", savedConnection.toString());
		LOG.debug("Saved Account: [{}]", savedAccount.toString());

		//Map Account to AccountDTO
        return _accountMapper.toDTO(newAccount);
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
