package com.bavis.budgetapp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 *
 * DTO to encapsulate needed information to exchange public token for access token via Plaid API
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeTokenRequestDto {
    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("secret")
    @ToString.Exclude
    private String secretKey;

    @JsonProperty("public_token")
    @ToString.Exclude
    private String publicToken;
}
