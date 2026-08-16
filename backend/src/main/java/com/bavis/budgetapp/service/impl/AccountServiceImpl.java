package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dto.response.AccountResponseDto;
import com.bavis.budgetapp.constants.ConnectionStatus;
import com.bavis.budgetapp.dto.request.ConnectAccountRequestDto;
import com.bavis.budgetapp.dto.request.UpdateAccountDto;
import com.bavis.budgetapp.entity.AccountVt;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.exception.AccountConnectionException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.mapper.AccountMapper;
import com.bavis.budgetapp.entity.Connection;
import com.bavis.budgetapp.service.ConnectionService;
import com.bavis.budgetapp.service.EffectivityService;
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
import com.bavis.budgetapp.constants.AccountType;

import java.time.LocalDate;
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

	@Autowired
	private EffectivityService _effectivityService;

	/**
	 * Functionality to connect a user's financial institution
	 *
	 * @param connectAccountRequestDto
	 * 			- DTO to house needed information for connecting a User to a financial institution
	 * @return
	 * 			- Relevant information regarding the connected account
	 * @throws AccountConnectionException
	 * 			- thrown in the case that an error occurs during account connection process
	 */
	@Override
	@Transactional
	public AccountResponseDto connectAccount(ConnectAccountRequestDto connectAccountRequestDto) throws AccountConnectionException {

		log.debug("Attempting To ConnectAccount via ConnectAccountRequest: [{}]", connectAccountRequestDto);

		double balance;
		String accessToken;

		try {
			accessToken = _plaidService.exchangeToken(connectAccountRequestDto.getPublicToken());
			if(!accessToken.isBlank()) {
				log.debug("Successfully retrieved access token for Connect Account Request: [{}]", connectAccountRequestDto);
			} else {
				log.error("Failed to retrieve access token via Connect Account Request : [{}]", connectAccountRequestDto);
				throw new PlaidServiceException("Unable to exchange publicToken for accessToken");
			}

			balance = _plaidService.retrieveBalance(connectAccountRequestDto.getPlaidAccountId(), accessToken);
			log.debug("Balance Retrieved From Plaid Service: [{}]", balance);

		} catch (PlaidServiceException exception){
			log.debug("A PlaidServiceException was thrown by PlaidService while attempting to connect account: [{}]", exception.getMessage());
			throw new AccountConnectionException(exception.getMessage());
		}

		Account newAccount = Account.builder()
				.accountId(connectAccountRequestDto.getPlaidAccountId())
				.user(_userService.getCurrentAuthUser())
				.validTimes(new ArrayList<>())
				.build();

		AccountVt initialVt = AccountVt.builder()
				.account(newAccount)
				.accountName(connectAccountRequestDto.getAccountName())
				.accountType(connectAccountRequestDto.getAccountType())
				.balance(balance)
				.build();

		_effectivityService.applyVtUpdate(newAccount.getValidTimes(), initialVt, LocalDate.now());

		Connection newConnection = Connection.builder()
				.connectionStatus(ConnectionStatus.CONNECTED)
				.accessToken(accessToken)
				.institutionName(connectAccountRequestDto.getAccountName())
				.lastSyncTime(LocalDateTime.now())
				.build();

		newAccount.setConnection(newConnection);

		Connection savedConnection = _connectionService.create(newConnection);
		Account savedAccount = _accountRepository.save(newAccount);

		log.debug("Saved Connection: [{}]", savedConnection.toString());
		log.debug("Saved Account: [{}]", savedAccount.toString());

		AccountVt activeVt = _effectivityService.getActiveVt(newAccount.getValidTimes(), LocalDate.now());
		return _accountMapper.toResponseDto(newAccount, activeVt);
	}

	/**
	 * Functionality to delete a specific user Account
	 *
	 * @param accountId
	 * 			- Account ID of relevant Account to be deleted
	 */
	@Override
	public void delete(String accountId) {
		log.info("Attempting to delete account with ID {}", accountId);

		Account accountToDelete = findEntity(accountId, null);
		Connection connection = accountToDelete.getConnection();
		String accessToken = connection.getAccessToken();

		try {
			_plaidService.removeAccount(accessToken);
		} catch (PlaidServiceException e) {
			if (!e.getMessage().contains("The Item you requested cannot be found")) {
				throw e;
			}
		}

		accountToDelete.setEndDate(LocalDate.now());
		_accountRepository.save(accountToDelete);
	}

	/**
	 * Functionality to update an existing user Account
	 *
	 * @param updateAccountDto
	 * 			- DTO containing updated Account attributes
	 * @return
	 * 			- AccountResponseDto containing updated Account data
	 */
	@Override
	public AccountResponseDto update(UpdateAccountDto updateAccountDto) {
		log.info("Attempting to update Account via updateAccountDto: {}", updateAccountDto);
		Account accountToUpdate = findEntity(updateAccountDto.getAccountId(), null);

		AccountVt active = _effectivityService.getActiveVt(accountToUpdate.getValidTimes(), LocalDate.now());
		AccountType currentType = active.getAccountType();
		String currentName = active.getAccountName();
		Double currentBalance = active.getBalance();

		String newName = updateAccountDto.getAccountName() != null ? updateAccountDto.getAccountName() : currentName;
		AccountType newType = updateAccountDto.getAccountType() != null ? updateAccountDto.getAccountType() : currentType;
		Double newBalance = currentBalance;

		AccountVt updateVt = AccountVt.builder()
				.account(accountToUpdate)
				.accountName(newName)
				.accountType(newType)
				.balance(newBalance)
				.build();

		_effectivityService.applyVtUpdate(accountToUpdate.getValidTimes(), updateVt, LocalDate.now());

		Account saved = _accountRepository.save(accountToUpdate);
		AccountVt activeVt = _effectivityService.getActiveVt(saved.getValidTimes(), LocalDate.now());
		return _accountMapper.toResponseDto(saved, activeVt);
	}

	@Override
	public AccountResponseDto get(String accountId, LocalDate asOf) throws RuntimeException {
		Account account = findEntity(accountId, asOf);
		AccountVt activeVt = _effectivityService.getActiveVt(account.getValidTimes(), (asOf != null) ? asOf : LocalDate.now());
		return _accountMapper.toResponseDto(account, activeVt);
	}

	/**
	 * Functionality to fetch a specific Account as of a point-in-time date
	 *
	 * @param accountId
	 * 			- Account ID corresponding to Account to be fetched
	 * @param asOf
	 * 			- Point-in-time date
	 * @return
	 * 			- Fetched Account
	 */
	@Override
	public Account findEntity(String accountId, LocalDate asOf) throws RuntimeException {
		log.info("Attempting to read an Account entity with ID {} asOf [{}]", accountId, asOf);
		LocalDate target = (asOf != null) ? asOf : LocalDate.now();
		return _accountRepository.findByAccountIdAndAsOf(accountId, target)
				.orElseThrow(() -> new RuntimeException("Unable to locate Account with ID " + accountId));
	}

	/**
	 * Functionality to retrieve all account DTOs associated with authenticated user as of a point-in-time date
	 *
	 * @param asOf
	 * 			- Point-in-time date
	 * @return
	 * 		- all account DTOs associated with authenticated user as of date
	 */
	@Override
	public List<AccountResponseDto> getAll(LocalDate asOf) {
		log.info("Attempting to read all account DTOs associated with current authenticated user asOf [{}]", asOf);
		return findAllEntities(asOf).stream()
				.map(account -> {
					LocalDate target = (asOf != null) ? asOf : LocalDate.now();
					AccountVt activeVt = _effectivityService.getActiveVt(account.getValidTimes(), target);
					return _accountMapper.toResponseDto(account, activeVt);
				})
				.toList();
	}

	@Override
	public List<Account> findAllEntities(LocalDate asOf) {
		log.info("Attempting to read all Account entities associated with current authenticated user asOf [{}]", asOf);
		User currentAuthUser = _userService.getCurrentAuthUser();
		LocalDate target = (asOf != null) ? asOf : LocalDate.now();
		return _accountRepository.findByUserUserIdAndAsOf(currentAuthUser.getUserId(), target);
	}
}
