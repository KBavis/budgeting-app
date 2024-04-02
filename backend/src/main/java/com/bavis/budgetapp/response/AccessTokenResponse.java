package com.bavis.budgetapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccessTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
}