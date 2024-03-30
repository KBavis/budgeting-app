package com.bavis.budgetapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccessTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
}