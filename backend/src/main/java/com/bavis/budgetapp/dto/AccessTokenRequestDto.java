package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessTokenRequestDto {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("request_id")
    private String requestId;
}