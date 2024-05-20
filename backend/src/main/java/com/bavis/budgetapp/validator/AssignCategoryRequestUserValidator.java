package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.annotation.AssignCategoryRequestValidUser;
import com.bavis.budgetapp.dto.AssignCategoryRequestDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.impl.CategoryServiceImpl;
import com.bavis.budgetapp.service.impl.TransactionServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Ensures that AssignCategoryRequestDto Category ID & Transaction ID correspond to Authenticated User making request
 */
@Component
@Log4j2
public class AssignCategoryRequestUserValidator implements ConstraintValidator<AssignCategoryRequestValidUser, AssignCategoryRequestDto> {

    @Autowired
    private CategoryServiceImpl categoryService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private TransactionServiceImpl transactionService;


    @Override
    public void initialize(AssignCategoryRequestValidUser constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(AssignCategoryRequestDto assignCategoryRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        Long categoryId = assignCategoryRequestDto.getCategoryId();
        String transactionId = assignCategoryRequestDto.getTransactionId();
        log.info("Determining if the following transaction ID [{}] and category ID [{}] correspond to same user", transactionId, categoryId);

        if(categoryId == null || transactionId == null || transactionId.isEmpty()){
            log.debug("Invalid AssignCategoryRequestDto! Users do not correspond to Auth User");
            return  false;
        }

        try {
            Category category = categoryService.read(categoryId);
            Transaction transaction = transactionService.readById(transactionId);
            if(category == null || transaction == null || transaction.getAccount() == null || category.getUser() == null) {
                log.debug("Invalid AssignCategoryRequestDto! Users do not correspond to Auth User");
                return false;
            }
            //Fetch Authenticated User
            User currentAuthUser = userService.getCurrentAuthUser();

            //Fetch User Corresponding to Transaction & Category
            Account transactionAccount = transaction.getAccount();
            User transactionUser = transactionAccount.getUser();
            User categoryUser = category.getUser();

            //Validate Corresponding To Auth User
            boolean valid = Objects.equals(currentAuthUser.getUserId(), transactionUser.getUserId()) && Objects.equals(currentAuthUser.getUserId(), categoryUser.getUserId());
            log.debug("AssignCategoryRequestDto [{}] validation: {}", assignCategoryRequestDto, valid);
            return  valid;
        } catch (RuntimeException e){
            log.debug("Runtime Exception occured during AssingCategoryRequestUserValidator: [{}]", e.getMessage());
            return false;
        }
    }
}
