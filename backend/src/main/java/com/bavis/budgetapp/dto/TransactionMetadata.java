package com.bavis.budgetapp.dto;

import com.bavis.budgetapp.model.PlaidDetailedCategory;
import com.bavis.budgetapp.model.PlaidPrimaryCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionMetadata {
    private String merchant;
    private Double amount;
    @JsonProperty("date_time")
    private LocalDateTime dateTime;
    @JsonProperty("plaid_primary_category")
    private PlaidPrimaryCategory plaidPrimaryCategory;
    @JsonProperty("plaid_detailed_category")
    private PlaidDetailedCategory plaidDetailedCategory;
}
