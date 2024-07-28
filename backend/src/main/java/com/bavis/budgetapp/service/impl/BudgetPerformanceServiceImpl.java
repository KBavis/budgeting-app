package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.constants.OverviewType;
import com.bavis.budgetapp.dao.BudgetPerformanceRepository;
import com.bavis.budgetapp.entity.BudgetPerformance;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.model.BudgetOverview;
import com.bavis.budgetapp.model.MonthYear;
import com.bavis.budgetapp.service.BudgetPerformanceService;
import com.bavis.budgetapp.service.TransactionService;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.util.GeneralUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.sql.ast.tree.expression.Over;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of our BudgetPerformance service
 *
 */
@Service
@Log4j2
public class BudgetPerformanceServiceImpl implements BudgetPerformanceService{

    @Autowired
    private BudgetPerformanceRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionService transactionService;

    @Override
    public List<BudgetPerformance> fetchBudgetPerformances() {
        User user = userService.getCurrentAuthUser();
        return repository.findByUserUserId(user.getUserId());
    }

    @Override
    @Transactional
    public void runGenerateBudgetPerformanceJob(MonthYear monthYear) {

        //If no MonthYear passed in, use current MonthYear
        if(StringUtils.isBlank(monthYear.getMonth())) {
            LocalDate currentDate = LocalDate.now();
            monthYear.setMonth(currentDate.getMonth().name());
            monthYear.setYear(currentDate.getYear());
        }



        //Fetch All Available User
        List<User> users = userService.readAll();

        List<BudgetPerformance> budgetPerformances = new ArrayList<>();
        for(User user: users) {
            BudgetPerformance budgetPerformance = new BudgetPerformance();

            List<Category> categories = user.getCategories();

            //Generate General BudgetOverview
            BudgetOverview generalBudgetOverview = generateBudgetOverview(categories, OverviewType.GENERAL, monthYear);
            budgetPerformance.setGeneralOverview(generalBudgetOverview);

            //Generate Needs Budget Overview
            BudgetOverview needsOverview = generateBudgetOverview(categories, OverviewType.NEEDS, monthYear);
            budgetPerformance.setNeedsOverview(needsOverview);

            //Generate Wants Budget Overview
            BudgetOverview wantsOverview = generateBudgetOverview(categories, OverviewType.WANTS, monthYear);
            budgetPerformance.setWantsOverview(wantsOverview);

            //Generate Investment Budget Overview
            BudgetOverview investmentOverview = generateBudgetOverview(categories, OverviewType.INVESTMENTS, monthYear);
            budgetPerformance.setInvestmentOverview(investmentOverview);

            budgetPerformance.setMonthYear(monthYear);
            budgetPerformance.setUser(user);
            budgetPerformance.setCategories(categories);

            //Add to List
            budgetPerformances.add(budgetPerformance);
        }

        //Persist all BudgetPerformance Entities
        repository.saveAllAndFlush(budgetPerformances);
    }

    @Override
    public BudgetPerformance fetchBudgetPerformance(MonthYear monthYear) {
        User user = userService.getCurrentAuthUser();
        return repository.findByMonthYear_MonthAndMonthYear_YearAndUserUserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId());
    }

    /**
     * Utility function to generate a BudgetOverview model
     *
     * @param categories
     *          - List of Categories to generate Overview for
     * @return
     *          - BudgetOverview model
     */
    public BudgetOverview generateBudgetOverview(List<Category> categories, OverviewType type, MonthYear monthYear) {
        //Filter based on CategoryType
        if(type != OverviewType.GENERAL) {
            String categoryTypeName = GeneralUtil.nullSafeToLowerCaseOrEmpty(type.getType());
            categories = categories.stream().filter(category -> categoryTypeName.equals(GeneralUtil.nullSafeToLowerCaseOrEmpty(category.getCategoryType().getName()))).toList();
        }

        //Calculate Total Amount Budgeted
        double totalAmountBudgeted = categories.stream().mapToDouble(Category::getBudgetAmount).sum();
        double totalAmountSpent = 0.0;

        //Calculate Total Amount Spent for Each Category combined
        for(Category category: categories) {
            List<Transaction> categoryTransactions = transactionService.fetchCategoryTransactions(category.getCategoryId());
            categoryTransactions = categoryTransactions.stream().filter(transaction -> GeneralUtil.isDateInMonthAndYear(transaction.getDate(), monthYear)).toList();

            //Calculate Total Spent
            totalAmountSpent += categoryTransactions.stream().mapToDouble(Transaction::getAmount).sum();

        }

        //Calculate Budget Utilization
        double totalBudgetUtilization = totalAmountSpent != 0 ? totalAmountSpent / totalAmountBudgeted : 0;


        return BudgetOverview.builder()
                .overviewType(type)
                .totalSpent(totalAmountSpent)
                .totalAmountAllocated(totalAmountBudgeted)
                .totalPercentUtilized(totalBudgetUtilization)
                .build();
    }
}
