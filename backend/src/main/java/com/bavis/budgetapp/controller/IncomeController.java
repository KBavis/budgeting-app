package com.bavis.budgetapp.controller;

import com.bavis.budgetapp.dto.request.IncomeDto;
import com.bavis.budgetapp.dto.request.UpdateIncomeDto;
import com.bavis.budgetapp.dto.response.IncomeResponseDto;
import com.bavis.budgetapp.service.IncomeService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
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

    /**
     * Create an Income entity
     *
     * @param income
     *          - IncomeDto containing attributes necessary to create Income entity
     * @return
     *          - IncomeResponseDto containing created Income entity
     */
    @PostMapping
    public ResponseEntity<IncomeResponseDto> create(@Valid @RequestBody IncomeDto income){
        log.info("Received Income creation request for Income [{}]", income);
        return ResponseEntity.ok(_incomeService.create(income));
    }

    /**
     * Read all Income entities corresponding to authenticated user
     *
     * @param asOf
     *          - Optional point-in-time date to evaluate Income state history
     * @return
     *          - List of IncomeResponseDtos corresponding to authenticated user
     */
    @GetMapping
    public ResponseEntity<List<IncomeResponseDto>> readAll(@RequestParam(name = "asOf", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate asOf) {
        log.info("Received request to fetch all incomes corresponding to authenticated user with asOf [{}]", asOf);
        return ResponseEntity.ok(_incomeService.readAllResponseDtos(asOf));
    }

    /**
     * Update a specified Income entity
     *
     * @param incomeDto
     *          - UpdateIncomeDto containing attributes necessary to update an Income entity
     * @return
     *          - Updated IncomeResponseDto
     */
    @PatchMapping
    public ResponseEntity<IncomeResponseDto> update(@RequestBody UpdateIncomeDto incomeDto) {
        log.info("Received request to update a users income via the following RequestBody: [{}]", incomeDto);
        return ResponseEntity.ok(_incomeService.update(incomeDto));
    }

    /**
     * Delete a specified Income entity
     *
     * @param incomeId
     *          - Income ID corresponding to Income entity needing deletion
     */
    @DeleteMapping("/{incomeId}")
    public ResponseEntity<Void> delete(@PathVariable Long incomeId) {
        log.info("Received request to delete Income entity with ID {}", incomeId);
        _incomeService.delete(incomeId);
        return ResponseEntity.ok().build();
    }
}
