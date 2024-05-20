package com.bavis.budgetapp.validator;

import com.bavis.budgetapp.dto.AssignCategoryRequestDto;
import com.bavis.budgetapp.entity.*;
import com.bavis.budgetapp.service.impl.CategoryServiceImpl;
import com.bavis.budgetapp.service.impl.TransactionServiceImpl;
import com.bavis.budgetapp.service.impl.UserServiceImpl;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
public class AssignCategoryRequestValidatorTests {
    @Mock
    ConstraintValidatorContext constraintValidatorContext;

    @Mock
    private CategoryServiceImpl categoryService;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private TransactionServiceImpl transactionService;

    @InjectMocks
    AssignCategoryRequestUserValidator validator;

    @Test
    void testIsValid_ValidRequest_Successful() {
        //Arrange
        User user = User.builder()
                .userId(10L)
                .username("username")
                .build();

        Category category = Category.builder()
                .categoryId(10L)
                .name("Category One")
                .user(user)
                .build();

        Account account = Account.builder()
                .accountId("account-id")
                .user(user)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionId("transaction-id")
                .name("Transaction")
                .build();

        AssignCategoryRequestDto assignCategoryRequestDto = AssignCategoryRequestDto.builder()
                .categoryId(String.valueOf(category.getCategoryId()))
                .transactionId(transaction.getTransactionId())
                .build();

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(categoryService.read(Long.valueOf(assignCategoryRequestDto.getCategoryId()))).thenReturn(category);
        when(transactionService.readById(assignCategoryRequestDto.getTransactionId())).thenReturn(transaction);

        //Act
        boolean valid = validator.isValid(assignCategoryRequestDto, constraintValidatorContext);

        //Assert
        assertTrue(valid);

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(categoryService, times(1)).read(category.getCategoryId());
        verify(transactionService, times(1)).readById(transaction.getTransactionId());
    }

    @Test
    void testIsValid_InvalidCategoryId_Failure() {
        //Arrange
        User user = User.builder()
                .userId(10L)
                .username("username")
                .build();

        Category category = Category.builder()
                .categoryId(10L)
                .name("Category One")
                .user(user)
                .build();

        Account account = Account.builder()
                .accountId("account-id")
                .user(user)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionId("transaction-id")
                .name("Transaction")
                .build();

        AssignCategoryRequestDto assignCategoryRequestDto = AssignCategoryRequestDto.builder()
                .categoryId(String.valueOf(category.getCategoryId()))
                .transactionId(transaction.getTransactionId())
                .build();

        //Mock
        when(categoryService.read(Long.valueOf(assignCategoryRequestDto.getCategoryId()))).thenReturn(null);
        when(transactionService.readById(assignCategoryRequestDto.getTransactionId())).thenReturn(transaction);

        //Act
        boolean valid = validator.isValid(assignCategoryRequestDto, constraintValidatorContext);

        //Assert
        assertFalse(valid);

        //Verify
        verify(categoryService, times(1)).read(Long.valueOf(assignCategoryRequestDto.getCategoryId()));
        verify(transactionService, times(1)).readById(transaction.getTransactionId());
    }

    @Test
    void testIsValid_InvalidTransactionId_Failure() {
        //Arrange
        User user = User.builder()
                .userId(10L)
                .username("username")
                .build();

        Category category = Category.builder()
                .categoryId(10L)
                .name("Category One")
                .user(user)
                .build();

        Account account = Account.builder()
                .accountId("account-id")
                .user(user)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionId("transaction-id")
                .name("Transaction")
                .build();

        AssignCategoryRequestDto assignCategoryRequestDto = AssignCategoryRequestDto.builder()
                .categoryId(String.valueOf(category.getCategoryId()))
                .transactionId(transaction.getTransactionId())
                .build();

        //Mock
        when(categoryService.read(Long.valueOf(assignCategoryRequestDto.getCategoryId()))).thenReturn(category);
        when(transactionService.readById(assignCategoryRequestDto.getTransactionId())).thenThrow(new RuntimeException("Invalid Transaction ID"));

        //Act
        boolean valid = validator.isValid(assignCategoryRequestDto, constraintValidatorContext);

        //Assert
        assertFalse(valid);

        //Verify
        verify(categoryService, times(1)).read(Long.valueOf(assignCategoryRequestDto.getCategoryId()));
        verify(transactionService, times(1)).readById(transaction.getTransactionId());
    }

    @Test
    void testIsValid_DifferentUserTransaction_Failure() {
        //Arrange
        User user = User.builder()
                .userId(10L)
                .username("username")
                .build();

        User accountUser = User.builder()
                .userId(11L)
                .username("Different User")
                .build();

        Category category = Category.builder()
                .categoryId(10L)
                .name("Category One")
                .user(user)
                .build();

        Account account = Account.builder()
                .accountId("account-id")
                .user(accountUser)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionId("transaction-id")
                .name("Transaction")
                .build();

        AssignCategoryRequestDto assignCategoryRequestDto = AssignCategoryRequestDto.builder()
                .categoryId(String.valueOf(category.getCategoryId()))
                .transactionId(transaction.getTransactionId())
                .build();

        //Mock
        when(categoryService.read(Long.valueOf(assignCategoryRequestDto.getCategoryId()))).thenReturn(category);
        when(transactionService.readById(assignCategoryRequestDto.getTransactionId())).thenReturn(transaction);
        when(userService.getCurrentAuthUser()).thenReturn(user);

        //Act
        boolean valid = validator.isValid(assignCategoryRequestDto, constraintValidatorContext);

        //Assert
        assertFalse(valid);

        //Verify
        verify(categoryService, times(1)).read(Long.valueOf(assignCategoryRequestDto.getCategoryId()));
        verify(transactionService, times(1)).readById(transaction.getTransactionId());
        verify(userService, times(1)).getCurrentAuthUser();
    }

    @Test
    void testIsValid_DifferentUserCategory_Failure() {
        //Arrange
        User user = User.builder()
                .userId(10L)
                .username("username")
                .build();

        User categoryUser = User.builder()
                .userId(11L)
                .username("Different User")
                .build();

        Category category = Category.builder()
                .categoryId(10L)
                .name("Category One")
                .user(user)
                .build();

        Account account = Account.builder()
                .accountId("account-id")
                .user(categoryUser)
                .build();

        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionId("transaction-id")
                .name("Transaction")
                .build();

        AssignCategoryRequestDto assignCategoryRequestDto = AssignCategoryRequestDto.builder()
                .categoryId(String.valueOf(category.getCategoryId()))
                .transactionId(transaction.getTransactionId())
                .build();

        //Mock
        when(categoryService.read(Long.valueOf(assignCategoryRequestDto.getCategoryId()))).thenReturn(category);
        when(transactionService.readById(assignCategoryRequestDto.getTransactionId())).thenReturn(transaction);
        when(userService.getCurrentAuthUser()).thenReturn(user);

        //Act
        boolean valid = validator.isValid(assignCategoryRequestDto, constraintValidatorContext);

        //Assert
        assertFalse(valid);

        //Verify
        verify(categoryService, times(1)).read(Long.valueOf(assignCategoryRequestDto.getCategoryId()));
        verify(transactionService, times(1)).readById(transaction.getTransactionId());
        verify(userService, times(1)).getCurrentAuthUser();
    }

}
