package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.dao.MonthlyCategoryPerformanceRepository;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.entity.analysis.MerchantAnalysis;
import com.bavis.budgetapp.entity.analysis.MonthlyCategoryPerformance;
import com.bavis.budgetapp.model.MonthYear;
import com.bavis.budgetapp.service.MonthlyCategoryPerformanceService;
import com.bavis.budgetapp.service.TransactionService;
import com.bavis.budgetapp.util.GeneralUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class MonthlyCategoryPerformanceServiceImpl implements MonthlyCategoryPerformanceService {

    @Autowired
    private MonthlyCategoryPerformanceRepository repository;

    @Autowired
    @Lazy
    private TransactionService transactionService;

    @Override
    public void generateMonthlyCategoryPerformances(Long userId, MonthYear monthYear, List<Category> categories) {

        List<MonthlyCategoryPerformance> monthlyCategoryPerformances = new ArrayList<>();
        for (Category category : categories) {
            Long categoryId = category.getCategoryId();
            Long categoryTypeId = category.getCategoryType().getCategoryTypeId();
            Double totalAmountAllocated = category.getBudgetAmount();

            // fetch Transactions corresponding to month, year, and category
            List<Transaction> categoryTransactions = getCategoryTransactions(monthYear, categoryId);

            Double totalSpend = getTotalSpend(categoryTransactions);
            Integer transactionCount = categoryTransactions.size();

            if (totalAmountAllocated <= 0) {
                throw new RuntimeException("Unable to calculate Category utilization due to Category allocation <= 0");
            }
            Double categoryUtilization = Math.round((totalSpend / totalAmountAllocated) * 100.0) / 100.0;;

            MonthlyCategoryPerformance categoryPerformance = MonthlyCategoryPerformance.builder()
                    .categoryTypeId(categoryTypeId)
                    .categoryId(categoryId)
                    .categoryPercentUtilization(categoryUtilization)
                    .totalAmountAllocated(totalAmountAllocated)
                    .totalSpend(totalSpend)
                    .monthYear(monthYear)
                    .userId(userId)
                    .transactionCount(transactionCount)
                    .topMerchants(getTopThreeMerchants(categoryTransactions))
                    .build();
            monthlyCategoryPerformances.add(categoryPerformance);
        }

        if (!monthlyCategoryPerformances.isEmpty()) {
            log.info("Saving {} MonthlyCategoryPerformances for User {} for {} {}", categories.size(), userId, monthYear.getMonth(), monthYear.getYear());
            repository.saveAll(monthlyCategoryPerformances);
        } else {
            log.warn("No MonthlyCategoryPerformances generated for User {} for {} {}", userId, monthYear.getMonth(), monthYear.getYear());
        }

    }

    @Override
    public List<MonthlyCategoryPerformance> getPerformances(Long categoryTypeId, MonthYear monthYear) {
        return repository.findByCategoryTypeIdAndMonthYear(categoryTypeId, monthYear);
    }

    public Double getTotalSpend(List<Transaction> transactions) {
        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    /**
     * Helper function to retrieve relevant Transactions corresponding to Category
     *
     * @param monthYear
     *          - relevant month and year
     * @param categoryId
     *          - category that Transactions correspond to
     * @return
     *          - relevant Categories
     */
    public List<Transaction> getCategoryTransactions(MonthYear monthYear, Long categoryId) {
        return Optional.ofNullable(transactionService.fetchCategoryTransactions(categoryId)).orElse(Collections.emptyList()).stream()
                .filter(transaction -> GeneralUtil.isDateInMonthAndYear(transaction.getDate(), monthYear))
                .toList();
    }


    /**
     * Retrieve the top 3 merchants based on monthly spending for month
     *
     * TODO: Effectively handle Venmo transactions and missing merchant names
     *
     * @param transactions
     *          - relevant Transactions
     * @return
     *          - top three merchants
     */
    public List<MerchantAnalysis> getTopThreeMerchants(List<Transaction> transactions) {
        Map<String, List<Transaction>> merchantMapping = new HashMap<>();

        // group Transactions based on merchant name
        for (Transaction transaction : transactions) {
            String merchantName = !StringUtils.isEmpty(transaction.getMerchantName()) ? transaction.getMerchantName() : transaction.getName();

            List<Transaction> merchantTransactions = merchantMapping.getOrDefault(merchantName, new ArrayList<>());
            merchantTransactions.add(transaction);

            merchantMapping.put(merchantName, merchantTransactions);
        }

        // generate merchant analysis for Transactions
        List<MerchantAnalysis> merchantAnalyses = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : merchantMapping.entrySet()) {
            String merchantName = entry.getKey();
            double totalAmountSpent = entry.getValue().stream()
                    .mapToDouble(Transaction::getAmount)
                    .sum();
            int transactionCount = entry.getValue().size();
            Double avgTransactionAmount = BigDecimal.valueOf(totalAmountSpent)
                    .divide(BigDecimal.valueOf(transactionCount), 2, RoundingMode.HALF_UP)
                    .doubleValue();

            MerchantAnalysis analysis = MerchantAnalysis.builder()
                    .totalSpent(totalAmountSpent)
                    .transactionCount(transactionCount)
                    .merchantName(merchantName)
                    .avgTransactionAmount(avgTransactionAmount)
                    .build();
            merchantAnalyses.add(analysis);
        }


        // extract top 3 greatest spenders by merchant
        return merchantAnalyses.stream()
                .sorted(Comparator.comparing(MerchantAnalysis::getTotalSpent).reversed())
                .limit(3)
                .toList();
    }
}
