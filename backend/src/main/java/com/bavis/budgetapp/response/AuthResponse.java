package com.bavis.budgetapp.response;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Class To Encapsulate Necessary Details To Return To User Upon Authenticating/Registering
 */
@Getter
@RequiredArgsConstructor
public class AuthResponse {
    private final String token; //jwt token
    private final UserDetails userDetails; //details pertaining to authenticated user
}
