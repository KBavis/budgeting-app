package com.bavis.budgetapp.filter;

import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.PlaidTransactionDto;
import com.bavis.budgetapp.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(profiles = "test")
public class TransactionFilterTests {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionFilters transactionFilters;

    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        sampleTransaction = new Transaction();
        sampleTransaction.setTransactionId("tx123");
        sampleTransaction.setAmount(100.0);
        sampleTransaction.setDate(LocalDate.now());
        sampleTransaction.setDeleted(false);
        sampleTransaction.setUpdatedByUser(false);
    }

    @Test
    void testPrevMonthTransactionFilters_shouldPass() {
        sampleTransaction.setDate(LocalDate.now().minusMonths(1));

        List<Transaction> accounted = new ArrayList<>();
        boolean result = transactionFilters.prevMonthTransactionFilters(accounted).test(sampleTransaction);

        assertTrue(result);
    }

    @Test
    void testPrevMonthTransactionFilters_shouldFail_ifDeleted() {
        sampleTransaction.setDate(LocalDate.now().minusMonths(1));
        sampleTransaction.setDeleted(true);

        boolean result = transactionFilters.prevMonthTransactionFilters(Collections.emptyList()).test(sampleTransaction);

        assertFalse(result);
    }

    @Test
    void testPrevMonthTransactionFilters_shouldFail_ifAlreadyAccounted() {
        Transaction existing = new Transaction();
        existing.setTransactionId("tx123");

        List<Transaction> accounted = List.of(existing);

        sampleTransaction.setDate(LocalDate.now().minusMonths(1));

        boolean result = transactionFilters.prevMonthTransactionFilters(accounted).test(sampleTransaction);

        assertFalse(result);
    }

    @Test
    void testModifiedTransactionFilters_shouldPass() {
        sampleTransaction.setUpdatedByUser(false);
        when(transactionRepository.existsById("tx123")).thenReturn(true);
        when(transactionRepository.existsByTransactionIdAndUpdatedByUserIsTrue("tx123")).thenReturn(false);

        boolean result = transactionFilters.modifiedTransactionFilters().test(sampleTransaction);

        assertTrue(result);
    }

    @Test
    void testModifiedTransactionFilters_shouldFail_notCurrentMonth() {
        sampleTransaction.setDate(LocalDate.now().minusMonths(1));

        boolean result = transactionFilters.modifiedTransactionFilters().test(sampleTransaction);

        assertFalse(result);
    }

    @Test
    void testModifiedTransactionFilters_shouldFail_ifNotExists() {
        when(transactionRepository.existsById("tx123")).thenReturn(false);

        boolean result = transactionFilters.modifiedTransactionFilters().test(sampleTransaction);

        assertFalse(result);
    }

    @Test
    void testModifiedTransactionFilters_shouldFail_ifUpdatedByUser() {
        when(transactionRepository.existsById("tx123")).thenReturn(true);
        when(transactionRepository.existsByTransactionIdAndUpdatedByUserIsTrue("tx123")).thenReturn(true);

        boolean result = transactionFilters.modifiedTransactionFilters().test(sampleTransaction);

        assertFalse(result);
    }

    @Test
    void testModifiedTransactionFilters_shouldFail_ifDeleted() {
        sampleTransaction.setDeleted(true);

        boolean result = transactionFilters.modifiedTransactionFilters().test(sampleTransaction);

        assertFalse(result);
    }

    @Test
    void testModifiedTransactionFilters_shouldFail_ifNegativeAmount() {
        sampleTransaction.setAmount(-50.0);

        boolean result = transactionFilters.modifiedTransactionFilters().test(sampleTransaction);

        assertFalse(result);
    }

    @Test
    void testAddedTransactionFilters_shouldPass_forPositiveCurrentMonthTransaction() {
        boolean result = transactionFilters.addedTransactionFilters().test(sampleTransaction);

        assertTrue(result);
    }

    @Test
    void testAddedTransactionFilters_shouldFail_ifNegativeAmount() {
        sampleTransaction.setAmount(-50.0);

        boolean result = transactionFilters.addedTransactionFilters().test(sampleTransaction);

        assertFalse(result);
    }

    @Test
    void testAddedTransactionFilters_shouldFail_ifNotCurrentMonth() {
        sampleTransaction.setDate(LocalDate.now().minusMonths(1));
        boolean result = transactionFilters.addedTransactionFilters().test(sampleTransaction);
        assertFalse(result);
    }

    @Test
    void testIsPendingAndUserModified_shouldPass_ifNoPendingId() {
        PlaidTransactionDto dto = new PlaidTransactionDto();
        dto.setPending_transaction_id(null);

        boolean result = transactionFilters.isPendingAndUserModified(new HashSet<>()).test(dto);

        assertTrue(result);
    }

    @Test
    void testIsPendingAndUserModified_shouldPass_ifTransactionNotPersisted() {
        PlaidTransactionDto dto = new PlaidTransactionDto();
        dto.setPending_transaction_id("pending123");

        when(transactionRepository.findById("pending123")).thenReturn(Optional.empty());

        boolean result = transactionFilters.isPendingAndUserModified(new HashSet<>()).test(dto);

        assertTrue(result);
    }

    @Test
    void testIsPendingAndUserModified_shouldFail_ifUpdatedByUser() {
        Transaction updated = new Transaction();
        updated.setUpdatedByUser(true);

        PlaidTransactionDto dto = new PlaidTransactionDto();
        dto.setPending_transaction_id("pending123");

        when(transactionRepository.findById("pending123")).thenReturn(Optional.of(updated));

        Set<String> filtered = new HashSet<>();
        boolean result = transactionFilters.isPendingAndUserModified(filtered).test(dto);

        assertFalse(result);
        assertTrue(filtered.contains("pending123"));
    }

    @Test
    void testIsPendingAndUserModified_shouldPass_ifNotUpdatedByUser() {
        Transaction notUpdated = new Transaction();
        notUpdated.setUpdatedByUser(false);

        PlaidTransactionDto dto = new PlaidTransactionDto();
        dto.setPending_transaction_id("pending456");

        when(transactionRepository.findById("pending456")).thenReturn(Optional.of(notUpdated));

        boolean result = transactionFilters.isPendingAndUserModified(new HashSet<>()).test(dto);

        assertTrue(result);
    }
}
