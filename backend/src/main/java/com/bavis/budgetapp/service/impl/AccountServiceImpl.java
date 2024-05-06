package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dto.AccountDto;
import com.bavis.budgetapp.constants.ConnectionStatus;
import com.bavis.budgetapp.exception.AccountConnectionException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.mapper.AccountMapper;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.dto.ConnectAccountRequestDto;
import com.bavis.budgetapp.service.ConnectionService;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.AccountRepository;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.service.AccountService;

import java.time.LocalDateTime;

/**
 * @author Kellen Bavis
 *
 * Implementation of our Account Service functionality
 */
@Service
@Log4j2
public class AccountServiceImpl implements AccountService{
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
	public AccountDto connectAccount(ConnectAccountRequestDto connectAccountRequestDto) throws AccountConnectionException{

		log.debug("Attempting To ConnectAccount via ConnectAccountRequest: [{}]", connectAccountRequestDto);

		double balance;
		String accessToken;

		try{
			//Exchange Public Token With Access Token & Log
			accessToken = _plaidService.exchangeToken(connectAccountRequestDto.getPublicToken());
			if(!accessToken.isBlank()) {
				log.debug("Successfully retrieved access token for Connect Account Request: [{}]", connectAccountRequestDto);
			} else {
				log.error("Failed to retrieve access token via Connect Account Request : [{}]", connectAccountRequestDto);
			}

			//Retrieve Balance Pertaining To Account
			balance = _plaidService.retrieveBalance(connectAccountRequestDto.getPlaidAccountId(), accessToken);
			log.debug("Balance Retrieved From Plaid Service: [{}]", balance);

		} catch (PlaidServiceException exception){
			log.debug("A PlaidServiceException was thrown by PlaidService while attempting to connect account: [{}]", exception.getMessage());
			throw new AccountConnectionException(exception.getMessage());
		}


		// Initialize Account to be persisted
		Account newAccount = Account.builder()
				.accountId(connectAccountRequestDto.getPlaidAccountId())
				.accountName(connectAccountRequestDto.getAccountName())
				.balance(balance)
				.accountType(connectAccountRequestDto.getAccountType())
				.user(_userService.getCurrentAuthUser())
				.build();


		// Initialize Connection to be persisted
		Connection newConnection = Connection.builder()
				.connectionStatus(ConnectionStatus.CONNECTED)
				.accessToken(accessToken)
				.institutionName(connectAccountRequestDto.getAccountName())
				.lastSyncTime(LocalDateTime.now())
				.build();

		//Set Connection in the Account
		newAccount.setConnection(newConnection);

		//Save Entities
		Connection savedConnection = _connectionService.create(newConnection);
		Account savedAccount = _accountRepository.save(newAccount);

		//Log Saved Connection/Account
		log.debug("Saved Connection: [{}]", savedConnection.toString());
		log.debug("Saved Account: [{}]", savedAccount.toString());

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
