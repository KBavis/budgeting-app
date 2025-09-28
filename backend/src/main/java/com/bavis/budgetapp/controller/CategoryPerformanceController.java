package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.entity.analysis.MonthlyCategoryPerformance;
import com.bavis.budgetapp.model.MonthYear;
import com.bavis.budgetapp.service.MonthlyCategoryPerformanceService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category/performance")
public class CategoryPerformanceController {

    private static final Logger log = LoggerFactory.getLogger(CategoryPerformanceController.class);
    @Autowired
    private MonthlyCategoryPerformanceService categoryPerformanceService;

    @GetMapping("/{categoryTypeId}")
    public List<MonthlyCategoryPerformance> getCategoryPerformances(@Valid @RequestBody MonthYear monthYear, @PathVariable(value = "categoryTypeId") Long categoryTypeId) {
        log.info("Retrieved request to fetch CategoryPerformances for MonthYear={} and CategoryTypeId={}", monthYear, categoryTypeId);
        return categoryPerformanceService.getPerformances(categoryTypeId, monthYear);
    }
}
