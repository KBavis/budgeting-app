package com.bavis.budgetapp.mapper;

import com.bavis.budgetapp.dto.PlaidTransactionDto;
import com.bavis.budgetapp.entity.Transaction;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        PlaidTransactionDto.PersonalFinanceCategoryDto personalFinanceCategoryDto = PlaidTransactionDto.PersonalFinanceCategoryDto.builder()
                .confidence_level(confidenceLevel)
                .primary(primary)
                .detailed(detailed)
                .build();

        LocalDate date = LocalDate.now();

        PlaidTransactionDto plaidTransactionDto = PlaidTransactionDto.builder()
                .transaction_id(transactionId)
                .counterpartyDto(counterpartyDto)
                .personalFinanceCategoryDto(personalFinanceCategoryDto)
                .amount(amount)
                .datetime(date)
                .account_id(accountId)
                .build();

        //Act
        Transaction target = transactionMapper.toEntity(plaidTransactionDto);

        //Assert
        assertNotNull(target);
        assertEquals(transactionId, plaidTransactionDto.getTransaction_id());
        assertEquals(counterpartyDto, plaidTransactionDto.getCounterpartyDto());
        assertEquals(personalFinanceCategoryDto, plaidTransactionDto.getPersonalFinanceCategoryDto());
        assertEquals(amount, plaidTransactionDto.getAmount());
        assertEquals(date, plaidTransactionDto.getDatetime());
        assertEquals(accountId, plaidTransactionDto.getAccount_id());
    }

}
