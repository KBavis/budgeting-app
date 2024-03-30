package com.bavis.budgetapp.request;

import com.bavis.budgetapp.dto.PlaidUserDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

@Data
@Builder
public class LinkTokenRequest {

    @JsonProperty("client_id")
    private final String clientId;

    @JsonProperty("secret")
    private final String secretKey;

    @JsonProperty("client_name")
    private String clientName;

    @JsonProperty("country_codes")
    private String[] countryCodes;

    private String language;

    private PlaidUserDTO user;

    private String[] products;
}
