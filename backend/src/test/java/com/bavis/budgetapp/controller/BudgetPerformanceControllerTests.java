package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.constants.OverviewType;
import com.bavis.budgetapp.entity.BudgetPerformance;
import com.bavis.budgetapp.entity.Category;
import com.bavis.budgetapp.model.BudgetOverview;
import com.bavis.budgetapp.model.BudgetPerformanceId;
import com.bavis.budgetapp.model.MonthYear;
import com.bavis.budgetapp.service.BudgetPerformanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@WithMockUser
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
public class BudgetPerformanceControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockBean
    private BudgetPerformanceService budgetPerformanceService;

    private BudgetPerformance budgetPerformance;
    private BudgetOverview generalOverview;
    private BudgetOverview needsOverview;
    private BudgetOverview wantsOverview;
    private BudgetOverview investmentOverview;
    private MonthYear monthYear;
    private Category category;
    @BeforeEach
    void setup() {
        category = Category.builder()
                .categoryId(10L)
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


        investmentOverview = BudgetOverview.builder()
                .totalPercentUtilized(.5)
                .totalSpent(1000)
                .totalAmountAllocated(2000)
                .overviewType(OverviewType.INVESTMENTS)
                .build();

        budgetPerformance = new BudgetPerformance();
        monthYear = new MonthYear("March", 2024);
        BudgetPerformanceId budgetPerformanceId = BudgetPerformanceId.builder()
                        .userId(10L)
                        .monthYear(monthYear)
                        .build();
        budgetPerformance.setId(budgetPerformanceId);
        budgetPerformance.setInvestmentOverview(investmentOverview);
        budgetPerformance.setNeedsOverview(needsOverview);
        budgetPerformance.setGeneralOverview(generalOverview);
        budgetPerformance.setWantsOverview(wantsOverview);
        budgetPerformance.setCategories(List.of(category));

        MockitoAnnotations.openMocks(this);

        objectMapper = new ObjectMapper();
    }

    @Test
    void testInvokeGenerateBudgetPerformanceJob_Success() throws Exception{
        //Mock
        doNothing().when(budgetPerformanceService).runGenerateBudgetPerformanceJob(monthYear);

        ResultActions resultActions = mockMvc.perform(post("/budget/performance")
                .content(objectMapper.writeValueAsString(monthYear))
                .contentType(MediaType.APPLICATION_JSON));

        resultActions
                .andExpect(status().isOk());

        verify(budgetPerformanceService, times(1)).runGenerateBudgetPerformanceJob(monthYear);
    }

    @Test
    void testFetchUsersBudgetPerformances_Success() throws Exception{
        //Arrange
        List<BudgetPerformance> expectedUserBudgetPerformances = List.of(budgetPerformance);

        //Mock
        when(budgetPerformanceService.fetchBudgetPerformances()).thenReturn(expectedUserBudgetPerformances);

        //Act
        ResultActions resultActions = mockMvc.perform(get("/budget/performance/all"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].categories[0].categoryId").value(category.getCategoryId()))
                .andExpect(jsonPath("$[0].generalOverview").value(budgetPerformance.getGeneralOverview()))
                .andExpect(jsonPath("$[0].needsOverview").value(budgetPerformance.getNeedsOverview()))
                .andExpect(jsonPath("$[0].investmentOverview").value(budgetPerformance.getInvestmentOverview()))
                .andExpect(jsonPath("$[0].wantsOverview").value(budgetPerformance.getWantsOverview()));

    verify(budgetPerformanceService, times(1)).fetchBudgetPerformances();
    }

    @Test
    void testFetchUsersBudgetPerformance_NoBudget() throws Exception {
        //Mock
        when(budgetPerformanceService.fetchBudgetPerformances()).thenReturn(Collections.emptyList());

        //Act
        ResultActions resultActions = mockMvc.perform(get("/budget/performance/all"));

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(budgetPerformanceService, times(1)).fetchBudgetPerformances();
    }


    @Test
    void testFetchBudgetPerformance_NoBudget() throws Exception {
        //Mock
        when(budgetPerformanceService.fetchBudgetPerformance(monthYear)).thenReturn(null);

        //Act
        ResultActions resultActions = mockMvc.perform(get("/budget/performance")
                .content(objectMapper.writeValueAsString(monthYear))
                .contentType(MediaType.APPLICATION_JSON));

        resultActions
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(budgetPerformanceService, times(1)).fetchBudgetPerformance(monthYear);
    }

    @Test
    void testFetchBudgetPerformance_Success() throws Exception {
        //Mock
        when(budgetPerformanceService.fetchBudgetPerformance(monthYear)).thenReturn(budgetPerformance);

        //Act
        ResultActions resultActions = mockMvc.perform(get("/budget/performance")
                .content(objectMapper.writeValueAsString(monthYear))
                .contentType(MediaType.APPLICATION_JSON));

        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categories[0].categoryId").value(category.getCategoryId()))
                .andExpect(jsonPath("$.generalOverview").value(budgetPerformance.getGeneralOverview()))
                .andExpect(jsonPath("$.needsOverview").value(budgetPerformance.getNeedsOverview()))
                .andExpect(jsonPath("$.investmentOverview").value(budgetPerformance.getInvestmentOverview()))
                .andExpect(jsonPath("$.wantsOverview").value(budgetPerformance.getWantsOverview()));

        verify(budgetPerformanceService, times(1)).fetchBudgetPerformance(monthYear);
    }
}
