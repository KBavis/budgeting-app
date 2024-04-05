package com.bavis.budgetapp.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bavis.budgetapp.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.net.ssl.KeyStoreBuilderParameters;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component("jwtAuthFilter")
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

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
            LOG.debug("HTTP Request Does Not Contain Proper Authorization!");
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        LOG.debug("JWT Extracted From Authorization Header: {}", jwt);
        DecodedJWT decodedJWT;
        try{
            //Verify the validity of the passed JWT Token
           decodedJWT = jwtVerifier.verify(jwt);

           //Extract Username & Validate Presence
            //TODO: Consider Using Subject As A Separate Field, And Then Just Generate Our Own Unique Claim Called userId
            String jwtUsername = decodedJWT.getSubject();
            LOG.debug("Extracted Jwt Username: '{}'", jwtUsername);

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
            LOG.error("JWT Verification Failed! Exception: {}", e.getMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
