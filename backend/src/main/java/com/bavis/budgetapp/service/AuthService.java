package com.bavis.budgetapp.service;


import com.bavis.budgetapp.exception.JwtServiceException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.exception.UserServiceException;
import com.bavis.budgetapp.dto.AuthRequestDto;
import com.bavis.budgetapp.dto.AuthResponseDto;
import org.springframework.security.core.AuthenticationException;


/**
 * @author Kellen Bavis
 *
 * Service to house the functionality of our authenticaton process
 */
public interface AuthService {
    /**
     * Functionality to register a new user to our application
     *
     * @param authRequestDto
     *          - DTO to house needed information for registering a user
     * @return
     *          - registered user & newly created JWT Token
     * @throws UserServiceException
     *              - thrown in the case that our User Service experiences an error
     * @throws PlaidServiceException
     *              - thrown in the case that our Plaid Service experiences an error
     * @throws JwtServiceException
     *              - thrown in the case that our Jwt Service experiences an error
     */
    AuthResponseDto register(AuthRequestDto authRequestDto) throws UserServiceException, PlaidServiceException, JwtServiceException;

    /**
     * Functionality to authenticate a new user to our application
     *
     * @param authRequestDto
     *          - DTO to house needed information for authenticating a user
     * @return
     *          - authenticated user & newly created JWT token
     * @throws AuthenticationException
     *          - thrown in the case that our user fails authentication
     */
    AuthResponseDto authenticate(AuthRequestDto authRequestDto) throws AuthenticationException;

    //TODO: Implement javadocs once implemented
    AuthResponseDto refresh(AuthRequestDto authRequestDto);

    //TODO: Implement this logic once implemented
    AuthResponseDto logout();
}
