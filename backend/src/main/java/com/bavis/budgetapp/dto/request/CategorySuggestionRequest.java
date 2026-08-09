package com.bavis.budgetapp.dto.request;

import com.bavis.budgetapp.dto.response.TransactionMetadata;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySuggestionRequest {
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("transaction")
    private TransactionMetadata transactionMetadata;
}
