package com.bavis.budgetapp.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Class To Encapsulate Necessary Details To Return To User Upon Authenticating/Registering
 */
@Getter
@RequiredArgsConstructor
@Builder
public class AuthResponse {
    private final String token; //jwt token
    @JsonProperty("user")
    private final UserDetails userDetails; //details pertaining to authenticated user
}
