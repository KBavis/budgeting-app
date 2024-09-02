package com.bavis.budgetapp.config;

import com.bavis.budgetapp.service.BudgetPerformanceService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Configuration class used to set up scheduled tasks
 *
 */
@Component
@Log4j2
@EnableScheduling
public class ScheduledTasksConfig {

    @Autowired
    private BudgetPerformanceService budgetPerformanceService;

    /**
     * Generate User's BudgetPerformances on the first day of every month
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void generateBudgetPerformance() {
        log.info("Executing monthly Budget Performance job");
        budgetPerformanceService.runGenerateBudgetPerformanceJob(null);
    }
}
