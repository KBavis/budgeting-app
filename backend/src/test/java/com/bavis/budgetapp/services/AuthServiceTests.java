package com.bavis.budgetapp.services;

import com.bavis.budgetapp.constants.Role;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.dto.AuthRequestDto;
import com.bavis.budgetapp.dto.AuthResponseDto;
import com.bavis.budgetapp.service.JwtService;
import com.bavis.budgetapp.service.PlaidService;
import com.bavis.budgetapp.service.UserService;
import com.bavis.budgetapp.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles(profiles = "test")
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

    private AuthRequestDto registerAuthRequestDto;
    private AuthRequestDto authenticateAuthRequestDto;


    @BeforeEach
    public void setup() {

        //init authService and auth request
        authService = new AuthServiceImpl(jwtService, userService, passwordEncoder, plaidService, authenticationManager);
        registerAuthRequestDto = AuthRequestDto.builder()
                .name("Test User")
                .passwordOne("password")
                .passwordTwo("password")
                .username("test-user")
                .build();
        authenticateAuthRequestDto = AuthRequestDto.builder()
                .username("username")
                .passwordOne("password")
                .build();
    }

    /**
     * Validate that our AuthService can correctly register a user
     */
    @Test
    public void testRegister_Success() {
        //Arrange
        String linkToken = "link-token";
        String encryptedPassword = "encrypted-password";
        String jwtToken = "jwt-token";
        Long userId = 10L;


        User testUser = User.builder()
                .userId(userId)
                .name(registerAuthRequestDto.getName())
                .username(registerAuthRequestDto.getUsername())
                .password(encryptedPassword)
                .role(Role.USER)
                .profileImage(null)
                .accounts(new ArrayList<>())
                .categories(new ArrayList<>())
                .linkToken(linkToken)
                .build();

        //Mock
        when(passwordEncoder.encode(registerAuthRequestDto.getPasswordOne())).thenReturn(encryptedPassword);
        when(userService.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0); //update User Id accordingly when creating user
            user.setUserId(userId);
            return user;
        });
        when(plaidService.generateLinkToken(userId)).thenReturn(linkToken);
        when(userService.update(any(Long.class), any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(jwtToken);

        //Act
        AuthResponseDto authResponseDto = authService.register(registerAuthRequestDto);

        //Assert
        assertNotNull(authResponseDto);
        assertEquals(authResponseDto.getToken(), jwtToken);
        assertEquals(authResponseDto.getUserDetails(), testUser);

        //Verify
        verify(passwordEncoder, times(1)).encode(registerAuthRequestDto.getPasswordOne());
        verify(userService, times(1)).create(any(User.class));
        verify(plaidService, times(1)).generateLinkToken(any(Long.class));
        verify(userService, times(1)).update(any(Long.class), any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }


    /**
     * Validate necessary exception is being thrown when Plaid Service fails
     */
    @Test
    public void testRegister_PlaidServiceException_Failure() {
        //Arrange
        String encryptedPassword = "encrypted-password";
        Long userId = 10L;

        //Mock
        when(passwordEncoder.encode(registerAuthRequestDto.getPasswordOne())).thenReturn(encryptedPassword);
        when(userService.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0); //update User Id accordingly when creating user
            user.setUserId(userId);
            return user;
        });
        when(plaidService.generateLinkToken(userId)).thenThrow(new PlaidServiceException("Invalid Response Code When Generating Link Token Via PlaidClient: [404]"));

        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            authService.register(registerAuthRequestDto);
        });
        assertEquals("PlaidServiceException: [Invalid Response Code When Generating Link Token Via PlaidClient: [404]]", exception.getMessage());
    }


    /**
     * Validate necessary exception is being thrown when Jwt Service fails
     */
    @Test
    public void testRegister_JwtServiceException_Failure() {
        // Arrange
        String encryptedPassword = "encrypted-password";
        String errorMessage = "Failed to Generate JWT Token: ";
        Long userId = 10L;
        User testUser = User.builder()
                .userId(userId)
                .name(registerAuthRequestDto.getName())
                .username(registerAuthRequestDto.getUsername())
                .password(encryptedPassword)
                .role(Role.USER)
                .profileImage(null)
                .accounts(new ArrayList<>())
                .categories(new ArrayList<>())
                .linkToken(null)
                .build();

        // Mock
        when(passwordEncoder.encode(registerAuthRequestDto.getPasswordOne())).thenReturn(encryptedPassword);
        when(userService.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(userId);
            return user;
        });
        when(userService.update(eq(userId), any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(1);
            return user;
        });
        when(jwtService.generateToken(any(User.class))).thenThrow(new RuntimeException(errorMessage));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(registerAuthRequestDto);
        });
        assertEquals(errorMessage, exception.getMessage());

    }

    /**
     * Validate that authenticate can handle successful scenarios
     */
    @Test
    public void testAuthenticate_Success() {
        //Arrange
        String username = authenticateAuthRequestDto.getUsername();
        String password = authenticateAuthRequestDto.getPasswordOne();
        String jwtToken = "jwt-token";
        Long userId = 0L;
        Authentication authenticatedUser = new UsernamePasswordAuthenticationToken(username, password);
        User testUser = User.builder()
                .userId(userId)
                .name("Test User")
                .username(username)
                .password(password)
                .role(Role.USER)
                .profileImage(null)
                .accounts(new ArrayList<>())
                .categories(new ArrayList<>())
                .linkToken(null)
                .build();

        //Mock
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authenticatedUser);
        when(userService.readByUsername(username)).thenReturn(testUser);
        when(jwtService.generateToken(testUser)).thenReturn(jwtToken);

        //Act
        AuthResponseDto authResponseDto = authService.authenticate(authenticateAuthRequestDto);


        //Assert
        assertNotNull(authResponseDto);
        assertEquals(authResponseDto.getToken(), jwtToken);
        assertEquals(authResponseDto.getUserDetails(), testUser);

        //Verify
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).readByUsername(username);
        verify(jwtService, times(1)).generateToken(testUser);
    }


    /**
     * Validate authenticate can correctly handle invalid credentials
     */
    @Test
    public void testAuthenticate_InvalidCredentials_Failed() {
        //Arrange
        AuthRequestDto invalidAuthRequestDto = AuthRequestDto.builder()
                .username("username")
                .passwordOne("password")
                .build();

        //Mock
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid credentials"));


        //Act & Assert
        BadCredentialsException badCredentialsException = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(invalidAuthRequestDto);
        });
        assertEquals("Invalid credentials", badCredentialsException.getMessage());

        //Verify
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(0)).readByUsername("username");
        verify(jwtService, times(0)).generateToken(any(User.class));
    }

    @Test
    void testLogout_Success() {
        //Arrange
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                null,
                null,
                null
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);

        //Act
        authService.logout();

        //Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

}
