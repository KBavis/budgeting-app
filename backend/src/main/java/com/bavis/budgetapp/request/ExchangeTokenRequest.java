package com.bavis.budgetapp.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeTokenRequest {
    @JsonProperty("client_id")
    private final String clientId;

    @JsonProperty("secret")
    private final String secretKey;

    @JsonProperty("public_token")
    private final String publicToken;
}
