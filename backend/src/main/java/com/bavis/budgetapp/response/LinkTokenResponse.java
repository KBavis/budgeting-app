package com.bavis.budgetapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LinkTokenResponse {
    @JsonProperty("link_token")
    private String linkToken;
}
