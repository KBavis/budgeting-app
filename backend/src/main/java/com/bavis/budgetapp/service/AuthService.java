package com.bavis.budgetapp.service;


import com.bavis.budgetapp.exception.JwtServiceException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.exception.UserServiceException;
import com.bavis.budgetapp.dto.AuthRequestDto;
import com.bavis.budgetapp.dto.AuthResponseDto;
import org.springframework.security.core.AuthenticationException;


public interface AuthService {
    AuthResponseDto register(AuthRequestDto authRequestDto) throws UserServiceException, PlaidServiceException, JwtServiceException;

    AuthResponseDto authenticate(AuthRequestDto authRequestDto) throws AuthenticationException;

    AuthResponseDto refresh(AuthRequestDto authRequestDto);

    AuthResponseDto logout();
}
