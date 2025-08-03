package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorySuggestionResponse {
    @JsonProperty("category_id")
    private Long categoryId;
    private Double confidence;
    private String source;
    private String reasoning;
}
