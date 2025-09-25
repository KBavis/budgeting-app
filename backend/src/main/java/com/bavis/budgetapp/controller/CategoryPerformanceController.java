package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.entity.analysis.MonthlyCategoryPerformance;
import com.bavis.budgetapp.model.MonthYear;
import com.bavis.budgetapp.service.MonthlyCategoryPerformanceService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/category/performance")
public class CategoryPerformanceController {

    @Autowired
    private MonthlyCategoryPerformanceService categoryPerformanceService;

    @GetMapping("/{categoryTypeId}")
    public List<MonthlyCategoryPerformance> getCategoryPerformances(@Valid @RequestBody MonthYear monthYear, @PathVariable(value = "categoryTypeId") Long categoryTypeId) {
        log.info("Retrieved request to fetch CategoryPerformances for MonthYear={} and CategoryTypeId={}", monthYear, categoryTypeId);
        return categoryPerformanceService.getPerformances(categoryTypeId, monthYear);
    }
}
