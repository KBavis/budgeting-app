package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UncategorizedSuggestionResponse {
    private String type;
    private List<String> reasons;
    @JsonProperty("suggested_actions")
    private List<String> suggestedActions;
}
