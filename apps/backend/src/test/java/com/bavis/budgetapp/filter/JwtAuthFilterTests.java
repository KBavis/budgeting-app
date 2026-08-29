package com.bavis.budgetapp.filter;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bavis.budgetapp.constants.Role;
import com.bavis.budgetapp.TestHelper;
import com.bavis.budgetapp.entity.User;
import com.bavis.budgetapp.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles(profiles = "test")
public class JwtAuthFilterTests {


    private String validToken;
    private String invalidToken;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtFilter;


    @BeforeEach
    public void setup() {
        //Generate Valid JWT Token
        User fakeUser = User.builder()
                .name("Test User")
                .username("test-user")
                .password("password")
                .build();
        Algorithm algorithm = TestHelper.createAlgorithm();
        validToken = TestHelper.getValidJwtToken(algorithm, fakeUser); //pass in algo so we can use same algo to validate token

        //Invalid Jwt Token
        invalidToken = "invalid-token";

        //Create Filter
        jwtFilter = new JwtAuthenticationFilter(jwtService, userDetailsService, algorithm); // use same algorithm used to generate JWT token
    }

    /**
     * Ensures our JWT Authentication Filter will correctly allow access to auth endpoint to requests with proper header
     *
     */
    @Test()
    @Order(3)
    public void testDoFilterInternal_Success() throws ServletException, IOException {
        //Assert Authentication Is Originally Null
        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());

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
        when(jwtService.validateToken(any(DecodedJWT.class), eq(userDetails))).thenReturn(true);

        // Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        //Assert SecurityContextHolder Authentication Updated Appropriately
        securityContext = SecurityContextHolder.getContext();
        assertNotNull(securityContext);
        assertNotNull(securityContext.getAuthentication());
        assertEquals(userDetails, securityContext.getAuthentication().getPrincipal());

        //Verify
        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, times(1)).loadUserByUsername("test-user");
        verify(jwtService, times(1)).validateToken(any(DecodedJWT.class), eq(userDetails));
    }

    /**
     *  Ensure our JWT Authentication Filter will correctly filter out any request without valid JWT token (format/algo)
     */
    @Test
    @Order(1)
    public void testDoFilterInternal_JWTVerifier_InvalidToken_Failed() throws Exception{
        //Assert Authentication originally null so we can validate it was not updated
        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());

        //Mock
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);

        //Act
        jwtFilter.doFilterInternal(request, response, filterChain);

        //Assert Authentication not added
        securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());

        //Verify ability to do next request
        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, times(0)).loadUserByUsername("test-user");
        verify(jwtService, times(0)).validateToken(any(DecodedJWT.class), any(UserDetails.class));

    }

    /**
     *  Ensures our JWT Authentication Filter will correctly filter out any request with improper user associated or expired token
     */
    @Test
    @Order(2)
    public void testDoFilterInternal_JwtService_InvalidToken_Failed() throws  Exception {
        //Assert Authentication originally null
        SecurityContext securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());


        // Arrange
        UserDetails userDetails = User.builder()
                .username("test-user")
                .role(Role.USER)
                .password("fake-password")
                .name("Test User")
                .build();

        //Mock
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken); //use valid JWT token
        when(userDetailsService.loadUserByUsername("test-user")).thenReturn(userDetails);
        when(jwtService.validateToken(any(DecodedJWT.class), eq(userDetails))).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        //Assert Authentication not added
        securityContext = SecurityContextHolder.getContext();
        assertNull(securityContext.getAuthentication());

        //Verify
        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, times(1)).loadUserByUsername("test-user");
        verify(jwtService, times(1)).validateToken(any(DecodedJWT.class), eq(userDetails));

    }
}
