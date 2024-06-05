package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.PlaidTransactionDto;
import com.bavis.budgetapp.dto.TransactionDto;
import com.bavis.budgetapp.entity.Account;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {TransactionMapperImpl.class})
@ActiveProfiles(profiles = "test")
@SpringBootTest
public class TransactionMapperTests {

    @Autowired
    private TransactionMapper transactionMapper;

    @Test
    void testToEntity_Successful() {
        //Arrange
        String accountId = "account-id";
        String transactionName = "Bookstore";
        String logoUrl = "logo-url";
        String confidenceLevel = "HIGH";
        String primary = "PRIMARY";
        String detailed = "DETAILED";
        String transactionId = "123XYZ";
        Double amount = 1000.0;

        PlaidTransactionDto.CounterpartyDto counterpartyDto = PlaidTransactionDto.CounterpartyDto.builder()
                .name(transactionName)
                .logo_url(logoUrl)
                .build();

        List<PlaidTransactionDto.CounterpartyDto> counterpartyDtoList = List.of(counterpartyDto);

        PlaidTransactionDto.PersonalFinanceCategoryDto personalFinanceCategoryDto = PlaidTransactionDto.PersonalFinanceCategoryDto.builder()
                .confidence_level(confidenceLevel)
                .primary(primary)
                .detailed(detailed)
                .build();

        LocalDate date = LocalDate.now();

        PlaidTransactionDto plaidTransactionDto = PlaidTransactionDto.builder()
                .transaction_id(transactionId)
                .counterparties(counterpartyDtoList)
                .personal_finance_category(personalFinanceCategoryDto)
                .amount(amount)
                .datetime(date)
                .date(null)
                .authorized_date(null)
                .account_id(accountId)
                .build();

        //Act
        Transaction target = transactionMapper.toEntity(plaidTransactionDto);

        //Assert
        assertNotNull(target);
        assertEquals(transactionId, target.getTransactionId());
        assertEquals(counterpartyDto.getName(), target.getName());
        assertEquals(amount, target.getAmount());
        assertEquals(date, target.getDate());
        assertEquals(logoUrl, target.getLogoUrl());
    }

    /**
     * Test Case to validate that even if 'dateTime' attribute is null, we can fetch a date
     */
    @Test
    void testToEntity_NullDatetime_Successful(){
        //Arrange
        String accountId = "account-id";
        String transactionName = "Bookstore";
        String logoUrl = "logo-url";
        String confidenceLevel = "HIGH";
        String primary = "PRIMARY";
        String detailed = "DETAILED";
        String transactionId = "123XYZ";
        Double amount = 1000.0;

        PlaidTransactionDto.CounterpartyDto counterpartyDto = PlaidTransactionDto.CounterpartyDto.builder()
                .name(transactionName)
                .logo_url(logoUrl)
                .build();

        List<PlaidTransactionDto.CounterpartyDto> counterpartyDtoList = List.of(counterpartyDto);

        PlaidTransactionDto.PersonalFinanceCategoryDto personalFinanceCategoryDto = PlaidTransactionDto.PersonalFinanceCategoryDto.builder()
                .confidence_level(confidenceLevel)
                .primary(primary)
                .detailed(detailed)
                .build();

        LocalDate localDate = null;
        Date authorizedDate = null;
        Date date = new Date();
        LocalDate expectedLocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(); //validate converted properly

        PlaidTransactionDto plaidTransactionDto = PlaidTransactionDto.builder()
                .transaction_id(transactionId)
                .counterparties(counterpartyDtoList)
                .personal_finance_category(personalFinanceCategoryDto)
                .amount(amount)
                .datetime(localDate)
                .authorized_date(authorizedDate)
                .date(date)
                .account_id(accountId)
                .build();

        //Act
        Transaction target = transactionMapper.toEntity(plaidTransactionDto);

        //Assert
        assertNotNull(target);
        assertEquals(transactionId, target.getTransactionId());
        assertEquals(counterpartyDto.getName(), target.getName());
        assertEquals(amount, target.getAmount());
        assertEquals(expectedLocalDate, target.getDate());
        assertEquals(logoUrl, target.getLogoUrl());
    }

    @Test
    void testToEntity_TransactionDto_Successful() {
        //Arrange
        String transactionName = "Transaction";
        double updatedAmount = 1000.0;
        String logoUrl = "logo-url";
        String transactionId = "transactionId";
        LocalDate localDate = LocalDate.now();
        Account account = Account.builder()
                .accountId("account-id")
                .build();
        Category category = Category.builder()
                .categoryId(10L)
                .build();
        TransactionDto transactionDto = TransactionDto.builder()
                .updatedName(transactionName)
                .updatedAmount(updatedAmount)
                .account(account)
                .category(category)
                .date(localDate)
                .build();

        //Act
        Transaction newTransaction = transactionMapper.toEntity(transactionDto);

        //Assert
        assertNotNull(newTransaction);
        assertEquals(transactionName, newTransaction.getName());
        assertEquals(updatedAmount, newTransaction.getAmount());
        assertEquals(localDate, newTransaction.getDate());
        assertNull(newTransaction.getTransactionId());
        assertNull(newTransaction.getCategory());
        assertNull(newTransaction.getAccount());
    }

}
