package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.GetCategoryPerformancesRequest;
import com.bavis.budgetapp.entity.analysis.MonthlyCategoryPerformance;
import com.bavis.budgetapp.model.MonthYear;
import com.bavis.budgetapp.service.MonthlyCategoryPerformanceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
