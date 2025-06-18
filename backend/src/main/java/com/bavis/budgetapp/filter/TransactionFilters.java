package com.bavis.budgetapp.filter;

import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.service.TransactionService;
import com.bavis.budgetapp.util.GeneralUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class TransactionFilters {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionRepository transactionRepository;


    /**
     * Generic Filters used by each group
     */
    private static final Predicate<Transaction> HAS_POSITIVE_AMOUNT =
            transaction -> transaction.getAmount() > 0;


    private static final Predicate<Transaction> IS_CURRENT_MONTH =
            transaction -> GeneralUtil.isDateInCurrentMonth(transaction.getDate());


    private static final Predicate<Transaction> IS_PREVIOUS_MONTH =
            transaction -> GeneralUtil.isDateInPreviousMonth(transaction.getDate());


    private Predicate<Transaction> alreadyExists() {
        return transaction -> transactionRepository.existsById(transaction.getTransactionId());
    }

    private Predicate<Transaction> alreadyExistsAndUpdatedByUser() {
        return transaction -> !transactionRepository.existsByTransactionIdAndUpdatedByUserIsTrue(transaction.getTransactionId());
    }


    /**
     * Filter to ensure that we have not already previously account for this transaction
     *
     * @param prevMonthTransactions
     *          - previous month transactions already accounted for
     */
    private Predicate<Transaction> notAlreadyAccountedFor(List<Transaction> prevMonthTransactions) {
        Set<String> existingIds = prevMonthTransactions.stream()
                .map(Transaction::getTransactionId)
                .collect(Collectors.toSet());

        return transaction -> !existingIds.contains(transaction.getTransactionId());
    }

    /***
     * Filter group for previous month transactions
     *
     * @param previousMonthTransactions
     *          - previous month transactions that have already been accounted for
     */
    public Predicate<Transaction> prevMonthTransactionFilters(List<Transaction> previousMonthTransactions) {
        return HAS_POSITIVE_AMOUNT
                .and(IS_PREVIOUS_MONTH)
                .and(notAlreadyAccountedFor(previousMonthTransactions));
    }

    /**
     * Filter group for modified transactions
     */
   public Predicate<Transaction> modifiedTransactionFilters() {
        return HAS_POSITIVE_AMOUNT
                .and(IS_CURRENT_MONTH)
                .and(alreadyExists())
                .and(alreadyExistsAndUpdatedByUser());
   }

    /**
     * Filter group for added transactions
     */
   public Predicate<Transaction> addedTransactionFilters() {
       return HAS_POSITIVE_AMOUNT
               .and(IS_CURRENT_MONTH);
   }

}
