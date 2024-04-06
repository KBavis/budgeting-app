package com.bavis.budgetapp.services;

import com.bavis.budgetapp.enumeration.Role;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.request.AuthRequest;
import com.bavis.budgetapp.response.AuthResponse;
import com.bavis.budgetapp.service.JwtService;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTests {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PlaidService plaidService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    public void setup() {
        authService = new AuthServiceImpl(jwtService, userService, passwordEncoder, authenticationManager, plaidService);
    }

    @Test
    public void testRegister_Success() {
        //Arrange
        String linkToken = "link-token";
        String encryptedPassword = "encrypted-password";
        String jwtToken = "jwt-token";
        Long userId = 10L;

        AuthRequest authRequest = AuthRequest.builder()
                .name("Test User")
                .passwordOne("password")
                .passwordTwo("password")
                .username("test-user")
                .build();

        User testUser = User.builder()
                .userId(userId)
                .name(authRequest.getName())
                .username(authRequest.getUsername())
                .password(encryptedPassword)
                .role(Role.USER)
                .profileImage(null)
                .accounts(new ArrayList<>())
                .categories(new ArrayList<>())
                .linkToken(linkToken)
                .build();

        //Mock
        when(userService.existsByUsername(authRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(authRequest.getPasswordOne())).thenReturn(encryptedPassword);
        when(userService.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0); //update User Id accordingly when creating user
            user.setUserId(userId);
            return user;
        });
        when(plaidService.generateLinkToken(userId)).thenReturn(linkToken);
        when(userService.update(any(Long.class), any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(jwtToken);

        //Act
        AuthResponse authResponse = authService.register(authRequest);

        //Assert
        assertNotNull(authResponse);
        assertEquals(authResponse.getToken(), jwtToken);
        assertEquals(authResponse.getUserDetails(), testUser);

        //Verify
        verify(userService, times(1)).existsByUsername(authRequest.getUsername());
        verify(passwordEncoder, times(1)).encode(authRequest.getPasswordOne());
        verify(userService, times(1)).create(any(User.class));
        verify(plaidService, times(1)).generateLinkToken(any(Long.class));
        verify(userService, times(1)).update(any(Long.class), any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }


}
