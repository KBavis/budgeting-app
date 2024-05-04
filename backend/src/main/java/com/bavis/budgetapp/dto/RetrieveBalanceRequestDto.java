package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 *
 * DTO for encapsulating needed information to retreive a User's balance from their account
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RetrieveBalanceRequestDto {
    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("secret")
    private String secret;

    @JsonProperty("access_token")
    private String accessToken;
}
