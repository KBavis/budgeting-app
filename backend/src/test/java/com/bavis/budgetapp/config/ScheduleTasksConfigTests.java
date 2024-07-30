package com.bavis.budgetapp.config;

import com.bavis.budgetapp.service.BudgetPerformanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.times;

@ActiveProfiles(profiles = "test")
public class ScheduleTasksConfigTests {

    @Mock
    BudgetPerformanceService budgetPerformanceService;

    @InjectMocks
    ScheduledTasksConfig config;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateBudgetPerformance_CallsService() {
        config.generateBudgetPerformance();
        Mockito.verify(budgetPerformanceService, times(1)).runGenerateBudgetPerformanceJob(null);
    }
}
