package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.constants.Role;
import com.bavis.budgetapp.exception.JwtServiceException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.exception.UserServiceException;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.dto.AuthRequestDto;
import com.bavis.budgetapp.dto.AuthResponseDto;
import com.bavis.budgetapp.model.LinkToken;
import com.bavis.budgetapp.service.AuthService;
import com.bavis.budgetapp.service.JwtService;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author Kellen Bavis
 *
 * Implementation of our Authentication Service functionality
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final JwtService _jwtService;
    private final UserService _userService;

    private final PasswordEncoder _passwordEncoder;

    private final PlaidService _plaidService;

    private final AuthenticationManager _authenticationManager;



    @Override
    @Transactional
    public AuthResponseDto register(AuthRequestDto authRequestDto) throws UserServiceException, PlaidServiceException, JwtServiceException {
        log.info("Attempting to Register new user pertaining to AuthRequestDto: [{}]", authRequestDto);

        User user = User.builder()
                .name(authRequestDto.getName())
                .username(authRequestDto.getUsername())
                .password(_passwordEncoder.encode(authRequestDto.getPasswordOne()))
                .role(Role.USER) //TODO: Consider having seperate roles
                .profileImage(null)
                .accounts(new ArrayList<>())
                .categories(new ArrayList<>())
                .incomes(new ArrayList<>())
                .linkToken(null)
                .build();

        log.info("Registered User: [" + _userService.create(user) + "]");

        //Generate Plaid Link Token for authenticated user
        LinkToken linkToken = _plaidService.generateLinkToken(user.getUserId());
        user.setLinkToken(linkToken);
        log.debug("Link Token Generated for User {} : {}", user.getUserId(), linkToken);

        //Ensure Plaid Link Token persisted
        user = _userService.update(user.getUserId(), user);
        log.info("User Following Plaid Link Token Generation: [{}]", user.toString());

        //Generate JWT Token for newly registered user
        String jwtToken = _jwtService.generateToken(user);

        //Return response containing JWT Token & persisted user
        return AuthResponseDto.builder()
                .token(jwtToken)
                .userDetails(user)
                .build();

    }

    @Override
    public AuthResponseDto authenticate(AuthRequestDto authRequestDto) throws AuthenticationException{
        log.info("Attempting to Authenticate an user via following AuthRequestDto: [{}]", authRequestDto);

        //Authenticate User using our AuthenticationManager Bean
        _authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDto.getUsername(),
                        authRequestDto.getPasswordOne()
                )
        );
        log.debug("Successfully Authenticated User: [{}]", authRequestDto.getUsername());

        //Generate JWT Token For Authenticated User
        User user = _userService.readByUsername(authRequestDto.getUsername());
        String jwtToken = _jwtService.generateToken(user);

        //Return response containing JWT Token & persisted user
        return AuthResponseDto.builder()
                .token(jwtToken)
                .userDetails(user)
                .build();
    }

    @Override
    public AuthResponseDto refresh(AuthRequestDto authRequestDto) {
        return null;
    }

    @Override
    public void logout() {
        log.info("Attempting to logout the currently authenticated user via AuthService");
        //Remove AuthenticationToken from SecurityContext
        SecurityContextHolder.getContext().setAuthentication(null);

        //TODO: Invalidate JWT Token (BA-495)
    }

}
