package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AccountsDtoValidAccounts;
import com.bavis.budgetapp.dto.AccountsDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.impl.AccountServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
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
public class AccountsDtoAccountsValidator implements ConstraintValidator<AccountsDtoValidAccounts, AccountsDto> {

    @Autowired
    private AccountServiceImpl accountService;

    @Autowired
    private UserServiceImpl userService;

    @Override
    public void initialize(AccountsDtoValidAccounts constraintAnnotation) {
        //nothing to initialize
    }

    @Override
    public boolean isValid(AccountsDto accountsDto, ConstraintValidatorContext constraintValidatorContext) {
        List<String> accountIds = accountsDto.getAccounts();
        if(accountIds == null) {
            return false;
        }

        boolean valid = false;
        for(String accountId: accountIds){
            //Fetch Account entity corresponding to Account ID
            Account account;
            try{
                account = accountService.read(accountId);
            } catch (RuntimeException e){
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
                return false;
            }
        }
        return valid;
    }
}
