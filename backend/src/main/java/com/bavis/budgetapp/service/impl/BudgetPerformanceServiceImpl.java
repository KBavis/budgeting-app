package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.constants.OverviewType;
import com.bavis.budgetapp.dao.BudgetPerformanceRepository;
import com.bavis.budgetapp.entity.analysis.BudgetPerformance;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.CategoryTypeVt;
import com.bavis.budgetapp.entity.CategoryVt;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.model.BudgetOverview;
import com.bavis.budgetapp.model.BudgetPerformanceId;
import com.bavis.budgetapp.model.MonthYear;
import com.bavis.budgetapp.service.BudgetPerformanceService;
import com.bavis.budgetapp.service.CategoryService;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.EffectivityService;
import com.bavis.budgetapp.service.MonthlyCategoryPerformanceService;
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

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryTypeService categoryTypeService;

    @Autowired
    private MonthlyCategoryPerformanceService categoryPerformanceService;

    @Autowired
    private EffectivityService effectivityService;

    @Override
    public List<BudgetPerformance> fetchBudgetPerformances() {
        User user = userService.getCurrentAuthUser();
        return repository.findById_UserId(user.getUserId());
    }

    @Override
    @Transactional
    public void runGenerateBudgetPerformanceJob(MonthYear monthYear) {

        if (monthYear == null) {
            monthYear = new MonthYear();
            LocalDate currentDate = LocalDate.now();
            log.info("No MonthYear passed in; using current Month and Year");
            monthYear.setMonth(currentDate.getMonth().name());
            monthYear.setYear(currentDate.getYear());
        }

        List<User> users = userService.readAll();

        List<BudgetPerformance> budgetPerformances = new ArrayList<>();
        for (User user : users) {
            if (repository.findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(),
                    monthYear.getYear(), user.getUserId()).isPresent()) {
                log.info("BudgetPerformance for MonthYear {} and User {} already exists; skipping generation",
                        monthYear, user.getUserId());
                continue;
            }

            BudgetPerformance budgetPerformance = new BudgetPerformance();

            LocalDate asOfDate = monthYear.toEndOfMonthDate();
            List<Category> categories = categoryService.findAllEntities(asOfDate);

            log.info("Calculating Budget Overviews for User {}", user.getUsername());
            HashMap<OverviewType, BudgetOverview> budgetOverviews = generateBudgetOverviews(categories, monthYear, user);


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

            categoryPerformanceService.generateMonthlyCategoryPerformances(user.getUserId(), monthYear, categories);

            budgetPerformances.add(budgetPerformance);
        }

        if (!budgetPerformances.isEmpty()) {
            repository.saveAllAndFlush(budgetPerformances);
        }
    }

    @Override
    public BudgetPerformance fetchBudgetPerformance(MonthYear monthYear) {
        User user = userService.getCurrentAuthUser();
        return repository
                .findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(),
                        user.getUserId())
                .orElse(null);
    }

    @Override
    @Transactional
    public BudgetPerformance recalculateUserBudgetPerformance(Long userId, MonthYear monthYear) {
        User user = userService.readById(userId);
        if (user == null) {
            log.error("User not found with id {}", userId);
            throw new RuntimeException("User not found with id " + userId);
        }

        if (monthYear == null || monthYear.getMonth() == null) {
            throw new IllegalArgumentException("MonthYear must be provided for budget performance recalculation");
        }

        log.info("Recalculating BudgetPerformance entity for UserId {} and MonthYear {}", user.getUserId(), monthYear);

        BudgetPerformance existingPerformance = repository.findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(
                monthYear.getMonth(), monthYear.getYear(), user.getUserId()).orElse(null);
        if (existingPerformance != null) {
            log.info("Deleting existing BudgetPerformance entity for UserId {} and MonthYear {}", user.getUserId(), monthYear);
            repository.delete(existingPerformance);
            repository.flush();
        }

        LocalDate asOfDate = monthYear.toEndOfMonthDate();
        List<Category> categories = categoryService.findAllEntities(asOfDate);
        HashMap<OverviewType, BudgetOverview> budgetOverviews = generateBudgetOverviews(categories, monthYear, user);

        BudgetPerformance newPerformance = new BudgetPerformance();
        BudgetPerformanceId id = BudgetPerformanceId.builder()
                .monthYear(monthYear)
                .userId(user.getUserId())
                .build();
        newPerformance.setId(id);

        budgetOverviews.forEach((overviewType, budgetOverview) -> {
            switch (overviewType) {
                case NEEDS -> newPerformance.setNeedsOverview(budgetOverview);
                case WANTS -> newPerformance.setWantsOverview(budgetOverview);
                case INVESTMENTS -> newPerformance.setInvestmentOverview(budgetOverview);
                case GENERAL -> newPerformance.setGeneralOverview(budgetOverview);
            }
        });

        categoryPerformanceService.generateMonthlyCategoryPerformances(user.getUserId(), monthYear, categories);

        return repository.saveAndFlush(newPerformance);
    }

    public HashMap<OverviewType, BudgetOverview> generateBudgetOverviews(List<Category> userCategories,
            MonthYear monthYear, User user) {
        LocalDate asOfDate = monthYear.toEndOfMonthDate();

        String categoryIds = userCategories.stream()
                .map(category -> String.valueOf(category.getCategoryId()))
                .collect(Collectors.joining(", "));
        log.info("Generating Budget Overviews for the Categories [{}] and the Month {} and Year {} asOf {}",
                categoryIds, monthYear.getMonth(), monthYear.getYear(), asOfDate);

        HashMap<OverviewType, BudgetOverview> budgetOverviews = new HashMap<>();
        List<OverviewType> overviewTypes = List.of(OverviewType.GENERAL, OverviewType.NEEDS, OverviewType.WANTS, OverviewType.INVESTMENTS);

        for(OverviewType overviewType: overviewTypes) {
            List<Category> filteredCategories = new ArrayList<>(userCategories);

            //Filer Categories for Overview Type
            if(overviewType != OverviewType.GENERAL) {
                String categoryTypeName = GeneralUtil.nullSafeToLowerCaseOrEmpty(overviewType.getType());
                filteredCategories = userCategories.stream().filter(category -> {
                    CategoryVt catVt = effectivityService.getActiveVt(category.getValidTimes(), asOfDate);
                    CategoryType catType = catVt.getCategoryType();
                    CategoryTypeVt catTypeVt = catType != null ? effectivityService.getActiveVt(catType.getValidTimes(), asOfDate) : null;
                    return catTypeVt != null && categoryTypeName.equals(GeneralUtil.nullSafeToLowerCaseOrEmpty(catTypeVt.getName()));
                }).toList();
                String filteredCategoryIds = filteredCategories.stream()
                        .map(category -> String.valueOf(category.getCategoryId()))
                        .collect(Collectors.joining(", "));
                log.info("Filtered Category Ids for Overview Type {} : [{}]", overviewType.getType(), filteredCategoryIds);
            }

            //Calculate Total Amount Allocated to Budget for CategoryType
            double totalAmountBudgeted = filteredCategories.stream()
                    .mapToDouble(cat -> {
                        CategoryVt catVt = effectivityService.getActiveVt(cat.getValidTimes(), asOfDate);
                        return catVt.getBudgetAmount();
                    })
                    .sum();

            //Calculate Total Amount Spent on Budget for CategoryType
            double totalAmountSpent = filteredCategories.stream()
                    .map(category -> Optional.ofNullable(transactionService.fetchCategoryTransactions(category.getCategoryId(), asOfDate)).orElse(Collections.emptyList()))
                    .flatMap(List::stream)
                    .filter(transaction -> GeneralUtil.isDateInMonthAndYear(transaction.getDate(), monthYear))
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            //Calculate Budget Utilization
            double totalBudgetUtilization = totalAmountSpent != 0 ? totalAmountSpent / totalAmountBudgeted : 0;
            if (totalBudgetUtilization != 0) { totalBudgetUtilization = (double) Math.round(totalBudgetUtilization * 100) / 100; }

            //Calculate Savings
            log.info("Total Amount Budgeted {} and Total Amount Spent {}", totalAmountBudgeted, totalAmountSpent);
            double difference = totalAmountBudgeted - totalAmountSpent;
            double totalAmountSaved = calculateTotalAmountSaved(overviewType, totalAmountSpent, user, asOfDate);

            BudgetOverview budgetOverview = BudgetOverview.builder()
                    .overviewType(overviewType)
                    .totalSpent(totalAmountSpent)
                    .totalAmountAllocated(totalAmountBudgeted)
                    .totalPercentUtilized(totalBudgetUtilization)
                    .totalAmountSaved(totalAmountSaved)
                    .savedAmountAttributesTotal(difference)
                    .build();

            log.info("Generated BudgetOverview for MonthYear {} and Categories [{}] : {}", monthYear, categoryIds, budgetOverview);
            budgetOverviews.put(overviewType, budgetOverview);
        }

        return budgetOverviews;
    }

    /**
     * Functionality to calculate savings for a Category Type based on monthly budget
     *
     * @param overviewType
     *          - OverviewType to calculate savings for
     * @param totalAmountSpent
     *          - total amount spent (corresponding to single CategoryType or ALL CategoryTypes)
     * @param user
     *          - User we are calculating savings for
     * @return
     *          - total amount saved for CategoryType
     */
    public double calculateTotalAmountSaved(OverviewType overviewType, double totalAmountSpent, User user, LocalDate asOfDate) {
        log.info("Calculating the Total Amount Saved for OverviewType {} and Expenditure {} asOf {}", overviewType.name(), totalAmountSpent, asOfDate);

        //Sum of all CategoryTypes 'budget_amount_allocated' MINUS total amount spent
        if (overviewType == OverviewType.GENERAL) {
            List<CategoryType> types = Optional.ofNullable(categoryTypeService.findAllEntities(user, asOfDate)).orElse(Collections.emptyList());
            double allCategoryTypeAllocations = types.stream()
                    .mapToDouble(type -> {
                        CategoryTypeVt ctVt = effectivityService.getActiveVt(type.getValidTimes(), asOfDate);
                        return ctVt.getBudgetAmount();
                    })
                    .sum();
            log.info("Total Amount Allocated for {} Overview : {}", overviewType.name(), allCategoryTypeAllocations);
            return allCategoryTypeAllocations - totalAmountSpent;
        }

        CategoryType categoryType = categoryTypeService.findEntityByName(overviewType.name(), user, asOfDate);
        double categoryTypeAllocation = 0.0;
        if (categoryType != null) {
            CategoryTypeVt ctVt = effectivityService.getActiveVt(categoryType.getValidTimes(), asOfDate);
            categoryTypeAllocation = ctVt.getBudgetAmount();
        }
        log.info("Total Amount Allocated for {} Overview: {}", overviewType.name(), categoryTypeAllocation);
        return categoryTypeAllocation - totalAmountSpent;
    }
}
