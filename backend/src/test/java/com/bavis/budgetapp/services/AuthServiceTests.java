package com.bavis.budgetapp.services;

import com.bavis.budgetapp.enumeration.Role;
import com.bavis.budgetapp.exception.BadAuthenticationRequest;
import com.bavis.budgetapp.exception.BadRegistrationRequestException;
import com.bavis.budgetapp.exception.PlaidServiceException;
import com.bavis.budgetapp.exception.UsernameTakenException;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.request.AuthRequest;
import com.bavis.budgetapp.response.AuthResponse;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private AuthRequest registerAuthRequest;
    private AuthRequest authenticateAuthRequest;


    @BeforeEach
    public void setup() {
        //init authService and auth request
        authService = new AuthServiceImpl(jwtService, userService, passwordEncoder, authenticationManager, plaidService);
        registerAuthRequest = AuthRequest.builder()
                .name("Test User")
                .passwordOne("password")
                .passwordTwo("password")
                .username("test-user")
                .build();
        authenticateAuthRequest = AuthRequest.builder()
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
                .name(registerAuthRequest.getName())
                .username(registerAuthRequest.getUsername())
                .password(encryptedPassword)
                .role(Role.USER)
                .profileImage(null)
                .accounts(new ArrayList<>())
                .categories(new ArrayList<>())
                .linkToken(linkToken)
                .build();

        //Mock
        when(userService.existsByUsername(registerAuthRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerAuthRequest.getPasswordOne())).thenReturn(encryptedPassword);
        when(userService.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0); //update User Id accordingly when creating user
            user.setUserId(userId);
            return user;
        });
        when(plaidService.generateLinkToken(userId)).thenReturn(linkToken);
        when(userService.update(any(Long.class), any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn(jwtToken);

        //Act
        AuthResponse authResponse = authService.register(registerAuthRequest);

        //Assert
        assertNotNull(authResponse);
        assertEquals(authResponse.getToken(), jwtToken);
        assertEquals(authResponse.getUserDetails(), testUser);

        //Verify
        verify(userService, times(1)).existsByUsername(registerAuthRequest.getUsername());
        verify(passwordEncoder, times(1)).encode(registerAuthRequest.getPasswordOne());
        verify(userService, times(1)).create(any(User.class));
        verify(plaidService, times(1)).generateLinkToken(any(Long.class));
        verify(userService, times(1)).update(any(Long.class), any(User.class));
        verify(jwtService, times(1)).generateToken(any(User.class));
    }


    /**
     * Validate proper exception thrown when username is already taken
     */
    @Test
    public void testRegister_UsernameTaken_Failure() {
        //Arrange
        String username = registerAuthRequest.getUsername();
        String errorMessage = "The username [" + username + "] has already been taken.";

        //Mock
        when(userService.existsByUsername(username)).thenReturn(true);

        //Act & Assert
        UsernameTakenException usernameTakenException = assertThrows(UsernameTakenException.class, () -> {
            authService.register(registerAuthRequest);
        });
        assertEquals(usernameTakenException.getMessage(), errorMessage);

        //Verify
        verify(userService, times(1)).existsByUsername(username);

    }

    /**
     * Validate proper exception thrown when username field isn't filled ouit
     */
    @Test
    public void testRegister_UsernameMissing_Failure() {
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username(null)
                .passwordOne("password")
                .passwordTwo("password")
                .name("name")
                .build();
        String errorMessage = "Username field was not filled out.";

        //Act & Assert
        BadRegistrationRequestException badRegistrationRequestException = assertThrows(BadRegistrationRequestException.class, () -> {
            authService.register(invalidAuthRequest);
        });
        assertEquals(badRegistrationRequestException.getMessage(), errorMessage);
    }

    /**
     * Validate proper exception thrown when name field isn't filled out
     */
    @Test
    public void testRegister_NameMissing_Failure() {
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username("username")
                .passwordOne("password")
                .passwordTwo("password")
                .name(null)
                .build();
        String errorMessage = "Name field was not filled out.";

        //Act & Assert
        BadRegistrationRequestException badRegistrationRequestException = assertThrows(BadRegistrationRequestException.class, () -> {
            authService.register(invalidAuthRequest);
        });
        assertEquals(badRegistrationRequestException.getMessage(), errorMessage);
    }

    /**
     * Validate that missing password field results in necessary exception
     */
    @Test
    public void testRegister_PasswordMissing_Failure() {
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username("username")
                .passwordOne("")
                .passwordTwo("")
                .name("name")
                .build();
        String errorMessage = "Password fields were not filled out.";

        //Act & Assert
        BadRegistrationRequestException badRegistrationRequestException = assertThrows(BadRegistrationRequestException.class, () -> {
            authService.register(invalidAuthRequest);
        });
        assertEquals(badRegistrationRequestException.getMessage(), errorMessage);
    }

    /**
     * Validate that mismatching passwords results in necessary exception
     */
    @Test
    public void testRegister_PasswordMismatch_Failure() {
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username("username")
                .passwordOne("right-password")
                .passwordTwo("wrong-password")
                .name("name")
                .build();
        String errorMessage = "Password fields do not match.";

        //Act & Assert
        BadRegistrationRequestException badRegistrationRequestException = assertThrows(BadRegistrationRequestException.class, () -> {
            authService.register(invalidAuthRequest);
        });
        assertEquals(badRegistrationRequestException.getMessage(), errorMessage);
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
        when(userService.existsByUsername(registerAuthRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerAuthRequest.getPasswordOne())).thenReturn(encryptedPassword);
        when(userService.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0); //update User Id accordingly when creating user
            user.setUserId(userId);
            return user;
        });
        when(plaidService.generateLinkToken(userId)).thenThrow(new PlaidServiceException("Invalid Response Code When Generating Link Token Via PlaidClient: [404]"));

        //Act & Assert
        PlaidServiceException exception = assertThrows(PlaidServiceException.class, () -> {
            authService.register(registerAuthRequest);
        });
        assertEquals(exception.getMessage(), "Invalid Response Code When Generating Link Token Via PlaidClient: [404]");
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
                .name(registerAuthRequest.getName())
                .username(registerAuthRequest.getUsername())
                .password(encryptedPassword)
                .role(Role.USER)
                .profileImage(null)
                .accounts(new ArrayList<>())
                .categories(new ArrayList<>())
                .linkToken(null)
                .build();

        // Mock
        when(userService.existsByUsername(registerAuthRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerAuthRequest.getPasswordOne())).thenReturn(encryptedPassword);
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
            authService.register(registerAuthRequest);
        });
        assertEquals(errorMessage, exception.getMessage());

    }

    /**
     * Validate that authenticate can handle successful scenarios
     */
    @Test
    public void testAuthenticate_Success() {
        //Arrange
        String username = authenticateAuthRequest.getUsername();
        String password = authenticateAuthRequest.getPasswordOne();
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
        AuthResponse authResponse = authService.authenticate(authenticateAuthRequest);


        //Assert
        assertNotNull(authResponse);
        assertEquals(authResponse.getToken(), jwtToken);
        assertEquals(authResponse.getUserDetails(), testUser);

        //Verify
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1)).readByUsername(username);
        verify(jwtService, times(1)).generateToken(testUser);
    }

    /**
     * Validate that authenticate correctly handles null username
     */
    @Test
    public void testAuthenticate_NullUsername_Failed() {
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username("")
                .passwordOne("password")
                .build();

        //Act & Assert
        BadAuthenticationRequest badAuthenticationRequest = assertThrows(BadAuthenticationRequest.class, () -> {
            authService.authenticate(invalidAuthRequest);
        });
        assertEquals("Please fill out required fields: [Username, Password]", badAuthenticationRequest.getMessage());

        //Verify
        verify(authenticationManager, times(0)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(0)).readByUsername("");
        verify(jwtService, times(0)).generateToken(any(User.class));
    }

    /**
     * Validate that authenticate correctly handles null password
     */
    @Test
    public void testAuthenticate_NullPassword_Failed() {
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username("username")
                .passwordOne("")
                .build();

        //Act & Assert
        BadAuthenticationRequest badAuthenticationRequest = assertThrows(BadAuthenticationRequest.class, () -> {
            authService.authenticate(invalidAuthRequest);
        });
        assertEquals("Please fill out required fields: [Username, Password]", badAuthenticationRequest.getMessage());

        //Verify
        verify(authenticationManager, times(0)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(0)).readByUsername("");
        verify(jwtService, times(0)).generateToken(any(User.class));
    }

    /**
     * Validate authenticate can correctly handle invalid credentials
     */
    @Test
    public void testAuthenticate_InvalidCredentials_Failed() {
        //Arrange
        AuthRequest invalidAuthRequest = AuthRequest.builder()
                .username("username")
                .passwordOne("password")
                .build();

        //Mock
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Invalid credentials"));


        //Act & Assert
        BadCredentialsException badCredentialsException = assertThrows(BadCredentialsException.class, () -> {
            authService.authenticate(invalidAuthRequest);
        });
        assertEquals("Invalid credentials", badCredentialsException.getMessage());

        //Verify
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(0)).readByUsername("username");
        verify(jwtService, times(0)).generateToken(any(User.class));
    }

}
