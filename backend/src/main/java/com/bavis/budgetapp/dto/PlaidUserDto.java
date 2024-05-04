package com.bavis.budgetapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
