package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExchangeTokenRequestDto {
    @JsonProperty("client_id")
    private final String clientId;

    @JsonProperty("secret")
    private final String secretKey;

    @JsonProperty("public_token")
    private final String publicToken;
}
