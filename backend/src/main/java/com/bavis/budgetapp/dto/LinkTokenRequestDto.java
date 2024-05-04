package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
public class LinkTokenRequestDto {

    @JsonProperty("client_id")
    private final String clientId;

    @JsonProperty("secret")
    private final String secretKey;

    @JsonProperty("client_name")
    private String clientName;

    @JsonProperty("country_codes")
    private String[] countryCodes;

    private String language;

    private PlaidUserDto user;

    private String[] products;
}
