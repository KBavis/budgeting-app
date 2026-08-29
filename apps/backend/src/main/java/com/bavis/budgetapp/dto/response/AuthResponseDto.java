package com.bavis.budgetapp.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Kellen Bavis
 *
 * DTO To Encapsulate Necessary Details To Return To User Upon Authenticating/Registering
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {
    private String token; //jwt token
    @JsonProperty("user")
    private UserDetails userDetails; //details pertaining to authenticated user
}
