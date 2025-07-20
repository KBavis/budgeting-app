package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaidAccountDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Balance {
        private BigDecimal available;
        private BigDecimal current;
    }

    @JsonProperty("account_id")
    private String accountId;
    private Balance balances;
}
