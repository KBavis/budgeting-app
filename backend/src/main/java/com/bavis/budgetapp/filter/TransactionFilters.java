package com.bavis.budgetapp.filter;

import com.bavis.budgetapp.dao.TransactionRepository;
import com.bavis.budgetapp.dto.request.PlaidTransactionDto;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.util.GeneralUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TransactionFilters {

    private final TransactionRepository transactionRepository;


    /**
     * Generic Filters used by each group
     */
    private static final Predicate<Transaction> HAS_POSITIVE_AMOUNT =
            transaction -> transaction.getAmount() > 0;


    private static final Predicate<Transaction> IS_CURRENT_MONTH =
            transaction -> GeneralUtil.isDateInCurrentMonth(transaction.getDate());


    private static final Predicate<Transaction> IS_PREVIOUS_MONTH =
            transaction -> GeneralUtil.isDateInPreviousMonth(transaction.getDate());

    private static final Predicate<Transaction> IS_ACTIVE =
            transaction -> GeneralUtil.isActive(transaction.getStartDate(), transaction.getEndDate());

    private Predicate<Transaction> alreadyExists() {
        return transaction -> transactionRepository.existsById(transaction.getTransactionId());
    }

    /**
     * Plaid re-sends Transactions we have already persisted (e.g. modifications, previous month re-syncs, or a full re-sync
     * after reconnecting). Saving one of those over a Transaction the user soft deleted would reset its end date and
     * silently revive it, so any Transaction persisted as soft deleted is excluded.
     */
    private Predicate<Transaction> notSoftDeleted() {
        return transaction -> !transactionRepository.existsByTransactionIdAndEndDateBefore(transaction.getTransactionId(), LocalDate.now());
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
                .and(IS_ACTIVE)
                .and(notAlreadyAccountedFor(previousMonthTransactions))
                .and(notSoftDeleted());
    }

    /**
     * Filter group for modified transactions
     */
    public Predicate<Transaction> modifiedTransactionFilters() {
        return HAS_POSITIVE_AMOUNT
                .and(IS_CURRENT_MONTH)
                .and(IS_ACTIVE)
                .and(alreadyExists())
                .and(alreadyExistsAndUpdatedByUser())
                .and(notSoftDeleted());
    }

    /**
     * Filter group for added transactions
     */
    public Predicate<Transaction> addedTransactionFilters() {
        return HAS_POSITIVE_AMOUNT
                .and(IS_CURRENT_MONTH)
                .and(notSoftDeleted());
    }


    /**
     * Filtering to account for pending/posted transactions that a user may have already updated
     */
    public Predicate<PlaidTransactionDto> isPendingAndUserModified(Set<String> pendingTransactionIds) {
        return plaidTransactionDto -> {
            String pendingTransactionId = plaidTransactionDto.getPending_transaction_id();

            // if the transaction doesn't corresponding to pending transaction, then we should not filter out
            if (StringUtils.isEmpty(pendingTransactionId)) {
                return true;
            }

            // check if we have modified the transaction
            Transaction persistedTransaction = transactionRepository.findById(pendingTransactionId).orElse(null);
            if (persistedTransaction == null) {
                return true; //transaction isn't persisted, so we should not filter out
            }

            // filter out transaction if it was updated by user
            if (persistedTransaction.isUpdatedByUser()){
                // add transaction ID in order to account for this in removedTransactions filter later on
                pendingTransactionIds.add(pendingTransactionId);
                return false;
            }
            return true;
        };
    }

}