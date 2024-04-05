package com.bavis.budgetapp.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bavis.budgetapp.filter.JwtAuthenticationFilter;
import com.bavis.budgetapp.helper.TestHelper;
import com.bavis.budgetapp.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class JwtAuthFilterTests {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private Algorithm algorithm;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private FilterChain filterChain;

    private TestHelper testHelper;

    @BeforeEach
    public void setup() {
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        testHelper = new TestHelper();
    }

    /**
     * Ensures our JWT Authentication Filter will correctly allow access to auth endpoint to requests with proper header
     *
     * TODO: Finish & Fix Me!!
     */
    @Test
    public void testDoFilterInternal_Success() {
        //Arrange
        String token = testHelper.getValidJwtToken();
        String authHeader = "Bearer " + token;
        String username = "testuser";

        //Mock
        DecodedJWT decodedJWT = mock(DecodedJWT.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(request.getHeader("Authorization")).thenReturn(authHeader);
        when(jwtService.validateToken(decodedJWT, userDetails)).thenReturn(true);
        when(decodedJWT.getSubject()).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(JWT.require(algorithm).build().verify(token)).thenReturn(decodedJWT);

        //Act
        try {
            jwtAuthenticationFilter.doFilter(request, response, filterChain);
        } catch (ServletException e) {
            fail("Unexpected exception thrown: {}", e);
        } catch (IOException e) {
            fail("Unexpected exception thrown: {}", e);
        }

        //Assert & Verfiy
        try {
            verify(filterChain, times(1)).doFilter(request, response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     *  Ensure our JWT Authentication Filter will correctly filter our any request without proper header
     */
    @Test
    public void testDoFilterInternal_Failed() {

    }
}
