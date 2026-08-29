package com.bavis.budgetapp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRemovalRequestDto {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("secret")
    private String secret;

    @JsonProperty("access_token")
    @NotEmpty(message = "accessToken must not be empty")
    private String accessToken;
}
