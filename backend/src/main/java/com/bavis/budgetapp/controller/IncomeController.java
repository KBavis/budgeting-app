package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.entity.Income;
import com.bavis.budgetapp.service.IncomeService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kellen Bavis
 *
 * Controller for working with Income entities
 */
@RestController
@Log4j2
@RequestMapping("/income")
public class IncomeController {

    private final IncomeService _incomeService;

    public IncomeController(IncomeService _incomeService){
        this._incomeService = _incomeService;
    }

    //TODO: Consider adding multiple incomes at once

    /**
     * Creation of a single income
     *
     * @param income
     *          - Income to create
     * @return
     *          - newly created Income
     */
    @PostMapping
    public ResponseEntity<Income> create(@Valid @RequestBody IncomeDto income){
        log.info("Received Income creation request for Income [{}]", income);
        return ResponseEntity.ok(_incomeService.create(income));
    }
}
