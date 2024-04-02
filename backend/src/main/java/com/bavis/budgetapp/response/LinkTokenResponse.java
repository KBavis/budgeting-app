package com.bavis.budgetapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LinkTokenResponse {
    @JsonProperty("link_token")
    private String linkToken;
}
