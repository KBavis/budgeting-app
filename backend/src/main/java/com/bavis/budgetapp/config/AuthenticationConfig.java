package com.bavis.budgetapp.config;

import com.bavis.budgetapp.dao.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Kellen Bavis
 *
 * Configuration class for our Authentication implementation
 */
@Configuration("authConfig")
public class AuthenticationConfig {

    private final UserRepository _userRepository;

    public AuthenticationConfig(UserRepository _userRepository) {
        this._userRepository = _userRepository;
    }

    /**
     * Configure UserDetailsService to utilize our user repository
     *
     * @return
     *      - UserDetailsService configured with our UserRepository
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> _userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User [" + username + "] not found within repository."));
    }

    /**
     *
     * @return
     *      - PasswordEncoder to encrypt users passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    /**
     *
     * @return
     *      - AuthenticationProvider utilized for authenticating user's credentials
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }


    /**
     *  Utilized to coordinate which AuthenticationProvider should be used to authenticate request
     *
     * @param config
     *          - AuthenticationConfiguration from Spring Security configuration
     * @return
     *      - AuthenticationManager utilized for managing the overall authentication process
     * @throws Exception
     *      - Throws Exceptions regarding fetching a proper AuthenticationManager from AuthenticationConfiguration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
