package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * @author Kellen Bavis
 *
 * DTO for encapsulating information needed to generate a link token via Plaid API in order to access Plaid Link on frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkTokenRequestDto {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("secret")
    private String secretKey;

    @JsonProperty("client_name")
    private String clientName;

    @JsonProperty("country_codes")
    private String[] countryCodes;

    private String language;

    private PlaidUserDto user;

    private String[] products;
}
