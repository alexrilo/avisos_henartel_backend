package com.serviflow.user.domain.port;

/**
 * Output port for password encoding operations.
 * Abstracts the password encoding implementation.
 */
public interface PasswordEncoder {

    /**
     * Encodes a raw password.
     */
    String encode(String rawPassword);

    /**
     * Verifies if a raw password matches an encoded password.
     */
    boolean matches(String rawPassword, String encodedPassword);
}
