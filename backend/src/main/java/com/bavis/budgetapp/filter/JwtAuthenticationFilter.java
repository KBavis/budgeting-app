package com.bavis.budgetapp.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bavis.budgetapp.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * @author Kellen Bavis
 *
 * Filter to verify valid authentication of incoming HTTP request
 */
@Component("jwtAuthFilter")
@Log4j2
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService _jwtService;
    private final UserDetailsService _userDetailsService;

    private final Algorithm _algorithm;


    public JwtAuthenticationFilter (JwtService _jwtService,
                                    UserDetailsService _userDetailsService,
                                    Algorithm _algorithm){
        this._jwtService = _jwtService;
        this._userDetailsService = _userDetailsService;
        this._algorithm = _algorithm;
    }



    /**
     * Filter incoming HTTP requests
     *
     *
     * @param request
     *          - incoming HTTP request
     * @param response
     *          -  outgoing HTTP response
     * @param filterChain
     *          - chain of filters that the request must pass through
     *
     */
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Extract Authorization header from HTTP request
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        //verifier for our JWT
        final JWTVerifier jwtVerifier = JWT.require(_algorithm)
                .withIssuer("bavis")
                .build();

        //Ensure request contains proper JWT Token prior to continuing
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        DecodedJWT decodedJWT;
        try{
            //Verify the validity of the passed JWT Token
           decodedJWT = jwtVerifier.verify(jwt);

           //Extract Username & Validate Presence
            //TODO: Consider Using Subject As A Separate Field, And Then Just Generate Our Own Unique Claim Called userId
            String jwtUsername = decodedJWT.getSubject();

            //Skip Authentication If User Has Already Been Authenticated
            //TODO: Ensure That When A User Logs Out That The SecurityContextHolder Authentication Is Set To Null
            if(jwtUsername != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //Fetch User Details From DB
                UserDetails userDetails = this._userDetailsService.loadUserByUsername(jwtUsername);

                //Determine If Claims In-Validate Our JWT
                if(_jwtService.validateToken(decodedJWT, userDetails)){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch(JWTVerificationException e){
            log.warn("HTTP Request does did not contain proper JWT Authentication: [{}]", e.getMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
