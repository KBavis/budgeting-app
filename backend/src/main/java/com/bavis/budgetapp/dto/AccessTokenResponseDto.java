package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO to encapsulate needed information returned by Plaid API when attempting to exchange public token for access token
 *
 * @author Kellen Bavis
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenResponseDto {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("item_id")
    private String itemId;

    @JsonProperty("request_id")
    private String requestId;
}