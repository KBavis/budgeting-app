package com.bavis.budgetapp.services;

import com.bavis.budgetapp.dao.MonthlyCategoryPerformanceRepository;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.entity.CategoryType;
import com.bavis.budgetapp.entity.Transaction;
import com.bavis.budgetapp.entity.analysis.MerchantAnalysis;
import com.bavis.budgetapp.entity.analysis.MonthlyCategoryPerformance;
import com.bavis.budgetapp.model.MonthYear;
import com.bavis.budgetapp.service.TransactionService;
import com.bavis.budgetapp.service.impl.MonthlyCategoryPerformanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles(profiles = "test")
public class MonthlyCategoryPerformanceServiceTests {

    @Mock
    private MonthlyCategoryPerformanceRepository repository;

    @Mock
    private TransactionService transactionServiceMock;

    @InjectMocks
    @Spy
    private MonthlyCategoryPerformanceServiceImpl categoryPerformanceService;

    @Captor
    private ArgumentCaptor<List<MonthlyCategoryPerformance>> argumentCaptor;

    private List<Category> categories;
    private MonthYear monthYear;
    private Long userId;
    private Map<Long, MonthlyCategoryPerformance> map;

    @BeforeEach
    void setup() {
        categories = getCategories();
        monthYear = MonthYear.builder()
                .month("MARCH")
                .year(2024)
                .build();
        userId = 3L;
        map = new HashMap<>();
    }

    @Test
    void testGenerateMonthlyCategoryPerformances_handlesZeroOrLess_allocations() {

        // arrange
        Category zeroAllocation = Category.builder().budgetAmount(0).categoryType(new CategoryType()).categoryId(1L).build();
        List<Category> zeroAllocationList = List.of(zeroAllocation);

        // mocks
        when(categoryPerformanceService.getTotalSpend(anyList())).thenReturn(600.00);
        when(categoryPerformanceService.getCategoryTransactions(any(MonthYear.class), any(Long.class))).thenReturn(getTransactions());

        // act
        RuntimeException e = assertThrows(RuntimeException.class, () ->  categoryPerformanceService.generateMonthlyCategoryPerformances(userId, monthYear, zeroAllocationList));

        // assert
        assertEquals("Unable to calculate Category utilization due to Category allocation <= 0", e.getMessage());

    }

    @Test
    void testGenerateMonthlyCategoryPerformances_savesExpectedPerformances() {
        // arrange
        MonthlyCategoryPerformance expectedPerformance = MonthlyCategoryPerformance.builder()
                .categoryTypeId(1L)
                .categoryId(1L)
                .categoryPercentUtilization(Math.round((600 / 200) * 100.0) / 100.0)
                .totalAmountAllocated(200.0)
                .totalSpend(600.0)
                .monthYear(monthYear)
                .transactionCount(1)
                .topMerchants(getMerchantAnalysis())
                .userId(userId)
                .build();

        // mocks
        when(categoryPerformanceService.getTotalSpend(anyList())).thenReturn(600.00);
        when(categoryPerformanceService.getCategoryTransactions(monthYear, 1L)).thenReturn(getTransactions());
        when(categoryPerformanceService.getTopThreeMerchants(anyList())).thenReturn(getMerchantAnalysis());
        when(repository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // act
        categoryPerformanceService.generateMonthlyCategoryPerformances(userId, monthYear, categories);

        // assert
        verify(repository, times(1)).saveAll(argumentCaptor.capture());
        List<MonthlyCategoryPerformance> actualPerformances = argumentCaptor.getValue();

        assertEquals(1, actualPerformances.size());
        assertEquals(expectedPerformance, actualPerformances.get(0));
    }

    @Test
    void testGenerateMonthlyCategoryPerformances_skipsSaving_whenNoPerformances_generated() {
        categoryPerformanceService.generateMonthlyCategoryPerformances(userId, monthYear, Collections.emptyList());
        verify(repository, never()).saveAll(anyList());
    }

    @Test
    void testGenerateMonthlyCategoryPerformances_callsExpectedFunctions() {
        // mocks
        when(categoryPerformanceService.getTotalSpend(anyList())).thenReturn(600.00);
        when(categoryPerformanceService.getCategoryTransactions(monthYear, 1L)).thenReturn(getTransactions());
        when(categoryPerformanceService.getTopThreeMerchants(anyList())).thenReturn(getMerchantAnalysis());
        when(repository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // act
        categoryPerformanceService.generateMonthlyCategoryPerformances(userId, monthYear, categories);

        // verify
        verify(categoryPerformanceService, times(1)).getCategoryTransactions(any(MonthYear.class), any(Long.class));
        verify(categoryPerformanceService, times(1)).getTotalSpend(anyList());
        verify(categoryPerformanceService, times(1)).getTopThreeMerchants(anyList());
    }

    @Test
    void testGetTopThreeMerchants_returnsTopThreeMerchants() {

        // arrange
        MerchantAnalysis priority1 = MerchantAnalysis.builder()
                .merchantName("Sephora")
                .transactionCount(1)
                .totalSpent(90.0)
                .avgTransactionAmount(90.0)
                .build();

        MerchantAnalysis priority2 = MerchantAnalysis.builder()
                .merchantName("Dicks")
                .transactionCount(2)
                .totalSpent(60.0)
                .avgTransactionAmount(30.0)
                .build();

        MerchantAnalysis priority3 = MerchantAnalysis.builder()
                .merchantName("Dell")
                .transactionCount(2)
                .totalSpent(30.0)
                .avgTransactionAmount(15.0)
                .build();

        List<MerchantAnalysis> merchantAnalyses = categoryPerformanceService.getTopThreeMerchants(getTopThreeMerchantTransactions());

        assertEquals(3, merchantAnalyses.size());
        assertEquals(priority1, merchantAnalyses.get(0));
        assertEquals(priority2, merchantAnalyses.get(1));
        assertEquals(priority3, merchantAnalyses.get(2));
    }

    @Test
    void testGetTopThreeMerchants_usesNameField_whenNoMerchant() {
        Transaction testTransaction = Transaction.builder()
                .transactionId("1")
                .amount(400.0)
                .name("My Test")
                .build();

        List<MerchantAnalysis> merchantAnalyses = categoryPerformanceService.getTopThreeMerchants(Collections.singletonList(testTransaction));

        assertEquals(1, merchantAnalyses.size());
        assertEquals(merchantAnalyses.get(0).getMerchantName(), "My Test");
    }

    @Test
    void testGetTotalSpend_returnsExpectedAmount() {
        assertEquals(190.0, categoryPerformanceService.getTotalSpend(getTopThreeMerchantTransactions()));
    }

    @Test
    void testGetCategoryTransactions_filtersNonActiveTransactions() {
        LocalDate now = LocalDate.now();
        long categoryId = 1L;
        MonthYear currentMonthYear = MonthYear.builder()
                .month(now.getMonth().name())
                .year(now.getYear())
                .build();

        Transaction activeTransaction = Transaction.builder()
                .amount(400.0)
                .date(now)
                .build();

        Transaction nonActiveTransaction = Transaction.builder()
                .amount(400.0)
                .date(now.minusMonths(1))
                .build();

        when(transactionServiceMock.fetchCategoryTransactions(categoryId)).thenReturn(List.of(activeTransaction, nonActiveTransaction));

        List<Transaction> actualTransactions = categoryPerformanceService.getCategoryTransactions(currentMonthYear, categoryId);

        assertEquals(1, actualTransactions.size());
        assertEquals(activeTransaction, actualTransactions.get(0));
    }

    @Test
    void testGetCategoryTransactions_handlesNullTransactions() {

        when(transactionServiceMock.fetchCategoryTransactions(any(Long.class))).thenReturn(null);

        List<Transaction> actualTransactions = categoryPerformanceService.getCategoryTransactions(monthYear, 1L);
        assertEquals(Collections.emptyList(), actualTransactions);
    }


    private List<Category> getCategories() {
        CategoryType categoryType = CategoryType.builder()
                .categoryTypeId(1L)
                .build();

        Category category1 = new Category();
        category1.setCategoryId(1L);
        category1.setCategoryType(categoryType);
        category1.setBudgetAmount(200.00);

        return List.of(category1);
    }

    private List<Transaction> getTransactions() {
        Transaction transaction1 = Transaction.builder()
                .transactionId("123")
                .amount(200.0)
                .build();

        return List.of(transaction1);
    }

    private List<MerchantAnalysis> getMerchantAnalysis() {
        MerchantAnalysis merchantAnalysis1 = MerchantAnalysis.builder()
                .merchantRank(1)
                .merchantName("Dunkin'")
                .totalSpent(100.00)
                .transactionCount(4)
                .avgTransactionAmount(25.00)
                .build();

        MerchantAnalysis merchantAnalysis2 = MerchantAnalysis.builder()
                .merchantRank(2)
                .merchantName("Dicks")
                .totalSpent(200.00)
                .transactionCount(4)
                .avgTransactionAmount(25.00)
                .build();

        MerchantAnalysis merchantAnalysis3 = MerchantAnalysis.builder()
                .merchantRank(3)
                .merchantName("Yoga")
                .totalSpent(100.00)
                .transactionCount(4)
                .avgTransactionAmount(25.00)
                .build();

        return List.of(merchantAnalysis1, merchantAnalysis2, merchantAnalysis3);
    }

    private List<Transaction> getTopThreeMerchantTransactions() {
        // Dicks --> $60 in total
        Transaction t1 = Transaction.builder()
                .transactionId("1")
                .merchantName("Dicks")
                .amount(20.0)
                .build();

        Transaction t2 = Transaction.builder()
                .transactionId("2")
                .merchantName("Dicks")
                .amount(40.0)
                .build();

        // Sephora --> $90 in total
        Transaction t3 = Transaction.builder()
                .transactionId("3")
                .merchantName("Sephora")
                .amount(90.0)
                .build();

        // Dell --> $30 in total
        Transaction t4 = Transaction.builder()
                .transactionId("4")
                .merchantName("Dell")
                .amount(15.0)
                .build();

        Transaction t5 = Transaction.builder()
                .transactionId("5")
                .merchantName("Dell")
                .amount(15.0)
                .build();

        // CVS --> $ 10 in total (should be filtered out)
        Transaction t6 = Transaction.builder()
                .transactionId("6")
                .merchantName("CVS")
                .amount(10.0)
                .build();

        return List.of(t1, t2, t3, t4, t5, t6);
    }
}
