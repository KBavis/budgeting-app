package com.bavis.budgetapp.security;

import com.bavis.budgetapp.constants.Role;
import com.bavis.budgetapp.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the link between a User's persisted Role and the SecurityConfig rule
 * {@code .requestMatchers(POST, "/budget/performance").hasAuthority("ADMIN")}.
 *
 * JwtAuthenticationFilter builds the Authentication exactly as done in authenticate(...) below:
 * the User is loaded from the DB on every request and its getAuthorities() become the granted authorities.
 */
class AdminAuthorityTests {

    private static Authentication authenticate(User user) {
        return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
    }

    private static AuthorizationDecision hasAuthorityAdmin(User user) {
        return AuthorityAuthorizationManager.<Object>hasAuthority("ADMIN").check(() -> authenticate(user), new Object());
    }

    @Test
    void userWithAdminRole_isGrantedAdminAuthority() {
        assertTrue(hasAuthorityAdmin(User.builder().username("admin").role(Role.ADMIN).build()).isGranted());
    }

    @Test
    void userWithUserRole_isDeniedAdminAuthority() {
        assertFalse(hasAuthorityAdmin(User.builder().username("someone").role(Role.USER).build()).isGranted());
    }

    /**
     * Documents why the rule uses hasAuthority("ADMIN") and not hasRole("ADMIN"): our authorities are the bare
     * enum name (no ROLE_ prefix), so hasRole("ADMIN") - which looks for ROLE_ADMIN - would lock out even admins.
     */
    @Test
    void hasRoleAdmin_wouldNotMatchOurAuthorities() {
        User admin = User.builder().username("admin").role(Role.ADMIN).build();
        AuthorizationDecision decision = AuthorityAuthorizationManager.<Object>hasRole("ADMIN")
                .check(() -> authenticate(admin), new Object());
        assertFalse(decision.isGranted());
    }
}
