package com.bavis.budgetapp.service.impl;

import com.bavis.budgetapp.enumeration.Role;
import com.bavis.budgetapp.exception.BadRegistrationRequestException;
import com.bavis.budgetapp.exception.UsernameTakenException;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.request.AuthRequest;
import com.bavis.budgetapp.response.AuthResponse;
import com.bavis.budgetapp.service.AuthService;
import com.bavis.budgetapp.service.JwtService;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
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


    @Override
    public AuthResponse register(AuthRequest authRequest) {

        //Validate that the username is indeed unique
        String username = authRequest.getUsername();
        if(_userService.existsByUsername(authRequest.getUsername())){
            throw new UsernameTakenException(authRequest.getUsername());
        }

        //Validate that the user filled out the required fields
        if(username == null || username.isEmpty()) {
            throw new BadRegistrationRequestException("Username field was not filled out.");
        }
        else if(!authRequest.getPasswordOne().equals(authRequest.getPasswordTwo())){
            throw new BadRegistrationRequestException("Password fields do not match.") ;
        }

        //TODO: Consider introducing necessary inclusion for password strength (i.e minimunm 10 letters, must contain uppercase/lowercase, etc)

        User user = User.builder()
                .name(authRequest.getName())
                .username(authRequest.getUsername())
                .password(_passwordEncoder.encode(authRequest.getPasswordOne()))
                .role(Role.USER) //TODO: Consider having seperate roles
                .profileImage(null)
                .accounts(new ArrayList<>())
                .categories(new ArrayList<>())
                .linkToken(null) //TODO: Figure out how to set this upon setting up a users account
                .build();

        LOG.info("Registered User: [" + _userService.create(user) + "]");

        //Generate Plaid Link Token for authenticated user
        //TODO: Determine if you need to save this user again to persist the link token
        String linkToken = _plaidService.generateLinkToken(user.getUserId());
        user.setLinkToken(linkToken);




        //TODO: Validate this logic regarding User Details
        String jwtToken = _jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .userDetails(user)
                .build();

    }

    @Override
    public AuthResponse authenticate(AuthRequest authRequest) {
        return null;
    }

    @Override
    public AuthResponse refresh(AuthRequest authRequest) {
        return null;
    }

    @Override
    public AuthResponse logout() {
        return null;
    }
}
