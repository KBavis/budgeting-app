package com.bavis.budgetapp.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bavis.budgetapp.enumeration.TimeType;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.impl.JwtServiceImpl;
import com.bavis.budgetapp.util.GeneralUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JWTServiceTests {

    @Mock
    private Algorithm algorithm;

    @InjectMocks
    private JwtServiceImpl jwtService;

    /**
     * Validates that our JWT Service can correctly validate a JWT Token
     */
    @Test
    public void testValidateToken_ValidToken_Success() {
        //Arrange
        String username = "test-user";
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expirationTime = GeneralUtil.addTimeToDate(currentTime, 1, TimeType.HOURS);
        Date expirationDate = GeneralUtil.localDateTimeToDate(expirationTime);

        //Mock
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(decodedJWT.getExpiresAt()).thenReturn(expirationDate);
        when(decodedJWT.getSubject()).thenReturn(username);
        when(userDetails.getUsername()).thenReturn(username);

        //Act
        boolean isValid = jwtService.validateToken(decodedJWT, userDetails);

        //Assert
        assertTrue(isValid);
    }

    /**
     * Validates that our JWT Service can handle expired tokens when validating JWT Tokens
     */
    @Test
    public void testValidateToken_ExpiredToken_Failed() {
        //Arrange
        String username = "test-user";
        LocalDateTime expirationTime = LocalDateTime.now().minusDays(1); //simulate expiration time being yesterday
        Date expirationDate = GeneralUtil.localDateTimeToDate(expirationTime);

        //Mock
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(decodedJWT.getExpiresAt()).thenReturn(expirationDate);
        when(decodedJWT.getSubject()).thenReturn(username);
        when(userDetails.getUsername()).thenReturn(username);

        //Act
        boolean isValid = jwtService.validateToken(decodedJWT, userDetails);

        //Assert
        assertFalse(isValid);
    }

    /**
     * Validates that our JWT Service can handle username mismatch correctly when validating token
     */
    @Test
    public void testValidateToken_InvalidUsername_Failed() {
        //Arrange
        String userDetailsUsername = "user-details-test-user";
        String tokenUsername = "token-user-name";
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expirationTime = GeneralUtil.addTimeToDate(currentTime, 1, TimeType.HOURS);
        Date expirationDate = GeneralUtil.localDateTimeToDate(expirationTime);

        //Mock
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(decodedJWT.getExpiresAt()).thenReturn(expirationDate);
        when(decodedJWT.getSubject()).thenReturn(tokenUsername);
        when(userDetails.getUsername()).thenReturn(userDetailsUsername);

        //Act
        boolean isValid = jwtService.validateToken(decodedJWT, userDetails);

        //Assert
        assertFalse(isValid);
    }

    /**
     * Validates our JWT Service can successfully generate a JWT Token
     */
    @Test
    public void testGenerateToken_Success() {
        //Arrange
        User user = new User();
        user.setUsername("test-user");

        //Mock
        when(algorithm.sign(any(byte[].class), any(byte[].class))).thenReturn("mocked-token".getBytes());

        //Act
        String token = jwtService.generateToken(user);

        //Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertInstanceOf(String.class, token);
        assertEquals(3, token.split("\\.").length); //validate format

        //Verify
        verify(algorithm, times(1)).sign(any(byte[].class), any(byte[].class));
    }

    /**
     * Validate our JWT Service handles failed token generations gracefully
     */
    @Test
    public void testGenerateToken_Failed() {
        //Arrange
        User user = new User();
        user.setUsername("test-user");

        //Mock
        when(algorithm.sign(any(byte[].class), any(byte[].class))).thenReturn(null); //ensures run time exception

        //Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtService.generateToken(user);
        });
        assertTrue(exception.getMessage().contains("Failed to Generate JWT Token:"));

        //Verify
        verify(algorithm, times(1)).sign(any(byte[].class), any(byte[].class));
    }
}
