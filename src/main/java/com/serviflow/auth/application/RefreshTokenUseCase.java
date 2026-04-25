package com.serviflow.auth.application;

import com.serviflow.auth.application.output.LoginOutput;
import com.serviflow.auth.domain.model.AuthToken;
import com.serviflow.auth.domain.port.TokenProvider;
import com.serviflow.user.application.exception.InvalidCredentialsException;

import java.util.Map;

/**
 * Use case for refreshing an access token using a valid refresh token.
 * Validates the refresh token and issues a new access token.
 */
public class RefreshTokenUseCase {

    private final TokenProvider tokenProvider;

    public RefreshTokenUseCase(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Executes the refresh token flow.
     *
     * @param refreshToken the refresh token string
     * @return LoginOutput with a new access token
     * @throws InvalidCredentialsException if the refresh token is invalid
     */
    public LoginOutput execute(String refreshToken) {
        if (!tokenProvider.isTokenValid(refreshToken)) {
            throw new InvalidCredentialsException("Invalid or expired refresh token");
        }

        String username = tokenProvider.extractUsername(refreshToken);
        Map<String, Object> claims = tokenProvider.extractClaims(refreshToken);

        AuthToken newAccessToken = tokenProvider.generateToken(username, claims);

        return new LoginOutput(
            claims.get("userId") instanceof Number n ? n.longValue() : null,
            newAccessToken.accessToken(),
            refreshToken,
            newAccessToken.tokenType(),
            username,
            claims.get("role") != null ? claims.get("role").toString() : null
        );
    }
}
