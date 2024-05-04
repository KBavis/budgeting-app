package com.bavis.budgetapp.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author Kellen Bavis
 *
 *DTO To Encapsulate Necessary Details To Return To User Upon Authenticating/Registering
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
