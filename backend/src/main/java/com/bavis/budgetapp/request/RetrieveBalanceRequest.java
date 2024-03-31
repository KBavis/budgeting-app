package com.bavis.budgetapp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RetrieveBalanceRequest {
    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("secret")
    private String secret;

    @JsonProperty("access_token")
    private String accessToken;
}
