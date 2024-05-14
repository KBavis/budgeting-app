package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.TransactionSyncRequestValidAccounts;
import com.bavis.budgetapp.dto.TransactionSyncRequestDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.AccountService;
import com.bavis.budgetapp.service.impl.AccountServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author Kellen Bavis
 *
 * Validation class to validate list of Account IDs corresponding to TransactionSyncRequestDto
 */
@Component
@Log4j2
public class TransactionSyncRequestAccountsValidator implements ConstraintValidator<TransactionSyncRequestValidAccounts, TransactionSyncRequestDto> {

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public void initialize(TransactionSyncRequestValidAccounts constraintAnnotation) {
        //nothing to initialize
    }

    @Override
    public boolean isValid(TransactionSyncRequestDto transactionSyncRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        log.info("Validating the list of Account ID's for the following TransactionSyncRequestDto: [{}]", transactionSyncRequestDto);
        List<String> accountIds = transactionSyncRequestDto.getAccounts();
        if(accountIds == null) {
            log.debug("Account IDs for the TransactionSyncRequestDto are null. Invalid TransactionSyncRequestDto.");
            return false;
        }

        boolean valid = false;
        for(String accountId: accountIds){
            //Fetch Account entity corresponding to Account ID
            Account account;
            try{
                account = accountService.read(accountId);
            } catch (RuntimeException e){
                log.error("The following exception occurred when reading Account with ID {}: [{}]", accountId, e.getMessage());
                return false;
            }

            //Validate Auth of Account corresponding to User
            //TODO: Update account service to validate that User is authenticated when reading and remove this logic
            if(account != null){
                User accountUser = account.getUser();
                User authUser = userService.getCurrentAuthUser();
                valid = Objects.equals(accountUser.getUserId(), authUser.getUserId());
                if(!valid){
                    return false;
                }
            } else {
                log.debug("Account ID corresponds to null/non-existing account. Invalid TransactionSyncRequestDto.");
                return false;
            }
        }
        log.debug("The list of accounts corresponding to TransactionSyncRequest validitiy: [{}]", valid);
        return valid;
    }
}
