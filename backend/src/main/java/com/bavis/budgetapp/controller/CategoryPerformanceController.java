package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.request.GetCategoryPerformancesRequest;
import com.bavis.budgetapp.entity.analysis.MonthlyCategoryPerformance;
import com.bavis.budgetapp.model.MonthYear;
import com.bavis.budgetapp.service.MonthlyCategoryPerformanceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Kellen Bavis
 */
@RestController
@Slf4j
@RequestMapping("/category/performance")
public class CategoryPerformanceController {

    @Autowired
    private MonthlyCategoryPerformanceService categoryPerformanceService;

    @PostMapping("/{categoryTypeId}")
    public List<MonthlyCategoryPerformance> getCategoryPerformances(@Valid @RequestBody MonthYear monthYear, @PathVariable(value = "categoryTypeId") Long categoryTypeId) {
        log.info("Retrieved request to fetch CategoryPerformances for MonthYear={} and CategoryTypeId={}", monthYear, categoryTypeId);
        return categoryPerformanceService.getPerformances(categoryTypeId, monthYear);
    }

    @PostMapping
    public List<MonthlyCategoryPerformance> getCategoryPerformances(@Valid @RequestBody GetCategoryPerformancesRequest request) {
        log.info("Retrieved request to fetch CategoryPerformances for MonthYear={} and CategoryTypeIds={}", request.getMonthYear(), request.getCategoryTypeIds());
        return categoryPerformanceService.getPerformances(request.getCategoryTypeIds(), request.getMonthYear());
    }

}
