package com.bavis.budgetapp.services;

import com.bavis.budgetapp.constants.OverviewType;
import com.bavis.budgetapp.dao.BudgetPerformanceRepository;
import com.bavis.budgetapp.entity.BudgetPerformance;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.model.BudgetOverview;
import com.bavis.budgetapp.model.BudgetPerformanceId;
import com.bavis.budgetapp.model.MonthYear;
import com.bavis.budgetapp.service.CategoryTypeService;
import com.bavis.budgetapp.service.TransactionService;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.service.impl.BudgetPerformanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles(profiles = "test")
public class BudgetPerformanceServiceTests {
    @Mock
    private BudgetPerformanceRepository budgetPerformanceRepository;

    @Mock
    private UserService userService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private CategoryTypeService categoryTypeService;

    @InjectMocks
    @Spy
    private BudgetPerformanceServiceImpl budgetPerformanceService;

    private BudgetPerformanceServiceImpl mockBudgetPerformanceService;

    private User user;
    private BudgetPerformance budgetPerformance;
    private BudgetOverview generalOverview;
    private BudgetOverview needsOverview;
    private BudgetOverview wantsOverview;
    private BudgetOverview investmentOverview;
    private MonthYear monthYear;
    private Category category;
    private CategoryType categoryType;
    private ArgumentCaptor<List<BudgetPerformance>> argumentCaptor;

    private List<Category> userCategories;
    private CategoryType needsCategoryType;

    private CategoryType wantsCategoryType;
    private CategoryType investmentsCategoryType;
    private Category needsCategory;
    private Category wantsCategory;
    private Category investmentsCategory;
    private Transaction needsTransaction;
    private Transaction wantsTransaction;
    private Transaction investmentTransaction;
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

         user = User.builder()
                 .userId(1L)
                 .build();

         categoryType = CategoryType.builder()
                 .name("Needs")
                 .build();

        category = Category.builder()
                .categoryId(10L)
                .categoryType(categoryType)
                .build();

        generalOverview = BudgetOverview.builder()
                .totalPercentUtilized(.5)
                .totalSpent(1000)
                .totalAmountAllocated(2000)
                .overviewType(OverviewType.GENERAL)
                .build();


        needsOverview = BudgetOverview.builder()
                .totalPercentUtilized(.5)
                .totalSpent(1000)
                .totalAmountAllocated(2000)
                .overviewType(OverviewType.NEEDS)
                .build();


        wantsOverview = BudgetOverview.builder()
                .totalPercentUtilized(.5)
                .totalSpent(1000)
                .totalAmountAllocated(2000)
                .overviewType(OverviewType.WANTS)
                .build();

        @SuppressWarnings("unchecked")
        Class<List<BudgetPerformance>> budgetPerformanceListKlass = (Class<List<BudgetPerformance>>)(Class)List.class;
        argumentCaptor = ArgumentCaptor.forClass(budgetPerformanceListKlass);

        investmentOverview = BudgetOverview.builder()
                .totalPercentUtilized(.5)
                .totalSpent(1000)
                .totalAmountAllocated(2000)
                .overviewType(OverviewType.INVESTMENTS)
                .build();

        budgetPerformance = new BudgetPerformance();
        monthYear = new MonthYear("MARCH", 2024);
        BudgetPerformanceId budgetPerformanceId = BudgetPerformanceId.builder()
                .userId(user.getUserId())
                .monthYear(monthYear)
                .build();
        budgetPerformance.setId(budgetPerformanceId);
        budgetPerformance.setInvestmentOverview(investmentOverview);
        budgetPerformance.setNeedsOverview(needsOverview);
        budgetPerformance.setGeneralOverview(generalOverview);
        budgetPerformance.setWantsOverview(wantsOverview);

        needsCategoryType = CategoryType.builder()
                .categoryTypeId(1L)
                .name("Needs")
                .savedAmount(500.0)
                .budgetAmount(2000.0)
                .build();

        wantsCategoryType = CategoryType.builder()
                .categoryTypeId(2L)
                .name("Wants")
                .savedAmount(1000.0)
                .budgetAmount(1000.0)
                .build();

        investmentsCategoryType = CategoryType.builder()
                .categoryTypeId(3L)
                .name("Investments")
                .savedAmount(600.0)
                .budgetAmount(1000.0)
                .build();

        needsCategory = Category.builder()
                .categoryId(1L)
                .categoryType(needsCategoryType)
                .budgetAmount(1000)
                .build();

        wantsCategory =  Category.builder()
                .categoryId(2L)
                .categoryType(wantsCategoryType)
                .budgetAmount(2000)
                .build();

        investmentsCategory = Category.builder()
                .categoryId(3L)
                .categoryType(investmentsCategoryType)
                .budgetAmount(3000)
                .build();

        needsTransaction = Transaction.builder()
                .amount(500)
                .date(LocalDate.of(2024, 3, 20))
                .build();

        wantsTransaction = Transaction.builder()
                .amount(1000)
                .date(LocalDate.of(2024, 3, 20))
                .build();

        investmentTransaction = Transaction.builder()
                .amount(3000)
                .date(LocalDate.of(2024, 3, 20))
                .build();

        userCategories = List.of(needsCategory, wantsCategory, investmentsCategory);
    }

    @Test
    void testFetchBudgetPerformances_Success() {
        //Arrange
        List<BudgetPerformance> budgetPerformances = List.of(budgetPerformance);

        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(budgetPerformanceRepository.findById_UserId(user.getUserId())).thenReturn(budgetPerformances);

        //Act
        List<BudgetPerformance> actualBudgetPerformances = budgetPerformanceService.fetchBudgetPerformances();

        //Assert
        assertEquals(budgetPerformances, actualBudgetPerformances);

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(budgetPerformanceRepository, times(1)).findById_UserId(user.getUserId());
    }

    @Test
    void testFetchBudgetPerformances_Null() {
        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(budgetPerformanceRepository.findById_UserId(user.getUserId())).thenReturn(null);

        //Act
        List<BudgetPerformance> actualBudgetPerformances = budgetPerformanceService.fetchBudgetPerformances();

        //Assert
        assertNull(actualBudgetPerformances);

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(budgetPerformanceRepository, times(1)).findById_UserId(user.getUserId());
    }

    @Test
    void testFetchBudgetPerformance_Success(){
        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(budgetPerformanceRepository
                .findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId()))
                .thenReturn(budgetPerformance);

        //Act
        BudgetPerformance actualBudgetPerformance = budgetPerformanceService.fetchBudgetPerformance(monthYear);

        //Assert
        assertEquals(budgetPerformance, actualBudgetPerformance);

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(budgetPerformanceRepository, times(1)).findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId());
    }

    @Test
    void testFetchBudgetPerformance_Null() {
        //Mock
        when(userService.getCurrentAuthUser()).thenReturn(user);
        when(budgetPerformanceRepository
                .findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId()))
                .thenReturn(null);

        //Act
        BudgetPerformance actualBudgetPerformance = budgetPerformanceService.fetchBudgetPerformance(monthYear);

        //Assert
        assertNull(actualBudgetPerformance);

        //Verify
        verify(userService, times(1)).getCurrentAuthUser();
        verify(budgetPerformanceRepository, times(1)).findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId());
    }

    @Test
    @DisplayName("Test runGenerateBudgetPerformanceJob utilizes current month & year when no MonthYear passed")
    void testRunGenerateBudgetPerformanceJob_NullMonthYear_UsesCurrent() {
        //Arrange
        HashMap<OverviewType, BudgetOverview> budgetOverviews = new HashMap<>();
        budgetOverviews.put(OverviewType.GENERAL, generalOverview);
        budgetOverviews.put(OverviewType.INVESTMENTS, investmentOverview);
        budgetOverviews.put(OverviewType.WANTS, wantsOverview);
        budgetOverviews.put(OverviewType.NEEDS, needsOverview);

        List<Category> categories = List.of(category);
        user.setCategories(categories);

        LocalDate currentDate = LocalDate.now();
        String expectedMonth = currentDate.getMonth().name();
        int expectedYear = currentDate.getYear();

        //Mock
        when(userService.readAll()).thenReturn(List.of(user));
        doReturn(budgetOverviews).when(budgetPerformanceService)
                .generateBudgetOverviews(any(), any(MonthYear.class), any());
        when(budgetPerformanceRepository
                .findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(expectedMonth, expectedYear, user.getUserId()))
                .thenReturn(null);


        //Act
        budgetPerformanceService.runGenerateBudgetPerformanceJob(null);

        //Verify & Capture
        verify(budgetPerformanceRepository, times(1)).saveAllAndFlush(argumentCaptor.capture());

        //Assert
        List<BudgetPerformance> budgetPerformances = argumentCaptor.getValue();
        BudgetPerformance performance = budgetPerformances.get(0);
        MonthYear monthYear = performance.getId().getMonthYear();
        assertEquals(expectedMonth, monthYear.getMonth());
        assertEquals(expectedYear, monthYear.getYear());
    }

    @Test
    @DisplayName("Test runGenerateBudgetPerformanceJob utilizes MonthYear passed in")
    void testRunGenerateBudgetPerformanceJob_UsesArg() {
        //Arrange
        HashMap<OverviewType, BudgetOverview> budgetOverviews = new HashMap<>();
        budgetOverviews.put(OverviewType.GENERAL, generalOverview);
        budgetOverviews.put(OverviewType.INVESTMENTS, investmentOverview);
        budgetOverviews.put(OverviewType.WANTS, wantsOverview);
        budgetOverviews.put(OverviewType.NEEDS, needsOverview);

        List<Category> categories = List.of(category);
        user.setCategories(categories);

        //Mock
        when(userService.readAll()).thenReturn(List.of(user));
        doReturn(budgetOverviews).when(budgetPerformanceService)
                .generateBudgetOverviews(any(), any(MonthYear.class), any());
        when(budgetPerformanceRepository
                .findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId()))
                .thenReturn(null);


        //Act
        budgetPerformanceService.runGenerateBudgetPerformanceJob(monthYear);

        //Verify & Capture
        verify(budgetPerformanceRepository, times(1)).saveAllAndFlush(argumentCaptor.capture());

        //Assert
        List<BudgetPerformance> budgetPerformances = argumentCaptor.getValue();
        BudgetPerformance performance = budgetPerformances.get(0);
        MonthYear actualMonthYear = performance.getId().getMonthYear();
        assertEquals(monthYear.getMonth(), actualMonthYear.getMonth());
        assertEquals(monthYear.getYear(), actualMonthYear.getYear());
    }

    @Test
    @DisplayName("Ensure runGenerateBudgetPerformanceJob generates proper BudgetPerformanceId")
    void testRunGenerateBudgetPerformanceJob_GeneratesProperID() {
        //Arrange
        HashMap<OverviewType, BudgetOverview> budgetOverviews = new HashMap<>();
        budgetOverviews.put(OverviewType.GENERAL, generalOverview);
        budgetOverviews.put(OverviewType.INVESTMENTS, investmentOverview);
        budgetOverviews.put(OverviewType.WANTS, wantsOverview);
        budgetOverviews.put(OverviewType.NEEDS, needsOverview);

        List<Category> categories = List.of(category);
        user.setCategories(categories);

        //Mock
        when(userService.readAll()).thenReturn(List.of(user));
        doReturn(budgetOverviews).when(budgetPerformanceService)
                .generateBudgetOverviews(any(), any(MonthYear.class), any());
        when(budgetPerformanceRepository
                .findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId()))
                .thenReturn(null);


        //Act
        budgetPerformanceService.runGenerateBudgetPerformanceJob(monthYear);

        //Verify & Capture
        verify(budgetPerformanceRepository, times(1)).saveAllAndFlush(argumentCaptor.capture());

        //Assert
        List<BudgetPerformance> budgetPerformances = argumentCaptor.getValue();
        BudgetPerformance performance = budgetPerformances.get(0);
        BudgetPerformanceId id = performance.getId();
        assertEquals(monthYear, id.getMonthYear());
        assertEquals(user.getUserId(), id.getUserId());
    }

    @Test
    @DisplayName("Ensure runGenerateBudgetPerformanceJob saves expected entities")
    void testRunGenerateBudgetPerformanceJob_SavesExpectedBudgetPerformanceEntities() {
        //Arrange
        HashMap<OverviewType, BudgetOverview> budgetOverviews = new HashMap<>();
        budgetOverviews.put(OverviewType.GENERAL, generalOverview);
        budgetOverviews.put(OverviewType.INVESTMENTS, investmentOverview);
        budgetOverviews.put(OverviewType.WANTS, wantsOverview);
        budgetOverviews.put(OverviewType.NEEDS, needsOverview);

        List<Category> categories = List.of(category);
        user.setCategories(categories);

        //Mock
        when(userService.readAll()).thenReturn(List.of(user));
        doReturn(budgetOverviews).when(budgetPerformanceService)
                .generateBudgetOverviews(any(), any(MonthYear.class), any());
        when(budgetPerformanceRepository
                .findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId()))
                .thenReturn(null);


        //Act
        budgetPerformanceService.runGenerateBudgetPerformanceJob(monthYear);

        //Verify & Capture
        verify(budgetPerformanceRepository, times(1)).saveAllAndFlush(argumentCaptor.capture());

        //Assert
        List<BudgetPerformance> budgetPerformances = argumentCaptor.getValue();
        assertEquals(1, budgetPerformances.size());

        BudgetPerformance performance = budgetPerformances.get(0);
        assertEquals(budgetPerformance, performance);
    }

    @Test
    @DisplayName("Test runGenerateBudgetPerformanceJob calls expected services/functions")
    void testRunGenerateBudgetPerformanceJob_CallsExpectedServicesAndFunctions() {
        //Arrange
        HashMap<OverviewType, BudgetOverview> budgetOverviews = new HashMap<>();
        budgetOverviews.put(OverviewType.GENERAL, generalOverview);
        budgetOverviews.put(OverviewType.INVESTMENTS, investmentOverview);
        budgetOverviews.put(OverviewType.WANTS, wantsOverview);
        budgetOverviews.put(OverviewType.NEEDS, needsOverview);

        List<Category> categories = List.of(category);
        user.setCategories(categories);

        //Mock
        when(userService.readAll()).thenReturn(List.of(user));
        doReturn(budgetOverviews).when(budgetPerformanceService)
                .generateBudgetOverviews(any(), any(MonthYear.class), any());
        when(budgetPerformanceRepository
                .findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId()))
                .thenReturn(null);


        //Act
        budgetPerformanceService.runGenerateBudgetPerformanceJob(monthYear);


        //Verify
        verify(budgetPerformanceService, times(1)).generateBudgetOverviews(any(), any(), any());
        verify(budgetPerformanceRepository, times(1)).saveAllAndFlush(any());
        verify(userService, times(1)).readAll();
        verify(budgetPerformanceRepository, times(1)).saveAllAndFlush(any());
        verify(budgetPerformanceRepository, times(1)).findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId());
    }


    @Test
    @DisplayName("Test runGenerateBudgetPerformanceJob doesn't save anything when no BudgetPerformances generated")
    void testRunGenerateBudgetPerformanceJob_SkipsSave() {
        //Arrange
        List<Category> categories = List.of(category);
        user.setCategories(categories);

        //Mock
        when(userService.readAll()).thenReturn(List.of(user));
        when(budgetPerformanceRepository
                .findById_MonthYear_MonthAndId_MonthYear_YearAndId_UserId(monthYear.getMonth(), monthYear.getYear(), user.getUserId()))
                .thenReturn(new BudgetPerformance());


        //Act
        budgetPerformanceService.runGenerateBudgetPerformanceJob(monthYear);


        //Verify
        verify(budgetPerformanceRepository, times(0)).saveAllAndFlush(any());
    }
    @Test
    @DisplayName("Test generateBudgetOverviews calculates correct total amount budgeted for each overview")
    void testGenerateBudgetOverviews_CorrectTotalAmountBudgeted() {
        //Mock
        when(transactionService.fetchCategoryTransactions(needsCategory.getCategoryId())).thenReturn(List.of(needsTransaction));
        when(transactionService.fetchCategoryTransactions(wantsCategory.getCategoryId())).thenReturn(List.of(wantsTransaction));
        when(transactionService.fetchCategoryTransactions(investmentsCategory.getCategoryId())).thenReturn(List.of(investmentTransaction));
        doReturn(0.0).when(budgetPerformanceService)
                .calculateTotalAmountSaved(any(OverviewType.class), any(double.class), any());

        HashMap<OverviewType, BudgetOverview> budgetOverviews = budgetPerformanceService.generateBudgetOverviews(userCategories, monthYear, user);


        for(Map.Entry<OverviewType, BudgetOverview> entry : budgetOverviews.entrySet()) {
            BudgetOverview budgetOverview = entry.getValue();
            switch (entry.getKey()) {
                case GENERAL -> assertEquals((needsCategory.getBudgetAmount() + wantsCategory.getBudgetAmount() + investmentsCategory.getBudgetAmount()), budgetOverview.getTotalAmountAllocated());
                case NEEDS -> assertEquals(needsCategory.getBudgetAmount(), budgetOverview.getTotalAmountAllocated());
                case INVESTMENTS -> assertEquals(investmentsCategory.getBudgetAmount(), budgetOverview.getTotalAmountAllocated());
                case WANTS -> assertEquals(wantsCategory.getBudgetAmount(), budgetOverview.getTotalAmountAllocated());
                default -> fail("Unexpected BudgetOverview Type");
            }
        }
    }

    @Test
    @DisplayName("Test generateBudgetOverviews calculates correct total amount spent for each overview")
    void testGenerateBudgetOverviews_CorrectTotalAmountSpent() {
        //Mock
        when(transactionService.fetchCategoryTransactions(needsCategory.getCategoryId())).thenReturn(List.of(needsTransaction));
        when(transactionService.fetchCategoryTransactions(wantsCategory.getCategoryId())).thenReturn(List.of(wantsTransaction));
        when(transactionService.fetchCategoryTransactions(investmentsCategory.getCategoryId())).thenReturn(List.of(investmentTransaction));
        doReturn(0.0).when(budgetPerformanceService)
                .calculateTotalAmountSaved(any(OverviewType.class), any(double.class), any());

        HashMap<OverviewType, BudgetOverview> budgetOverviews = budgetPerformanceService.generateBudgetOverviews(userCategories, monthYear, user);


        for(Map.Entry<OverviewType, BudgetOverview> entry : budgetOverviews.entrySet()) {
            BudgetOverview budgetOverview = entry.getValue();
            switch (entry.getKey()) {
                case GENERAL -> assertEquals((needsTransaction.getAmount() + wantsTransaction.getAmount() + investmentTransaction.getAmount()), budgetOverview.getTotalSpent());
                case NEEDS -> assertEquals(needsTransaction.getAmount(), budgetOverview.getTotalSpent());
                case INVESTMENTS -> assertEquals(investmentTransaction.getAmount(), budgetOverview.getTotalSpent());
                case WANTS -> assertEquals(wantsTransaction.getAmount(), budgetOverview.getTotalSpent());
                default -> fail("Unexpected BudgetOverview Type");
            }
        }
    }

    @Test
    @DisplayName("Test generateBudgetOverviews calculates correct budget utilization for each overview")
    void testGenerateBudgetOverviews_CorrectUtilization() {
        //Mock
        when(transactionService.fetchCategoryTransactions(needsCategory.getCategoryId())).thenReturn(List.of(needsTransaction));
        when(transactionService.fetchCategoryTransactions(wantsCategory.getCategoryId())).thenReturn(List.of(wantsTransaction));
        when(transactionService.fetchCategoryTransactions(investmentsCategory.getCategoryId())).thenReturn(List.of(investmentTransaction));
        doReturn(0.0).when(budgetPerformanceService)
                .calculateTotalAmountSaved(any(OverviewType.class), any(double.class), any());

        HashMap<OverviewType, BudgetOverview> budgetOverviews = budgetPerformanceService.generateBudgetOverviews(userCategories, monthYear, user);

        double expectedNeedsUtilization = needsTransaction.getAmount() / needsCategory.getBudgetAmount();
        double expectedWantsUtilization = wantsTransaction.getAmount() / wantsCategory.getBudgetAmount();
        double expectedInvestmentsUtilization = investmentTransaction.getAmount() / investmentsCategory.getBudgetAmount();
        double expectedGeneralUtilization = (needsTransaction.getAmount() + wantsTransaction.getAmount() + investmentTransaction.getAmount()) /
                        (needsCategory.getBudgetAmount() + wantsCategory.getBudgetAmount() + investmentsCategory.getBudgetAmount());

        for(Map.Entry<OverviewType, BudgetOverview> entry : budgetOverviews.entrySet()) {
            BudgetOverview budgetOverview = entry.getValue();
            switch (entry.getKey()) {
                case GENERAL -> assertEquals(expectedGeneralUtilization, budgetOverview.getTotalPercentUtilized());
                case NEEDS -> assertEquals(expectedNeedsUtilization, budgetOverview.getTotalPercentUtilized());
                case INVESTMENTS -> assertEquals(expectedInvestmentsUtilization, budgetOverview.getTotalPercentUtilized());
                case WANTS -> assertEquals(expectedWantsUtilization, budgetOverview.getTotalPercentUtilized());
                default -> fail("Unexpected BudgetOverview Type");
            }
        }
    }

    @Test
    @DisplayName("Test generateBudgetOverviews with no transactions corresponding to Categories")
    void testGenerateBudgetOverviews_NoTransactionsAssociated() {
        //Mock
        when(transactionService.fetchCategoryTransactions(needsCategory.getCategoryId())).thenReturn(null);
        when(transactionService.fetchCategoryTransactions(wantsCategory.getCategoryId())).thenReturn(null);
        when(transactionService.fetchCategoryTransactions(investmentsCategory.getCategoryId())).thenReturn(null);
        doReturn(0.0).when(budgetPerformanceService)
                .calculateTotalAmountSaved(any(OverviewType.class), any(double.class), any());

        HashMap<OverviewType, BudgetOverview> budgetOverviews = budgetPerformanceService.generateBudgetOverviews(userCategories, monthYear, user);

        assertEquals(4, budgetOverviews.size());

        //Each BudgetOverview should indicate 0 money spent, and 0 % utilization
        for(Map.Entry<OverviewType, BudgetOverview> entry : budgetOverviews.entrySet()) {
            BudgetOverview budgetOverview = entry.getValue();
            assertEquals(0, budgetOverview.getTotalSpent());
            assertEquals(0, budgetOverview.getTotalPercentUtilized());
        }
    }


    @Test
    @DisplayName("Test generateBudgetOverviews generates correct HashMap")
    void testGenerateBudgetOverviews_HashMap_Correct() {
        //Mock
        when(transactionService.fetchCategoryTransactions(needsCategory.getCategoryId())).thenReturn(List.of(needsTransaction));
        when(transactionService.fetchCategoryTransactions(wantsCategory.getCategoryId())).thenReturn(List.of(wantsTransaction));
        when(transactionService.fetchCategoryTransactions(investmentsCategory.getCategoryId())).thenReturn(List.of(investmentTransaction));
        doReturn(0.0).when(budgetPerformanceService)
                .calculateTotalAmountSaved(any(OverviewType.class), any(double.class), any());

        HashMap<OverviewType, BudgetOverview> budgetOverviews = budgetPerformanceService.generateBudgetOverviews(userCategories, monthYear, user);

        assertEquals(4, budgetOverviews.size());
        assertTrue(budgetOverviews.containsKey(OverviewType.GENERAL));
        assertTrue(budgetOverviews.containsKey(OverviewType.NEEDS));
        assertTrue(budgetOverviews.containsKey(OverviewType.INVESTMENTS));
        assertTrue(budgetOverviews.containsKey(OverviewType.WANTS));
    }


    @Test
    @DisplayName("Test generateBudgetOverview calls calculateTotalAmountSaved")
    void testGenerateBudgetOverviews_CallsCalculateTotalAmountSaved() {
        //Mock
        when(transactionService.fetchCategoryTransactions(needsCategory.getCategoryId())).thenReturn(List.of(needsTransaction));
        when(transactionService.fetchCategoryTransactions(wantsCategory.getCategoryId())).thenReturn(List.of(wantsTransaction));
        when(transactionService.fetchCategoryTransactions(investmentsCategory.getCategoryId())).thenReturn(List.of(investmentTransaction));
        doReturn(50.0).when(budgetPerformanceService)
                .calculateTotalAmountSaved(any(OverviewType.class), any(double.class), any());


        HashMap<OverviewType, BudgetOverview> budgetOverviews = budgetPerformanceService.generateBudgetOverviews(userCategories, monthYear, user);


        //Each BudgetOverview should have totalSavings of $50.0
        for(Map.Entry<OverviewType, BudgetOverview> entry : budgetOverviews.entrySet()) {
            BudgetOverview budgetOverview = entry.getValue();
            assertEquals(50.0, budgetOverview.getTotalAmountSaved());
        }

        verify(budgetPerformanceService, times(4)).calculateTotalAmountSaved(any(OverviewType.class), any(double.class), any());
    }

    @Test
    @DisplayName("Test generateBudgetOverview correctly assigns savedAmountAttributesTotal equal to amount over/under budget")
    void testGenerateBudgetOverview_OverUnderBudgetCalculation() {
        //Arrange
        double totalAmountSpentGeneral = needsTransaction.getAmount() + wantsTransaction.getAmount() + investmentTransaction.getAmount();
        double totalAmountSpentNeeds =  needsTransaction.getAmount();
        double totalAmountSpentWants = wantsTransaction.getAmount();
        double totalAmountSpentInvestments = investmentTransaction.getAmount();
        double totalAmountBudgeted = wantsCategory.getBudgetAmount() + investmentsCategory.getBudgetAmount() + needsCategory.getBudgetAmount();

        //Mock
        when(transactionService.fetchCategoryTransactions(needsCategory.getCategoryId())).thenReturn(List.of(needsTransaction));
        when(transactionService.fetchCategoryTransactions(wantsCategory.getCategoryId())).thenReturn(List.of(wantsTransaction));
        when(transactionService.fetchCategoryTransactions(investmentsCategory.getCategoryId())).thenReturn(List.of(investmentTransaction));
        doReturn(50.0).when(budgetPerformanceService)
                .calculateTotalAmountSaved(any(OverviewType.class), any(double.class), any());


        HashMap<OverviewType, BudgetOverview> budgetOverviews = budgetPerformanceService.generateBudgetOverviews(userCategories, monthYear, user);


        //Each BudgetOverview should have totalSavings of $50.0
        for(Map.Entry<OverviewType, BudgetOverview> entry : budgetOverviews.entrySet()) {
            BudgetOverview budgetOverview = entry.getValue();
            OverviewType type = entry.getKey();
            switch (type) {
                case GENERAL:
                    assertEquals(totalAmountBudgeted - totalAmountSpentGeneral, budgetOverview.getSavedAmountAttributesTotal());
                    break;
                case NEEDS:
                    assertEquals(needsCategory.getBudgetAmount() - totalAmountSpentNeeds, budgetOverview.getSavedAmountAttributesTotal());
                    break;
                case INVESTMENTS:
                    assertEquals(investmentsCategory.getBudgetAmount() - totalAmountSpentInvestments, budgetOverview.getSavedAmountAttributesTotal());
                    break;
                case WANTS:
                    assertEquals(wantsCategory.getBudgetAmount() - totalAmountSpentWants, budgetOverview.getSavedAmountAttributesTotal());
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + type);
            }
        }

        verify(budgetPerformanceService, times(4)).calculateTotalAmountSaved(any(OverviewType.class), any(double.class), any());
    }

    @Test
    void testCalculateTotalAmountSaved_GeneralOverview() {
        //Arrange
        double totalAmountSpent = 456.20;
        double expectedTotalAmountSaved = needsCategoryType.getBudgetAmount() + wantsCategoryType.getBudgetAmount() + investmentsCategoryType.getBudgetAmount();
        expectedTotalAmountSaved -= totalAmountSpent;

        //Mock
        when(categoryTypeService.readAll(user)).thenReturn(List.of(needsCategoryType, wantsCategoryType, investmentsCategoryType));

        //Act
        double totalAmountSaved = budgetPerformanceService.calculateTotalAmountSaved(OverviewType.GENERAL, totalAmountSpent, user);

        //Assert
        assertEquals(expectedTotalAmountSaved, totalAmountSaved);

        //Verify
        verify(categoryTypeService, times(1)).readAll(user);
    }


    @Test
    void testCalculateTotalAmountSaved_AnyOtherOverview() {
        //Arrange
        double totalAmountSpent = 456.20;
        double expectedTotalAmountSaved = needsCategoryType.getBudgetAmount() - totalAmountSpent;

        //Mock
        when(categoryTypeService.readByName(OverviewType.NEEDS.name(), user)).thenReturn(needsCategoryType);

        //Act
        double totalAmountSaved = budgetPerformanceService.calculateTotalAmountSaved(OverviewType.NEEDS, totalAmountSpent, user);

        //Assert
        assertEquals(expectedTotalAmountSaved, totalAmountSaved);

        //Verify
        verify(categoryTypeService, times(0)).readAll(user);
        verify(categoryTypeService, times(1)).readByName(OverviewType.NEEDS.name(), user);
    }

    @Test
    void testCalculateTotalAmountSaved_NullCategoryType_ReturnsDifference() {
        //Arrange
        double totalAmountSpent = 456.20;

        //Mock
        when(categoryTypeService.readByName(OverviewType.NEEDS.name(), user)).thenReturn(null);

        //Act
        double totalAmountSaved = budgetPerformanceService.calculateTotalAmountSaved(OverviewType.NEEDS, totalAmountSpent, user);

        //Assert
        assertEquals(totalAmountSpent * -1, totalAmountSaved); //0 - 456.20 = -456.20

        //Verify
        verify(categoryTypeService, times(0)).readAll(user);
        verify(categoryTypeService, times(1)).readByName(OverviewType.NEEDS.name(), user);
    }

    @Test
    void testCalculateTotalAmountSaved_NullCategoryTypes_ReturnsZero() {
        //Arrange
        double totalAmountSpent = 456.20;

        //Mock
        when(categoryTypeService.readAll(user)).thenReturn(null);

        //Act
        double totalAmountSaved = budgetPerformanceService.calculateTotalAmountSaved(OverviewType.GENERAL, totalAmountSpent, user);

        //Assert
        assertEquals(-1 * totalAmountSpent, totalAmountSaved); //totalAmountSaved = 0 - 456.20

        //Verify
        verify(categoryTypeService, times(1)).readAll(user);
        verify(categoryTypeService, times(0)).readByName(any(String.class));
    }


    /**
     * Utility function to calculate total categoryType 'savedAmount' attribute values
     *
     * @param overviewType
     *          - categoryType to fetch budget for
     * @return
     *          - difference
     */
    private double calculateDifference(OverviewType overviewType) {
        switch (overviewType) {
            case GENERAL:
                return (needsCategory.getBudgetAmount() + wantsCategory.getBudgetAmount() + investmentsCategory.getBudgetAmount())
                        - (needsTransaction.getAmount() + wantsTransaction.getAmount() + investmentTransaction.getAmount());
            case INVESTMENTS:
                return investmentsCategory.getBudgetAmount() - investmentTransaction.getAmount();
            case WANTS:
                return wantsCategory.getBudgetAmount() - wantsTransaction.getAmount();
            case NEEDS:
                return needsCategory.getBudgetAmount() - needsTransaction.getAmount();
            default:
                throw new IllegalArgumentException("Unknown OverviewType: " + overviewType);
        }
    }


}
