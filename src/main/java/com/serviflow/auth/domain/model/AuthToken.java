package com.serviflow.auth.domain.model;

import java.time.Instant;
import java.util.Objects;

/**
 * Value object representing an authentication token.
 */
public final class AuthToken {

    private final String accessToken;
    private final String tokenType;
    private final Instant expiresAt;

    public AuthToken(String accessToken, String tokenType, Instant expiresAt) {
        this.accessToken = Objects.requireNonNull(accessToken, "Access token cannot be null");
        this.tokenType = Objects.requireNonNull(tokenType, "Token type cannot be null");
        this.expiresAt = Objects.requireNonNull(expiresAt, "Expires at cannot be null");
    }

    /**
     * Factory method for creating an AuthToken with default "Bearer" type.
     */
    public static AuthToken of(String accessToken, Instant expiresAt) {
        return new AuthToken(accessToken, "Bearer", expiresAt);
    }

    public String accessToken() {
        return accessToken;
    }

    public String tokenType() {
        return tokenType;
    }

    public Instant expiresAt() {
        return expiresAt;
    }

    /**
     * Checks if the token is expired.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthToken authToken)) return false;
        return Objects.equals(accessToken, authToken.accessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken);
    }

    @Override
    public String toString() {
        return "AuthToken{" +
               "accessToken='" + accessToken + '\'' +
               ", tokenType='" + tokenType + '\'' +
               ", expiresAt=" + expiresAt +
               '}';
    }
}
