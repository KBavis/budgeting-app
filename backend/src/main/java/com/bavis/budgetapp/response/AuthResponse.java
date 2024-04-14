package com.bavis.budgetapp.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Class To Encapsulate Necessary Details To Return To User Upon Authenticating/Registering
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token; //jwt token
    @JsonProperty("user")

    private UserDetails userDetails; //details pertaining to authenticated user
}
