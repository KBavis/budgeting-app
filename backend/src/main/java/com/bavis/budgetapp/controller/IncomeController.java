package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.IncomeDto;
import com.bavis.budgetapp.entity.Income;
import com.bavis.budgetapp.service.IncomeService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * Fetching of all Income entities associated with authenticated user
     *
     * @return
     *      - all Incomes associated with Auth user
     */
    @GetMapping
    public ResponseEntity<List<Income>> readAll() {
        log.info("Received request to fetch all incomes corresponding to authenticated user");
        return ResponseEntity.ok(_incomeService.readAll());
    }
}
