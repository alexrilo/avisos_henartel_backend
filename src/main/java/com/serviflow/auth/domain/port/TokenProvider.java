package com.serviflow.auth.domain.port;

import com.serviflow.auth.domain.model.AuthToken;

import java.util.Map;

/**
 * Output port for JWT token generation and validation.
 * Pure interface with no framework dependencies.
 */
public interface TokenProvider {

    /**
     * Generates an authentication token for the given username and claims.
     */
    AuthToken generateToken(String username, Map<String, Object> claims);

    /**
     * Generates a refresh token for the given username and claims.
     */
    AuthToken generateRefreshToken(String username, Map<String, Object> claims);

    /**
     * Extracts the username from a token.
     */
    String extractUsername(String token);

    /**
     * Extracts all claims from a token.
     */
    Map<String, Object> extractClaims(String token);

    /**
     * Validates if a token is valid.
     */
    boolean isTokenValid(String token);
}
