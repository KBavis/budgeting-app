package com.bavis.budgetapp.config;

import com.bavis.budgetapp.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Kellen Bavis
 *
 * Class utilized to configure our Security regarding incoming HTTP Requests
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    /**
     * Configure HTTP Security and Apply JWT Authentication Filter to HTTP Requests
     *
     * @param http
     *      - HTTP request security used to configure web security
     * @return
     *      - HTTP Security Filter Chain based on configurations
     * @throws Exception
     *      - Exception that could occur while performing filter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF
                .authorizeHttpRequests((authz) ->
                        authz
                                .requestMatchers("/auth/**", "/error").permitAll() // Allow /auth/** endpoints
                                .anyRequest().authenticated() // Authenticate all other requests
                )
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session
                )
                .authenticationProvider(authenticationProvider) // Set the authentication provider
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }

    /**
     * Configures our CORS Mappings for our production/development front-end
     *
     * @return
     *      - WebMvcConfigurer utilized for our CORS Mappings
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {

        //TODO: Configure these URLs in our application-yaml file
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(
                                "http://localhost:3000",
                                "https://budget-app-frontend-6cfcivaeda-pd.a.run.app" //google cloud run
                        )
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }
        };
    }
}
