package com.serviflow.auth.infrastructure.config;

import com.serviflow.auth.domain.port.TokenProvider;
import com.serviflow.auth.infrastructure.encoder.BcryptPasswordEncoderAdapter;
import com.serviflow.auth.infrastructure.security.JwtTokenProviderAdapter;
import com.serviflow.user.domain.port.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration for Auth infrastructure components.
 * Wires adapters and config beans.
 */
@Configuration
public class AuthInfrastructureConfig {

    /**
     * Domain PasswordEncoder port — used by use cases.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BcryptPasswordEncoderAdapter();
    }

    /**
     * Spring Security PasswordEncoder — used by DaoAuthenticationProvider.
     * Delegates to the same BCrypt implementation.
     */
    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder springPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TokenProvider tokenProvider(
            @Value("${jwt.secret}") String secret, 
            @Value("${jwt.expiration}") long expirationMs,
            @Value("${jwt.refresh-expiration}") long refreshExpirationMs) {
        return new JwtTokenProviderAdapter(secret, expirationMs, refreshExpirationMs);
    }
}
