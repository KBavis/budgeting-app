package com.bavis.budgetapp.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kellen Bavis
 *
 * DTO for storing a client user ID needed for generating Plaid Link token
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaidUserDto {

    @JsonProperty("client_user_id")
    private String client_user_id;
}
