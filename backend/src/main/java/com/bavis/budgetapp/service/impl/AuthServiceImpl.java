package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.constants.Role;
import com.bavis.budgetapp.exception.JwtServiceException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.exception.UserServiceException;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.dto.AuthRequestDto;
import com.bavis.budgetapp.dto.AuthResponseDto;
import com.bavis.budgetapp.service.AuthService;
import com.bavis.budgetapp.service.JwtService;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.service.UserService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final JwtService _jwtService;
    private final UserService _userService;

    private final PasswordEncoder _passwordEncoder;

    private final PlaidService _plaidService;

    private final AuthenticationManager _authenticationManager;

    public AuthServiceImpl(JwtService _jwtService, UserService _userService, PasswordEncoder _passwordEncoder, AuthenticationManager _authenticationManager, PlaidService _plaidService){
        this._jwtService = _jwtService;
        this._userService = _userService;
        this._authenticationManager = _authenticationManager;
        this._passwordEncoder = _passwordEncoder;
        this._plaidService = _plaidService;
    }


    /**
     * Register user within our application
     *
     * @param authRequestDto
     *          - Authentication Request sent to server
     * @return
     * `        - AuthResposne containing UserDetails and JWT Token
     * @throws RuntimeException
     *          - Exception for any potential BadRegistrationException, PlaidServiceException, JWTException
     */


    @Override
    @Transactional
    public AuthResponseDto register(AuthRequestDto authRequestDto) throws UserServiceException, PlaidServiceException, JwtServiceException {

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

        LOG.info("Registered User: [" + _userService.create(user) + "]");

        //Generate Plaid Link Token for authenticated user
        String linkToken = _plaidService.generateLinkToken(user.getUserId());
        user.setLinkToken(linkToken);
        LOG.debug("Link Token Generated for User {} : {}", user.getUserId(), linkToken);

        //Ensure Plaid Link Token persisted
        user = _userService.update(user.getUserId(), user);
        LOG.info("User Following Plaid Link Token Generation: [{}]", user.toString());

        String jwtToken = _jwtService.generateToken(user);
        return AuthResponseDto.builder()
                .token(jwtToken)
                .userDetails(user)
                .build();

    }

    @Override
    public AuthResponseDto authenticate(AuthRequestDto authRequestDto) throws AuthenticationException{

        //Authenticate User using our AuthenticationManager Bean
        _authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDto.getUsername(),
                        authRequestDto.getPasswordOne()
                )
        );

        //Generate JWT Token For Authenticated User
        User user = _userService.readByUsername(authRequestDto.getUsername());
        String jwtToken = _jwtService.generateToken(user);
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
    public AuthResponseDto logout() {
        return null;
    }

}
