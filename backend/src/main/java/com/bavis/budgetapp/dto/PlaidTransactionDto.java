package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Kellen Bavis
 *
 * DTO to store all the relevant information regarding a Plaid API Transaction
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaidTransactionDto {
    private String account_id;

    private String transaction_id;

    private double amount;

    private LocalDate datetime;

    private List<CounterpartyDto> counterparties;

    private PersonalFinanceCategoryDto personal_finance_category;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CounterpartyDto {
        private String logo_url;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PersonalFinanceCategoryDto {
        private String confidence_level;
        private String detailed;
        private String primary;
    }
}
