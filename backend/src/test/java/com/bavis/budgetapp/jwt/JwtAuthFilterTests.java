package com.bavis.budgetapp.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bavis.budgetapp.enumeration.Role;
import com.bavis.budgetapp.filter.JwtAuthenticationFilter;
import com.bavis.budgetapp.helper.TestHelper;
import com.bavis.budgetapp.model.User;
import com.bavis.budgetapp.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class JwtAuthFilterTests {


    private TestHelper testHelper;
    private String validToken;
    private String invalidToken;

    //attributes to be mocked

    private JwtAuthenticationFilter jwtFilter;
    private JwtService jwtService;
    private HttpServletRequest request;

    private HttpServletResponse response;

    private FilterChain filterChain;

    private UserDetailsService userDetailsService;
    @BeforeEach
    public void setup() {
        testHelper = new TestHelper();
        validToken = testHelper.getValidJwtToken();
        invalidToken = "invalid-token";

        //Establish Mocks
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        userDetailsService = mock(UserDetailsService.class);
        jwtService = mock(JwtService.class);
        jwtFilter = new JwtAuthenticationFilter(jwtService, userDetailsService, testHelper.createAlgorithm());
    }

    /**
     * Ensures our JWT Authentication Filter will correctly allow access to auth endpoint to requests with proper header
     *
     */
    //TODO: Finish implementation!!!!
    @Test
    public void testDoFilterInternal_Success() throws ServletException, IOException {
        // Arrange
        UserDetails userDetails = User.builder()
                .username("test-user")
                .role(Role.USER)
                .password("fake-password")
                .name("Test User")
                .build();


        //Mock
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(userDetailsService.loadUserByUsername("test-user")).thenReturn(userDetails);
        when(jwtService.validateToken(any(), eq(userDetails))).thenReturn(true);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     *  Ensure our JWT Authentication Filter will correctly filter our any request without proper header
     */
    @Test
    public void testDoFilterInternal_Failed() {

    }
}
