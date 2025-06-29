package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dto.AccountDto;
import com.bavis.budgetapp.constants.ConnectionStatus;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.exception.AccountConnectionException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.mapper.AccountMapper;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.dto.ConnectAccountRequestDto;
import com.bavis.budgetapp.service.ConnectionService;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.service.TransactionService;
import com.bavis.budgetapp.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.bavis.budgetapp.dao.AccountRepository;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.service.AccountService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kellen Bavis
 *
 * Implementation of our Account Service functionality
 */
@Service
@Log4j2
public class AccountServiceImpl implements AccountService{

	@Autowired
	private AccountRepository _accountRepository;

	@Autowired
	private PlaidService _plaidService;

	@Autowired
	private UserService _userService;

	@Autowired
	private AccountMapper _accountMapper;

	@Autowired
	private ConnectionService _connectionService;

	@Autowired
	@Lazy
	private TransactionService _transactionService;


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
	public void delete(String accountId) {
		log.info("Attempting to delete account with ID {}", accountId);

		//Fetch Corresponding Account
		Account accountToDelete = read(accountId);
		Connection connection = accountToDelete.getConnection();
		String accessToken = connection.getAccessToken();

		//Remove Via Plaid Service
		try{
			_plaidService.removeAccount(accessToken);
		} catch (PlaidServiceException e) {
			// only throw exception if it's not a retry deletion (aka Plaid Item is deleted, but our Account is not)
			if (!e.getMessage().contains("The Item you requested cannot be found")) {
				throw e;
			}
		}

		accountToDelete.setDeleted(true);
		_accountRepository.save(accountToDelete);
	}

	//TODO: Implement
	@Override
	public Account update(Account account, Long accountId) {
		return null;
	}

	@Override
	public Account read(String accountId) throws RuntimeException{
		log.info("Attempting to read a Account entity with the ID {}", accountId);
		return  _accountRepository.findByAccountId(accountId)
				.orElseThrow(() -> new RuntimeException("Unable to locate Account with ID " + accountId));
	}

	@Override
	public List<AccountDto> readAll() {
		log.info("Attempting to read all accounts associated with current authenticated user");
		User currentAuthUser = _userService.getCurrentAuthUser();
        return _accountRepository.findByUserUserId(currentAuthUser.getUserId()).stream()
				.filter(account -> !account.isDeleted())
				.map(_accountMapper::toDTO)
				.toList();
	}

}
