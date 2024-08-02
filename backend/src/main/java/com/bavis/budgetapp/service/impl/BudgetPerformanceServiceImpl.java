package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.constants.OverviewType;
import com.bavis.budgetapp.dao.BudgetPerformanceRepository;
import com.bavis.budgetapp.entity.BudgetPerformance;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.model.BudgetOverview;
import com.bavis.budgetapp.model.BudgetPerformanceId;
import com.bavis.budgetapp.model.MonthYear;
import com.bavis.budgetapp.service.BudgetPerformanceService;
import com.bavis.budgetapp.service.TransactionService;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.util.GeneralUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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
        return repository.findById_UserId(user.getUserId());
    }

    /**
     * Generate BudgetPerformance for all active users for a specific month and year
     *
     * TODO: Make this endpoint Admin User permissions only
     *
     * @param monthYear
     *     - unique month/year to run Job for
     */
    @Override
    @Transactional
    public void runGenerateBudgetPerformanceJob(MonthYear monthYear) {

        //If no MonthYear passed in, use current MonthYear
        if(monthYear == null) {
            monthYear = new MonthYear();
            LocalDate currentDate = LocalDate.now();
            log.info("No MonthYear passed in; using current Month and Year");
            monthYear.setMonth(currentDate.getMonth().name());
            monthYear.setYear(currentDate.getYear());
        }



        //Fetch All Available User
        List<User> users = userService.readAll();

        List<BudgetPerformance> budgetPerformances = new ArrayList<>();
        for(User user: users) {
            //Skip all generating BudgetPerformance for users who already have a BudgetPerformance created for that Month/Year
            if(repository.findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId()) != null){
                log.info("BudgetPerformance for MonthYear {} and User {} already exists; skipping generation", monthYear, user.getUserId());
                continue;
            }

            BudgetPerformance budgetPerformance = new BudgetPerformance();

            List<Category> categories = user.getCategories();

            //Generate General BudgetOverviews
            HashMap<OverviewType, BudgetOverview> budgetOverviews = generateBudgetOverviews(categories, monthYear);


            //Generate BudgetPerformanceId
            BudgetPerformanceId id = BudgetPerformanceId.builder()
                            .monthYear(monthYear)
                            .userId(user.getUserId())
                            .build();
            budgetPerformance.setId(id);

            log.info("Generating Budget Performance with ID {} for User {} and MonthYear {}", id, user.getUserId(), monthYear);
            budgetOverviews.forEach(((overviewType, budgetOverview) -> {
                switch (overviewType){
                    case NEEDS -> budgetPerformance.setNeedsOverview(budgetOverview);
                    case WANTS -> budgetPerformance.setWantsOverview(budgetOverview);
                    case INVESTMENTS -> budgetPerformance.setInvestmentOverview(budgetOverview);
                    case GENERAL -> budgetPerformance.setGeneralOverview(budgetOverview);
                }
            }));

            //Add to List
            budgetPerformances.add(budgetPerformance);
        }

        //Persist all BudgetPerformance Entities
        if(!budgetPerformances.isEmpty()) { repository.saveAllAndFlush(budgetPerformances); }
    }

    /**
     * Fetch BudgetPerformance for specific MonthYear
     *
     * @param monthYear
     *          - Month/Year to fetch BudgetPerformance for
     * @return
     *          - persisted BudgetPerformance
     */
    @Override
    public BudgetPerformance fetchBudgetPerformance(MonthYear monthYear) {
        User user = userService.getCurrentAuthUser();
        return repository.findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId());
    }

    /**
     * Utility function to generate a BudgetOverview model
     *
     * @param userCategories
     *          - List of Categories to generate Overview for
     * @param monthYear
     *          - Unique Month/Year to generate budget for
     * @return
     *          - BudgetOverview model
     */
    public HashMap<OverviewType, BudgetOverview> generateBudgetOverviews(List<Category> userCategories, MonthYear monthYear) {
        String categoryIds = userCategories.stream()
                                .map(category -> String.valueOf(category.getCategoryId()))
                                .collect(Collectors.joining(", "));
        log.info("Generating Budget Overviews for the Categories [{}] and the Month {} and Year {}", categoryIds, monthYear.getMonth(), monthYear.getYear());

        HashMap<OverviewType, BudgetOverview> budgetOverviews = new HashMap<>();
        List<OverviewType> overviewTypes = List.of(OverviewType.GENERAL, OverviewType.NEEDS, OverviewType.WANTS, OverviewType.INVESTMENTS);

        for(OverviewType overviewType: overviewTypes) {
            List<Category> filteredCategories = new ArrayList<>(userCategories);

            //Filer Categories for Overview Type
            if(overviewType != OverviewType.GENERAL) {
                String categoryTypeName = GeneralUtil.nullSafeToLowerCaseOrEmpty(overviewType.getType());
                filteredCategories = userCategories.stream().filter(category -> categoryTypeName.equals(GeneralUtil.nullSafeToLowerCaseOrEmpty(category.getCategoryType().getName()))).toList();
                String filteredCategoryIds = filteredCategories.stream()
                        .map(category -> String.valueOf(category.getCategoryId()))
                        .collect(Collectors.joining(", "));
                log.info("Filtered Category Ids for Overview Type {} : [{}]", overviewType.getType(), filteredCategoryIds);
            }

            double totalAmountBudgeted = filteredCategories.stream()
                                            .mapToDouble(Category::getBudgetAmount)
                                            .sum();
            double totalAmountSpent = filteredCategories.stream()
                                            .map(category -> Optional.ofNullable(transactionService.fetchCategoryTransactions(category.getCategoryId())).orElse(Collections.emptyList())) //Fetch Transactions or use empty List
                                            .flatMap(List::stream)
                                            .filter(transaction -> GeneralUtil.isDateInMonthAndYear(transaction.getDate(), monthYear)) //Filter Transactions to verify ones in present month
                                            .mapToDouble(Transaction::getAmount)
                                            .sum();
            double totalBudgetUtilization = totalAmountSpent != 0 ? totalAmountSpent / totalAmountBudgeted : 0;

            BudgetOverview budgetOverview = BudgetOverview.builder()
                    .overviewType(overviewType)
                    .totalSpent(totalAmountSpent)
                    .totalAmountAllocated(totalAmountBudgeted)
                    .totalPercentUtilized(totalBudgetUtilization)
                    .build();
            log.info("Generated BudgetOverview for MonthYear {} and Categories [{}] : {}", monthYear, categoryIds, budgetOverview);
            budgetOverviews.put(overviewType, budgetOverview);
        }

        return budgetOverviews;
    }
}
