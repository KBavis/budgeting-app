package com.bavis.budgetapp.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LinkTokenResponse {
    @JsonProperty("link_token")
    private String linkToken;

    @JsonProperty("expiration")
    LocalDateTime expiration;

    @JsonProperty("request_id")
    String requestId;
}
