package com.bavis.budgetapp.entity;

import com.bavis.budgetapp.constants.TemporalConstants;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionTests {

    @Test
    void testAlignStartDate_transactionSyncedAfterItOccurred_usesTransactionDate() {
        LocalDate transactionDate = LocalDate.of(2026, 10, 26);
        Transaction transaction = Transaction.builder()
                .date(transactionDate)
                .startDate(LocalDate.of(2026, 11, 7))
                .build();

        transaction.alignStartDateWithTransactionDate();

        assertEquals(transactionDate, transaction.getStartDate());
    }

    @Test
    void testAlignStartDate_existingEarlierStartDate_isPreserved() {
        Transaction transaction = Transaction.builder()
                .date(LocalDate.of(2026, 10, 26))
                .startDate(TemporalConstants.BEGINNING_OF_TIME)
                .build();

        transaction.alignStartDateWithTransactionDate();

        assertEquals(TemporalConstants.BEGINNING_OF_TIME, transaction.getStartDate());
    }

    @Test
    void testAlignStartDate_nullTransactionDate_leavesStartDateUnchanged() {
        LocalDate startDate = LocalDate.of(2026, 11, 7);
        Transaction transaction = Transaction.builder()
                .startDate(startDate)
                .build();

        transaction.alignStartDateWithTransactionDate();

        assertEquals(startDate, transaction.getStartDate());
    }

    @Test
    void testAlignStartDate_doesNotAffectEndDate() {
        LocalDate endDate = LocalDate.of(2026, 11, 6);
        Transaction transaction = Transaction.builder()
                .date(LocalDate.of(2026, 10, 26))
                .startDate(LocalDate.of(2026, 11, 7))
                .endDate(endDate)
                .build();

        transaction.alignStartDateWithTransactionDate();

        assertEquals(endDate, transaction.getEndDate());
    }
}
