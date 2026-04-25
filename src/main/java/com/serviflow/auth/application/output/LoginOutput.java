package com.serviflow.auth.application.output;

/**
 * Output record for login response.
 */
public record LoginOutput(
    Long userId,
    String accessToken,
    String refreshToken,
    String tokenType,
    String username,
    String role
) {}
