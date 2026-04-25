package com.serviflow.auth.application;

import com.serviflow.auth.application.input.LoginInput;
import com.serviflow.auth.application.output.LoginOutput;
import com.serviflow.auth.domain.model.AuthToken;
import com.serviflow.auth.domain.port.TokenProvider;
import com.serviflow.user.application.exception.InvalidCredentialsException;
import com.serviflow.user.domain.entity.User;
import com.serviflow.user.domain.port.PasswordEncoder;
import com.serviflow.user.domain.port.UserRepository;
import com.serviflow.user.domain.valueobject.Email;

import java.time.Instant;
import java.util.Map;

/**
 * Use case for user authentication (login).
 * Validates credentials and generates JWT token.
 */
public class LoginUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    public LoginUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    /**
     * Executes the use case for user login.
     * 
     * @param input LoginInput containing email and password
     * @return LoginOutput with token and user info
     * @throws InvalidCredentialsException if credentials are invalid or user is inactive
     */
    public LoginOutput execute(LoginInput input) {
        Email email = new Email(input.email());
        User user = userRepository.findByEmail(email)
            .orElseThrow(InvalidCredentialsException::new);

        if (!user.isActive()) {
            throw new InvalidCredentialsException("User account is deactivated");
        }

        if (!passwordEncoder.matches(input.password(), user.password())) {
            throw new InvalidCredentialsException();
        }

        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("role", user.role().name());
        claims.put("userId", user.id() != null ? user.id().value() : null);
        if (user.isTechnician() && user.id() != null) {
            claims.put("tecnicoId", user.id().value());
        }

        AuthToken accessToken = tokenProvider.generateToken(user.username(), claims);
        AuthToken refreshToken = tokenProvider.generateRefreshToken(user.username(), claims);

        return new LoginOutput(
            user.id() != null ? user.id().value() : null,
            accessToken.accessToken(),
            refreshToken.accessToken(),
            accessToken.tokenType(),
            user.username(),
            user.role().name()
        );
    }
}
