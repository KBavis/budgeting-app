package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Kellen Bavis
 *
 * DTO to encapsulate response from Plaid API when generating Link Token
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkTokenResponseDto {
    @JsonProperty("link_token")
    private String linkToken;

    @JsonProperty("expiration")
    LocalDateTime expiration;

    @JsonProperty("request_id")
    String requestId;
}
