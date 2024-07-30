package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.entity.BudgetPerformance;
import com.bavis.budgetapp.model.MonthYear;
import com.bavis.budgetapp.service.BudgetPerformanceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/budget/performance")
public class BudgetPerformanceController {

    @Autowired
    private BudgetPerformanceService budgetPerformanceService;

    /**
     * Fetch a BudgetPerformance pertaining to a specific Month/Year
     *
     * @param monthYear
     *          - unique month/year to fetch BudgetPerformance for
     * @return
     *          - persisted BudgetPerformance
     */
    @GetMapping
    public BudgetPerformance fetchBudgetPerformance(@RequestBody MonthYear monthYear) {
        log.info("Fetching BudgetPerformance entity for the MonthYear {}", monthYear);
        return budgetPerformanceService.fetchBudgetPerformance(monthYear);
    }

    /**
     * Invoke the BudgetPerformance generation job for a specific Month/Year
     *
     * @param monthYear
     *          - month/year to generate BudgetPerformance for
     */
    @PostMapping
    public void invokeGenerateBudgetPerformanceJob(@RequestBody MonthYear monthYear) {
        log.info("Invoking the GenerateBudgetPerformanceJob for the MonthYear {}", monthYear);
        budgetPerformanceService.runGenerateBudgetPerformanceJob(monthYear);
    }

    /**
     * Fetch all BudgetPerformances pertaining to a specific User
     *
     * @return
     *      - all BudgetPerformance entities corresponding to auth user
     */
    @GetMapping("/all")
    public List<BudgetPerformance> fetchUsersBudgetPerformances() {
        log.info("Fetching the BudgetPerformance entities for current authenticated user");
        return budgetPerformanceService.fetchBudgetPerformances();
    }
}
