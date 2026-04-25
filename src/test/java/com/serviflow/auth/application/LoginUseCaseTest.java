package com.serviflow.auth.application;

import com.serviflow.auth.application.input.LoginInput;
import com.serviflow.auth.application.output.LoginOutput;
import com.serviflow.auth.domain.model.AuthToken;
import com.serviflow.auth.domain.port.TokenProvider;
import com.serviflow.user.application.exception.InvalidCredentialsException;
import com.serviflow.user.domain.entity.Role;
import com.serviflow.user.domain.entity.User;
import com.serviflow.user.domain.port.PasswordEncoder;
import com.serviflow.user.domain.port.UserRepository;
import com.serviflow.user.domain.valueobject.Email;
import com.serviflow.user.domain.valueobject.UserId;
import com.serviflow.user.domain.valueobject.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginUseCase")
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenProvider tokenProvider;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(userRepository, passwordEncoder, tokenProvider);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("should return token with valid credentials")
        void shouldReturnTokenWithValidCredentials() {
            // Given
            LoginInput input = new LoginInput("john@example.com", "password123");

            User user = User.reconstitute(
                new UserId(1L),
                "testuser",
                "hashedPassword",
                "John",
                "Doe",
                new Email("john@example.com"),
                Role.ADMINISTRADOR,
                UserStatus.ACTIVE,
                LocalDateTime.now()
            );
            when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);

            AuthToken token = new AuthToken("jwt-token", "Bearer", Instant.now().plusSeconds(3600));
            AuthToken refreshToken = new AuthToken("refresh-token", "Bearer", Instant.now().plusSeconds(604800));
            when(tokenProvider.generateToken(anyString(), anyMap())).thenReturn(token);
            when(tokenProvider.generateRefreshToken(anyString(), anyMap())).thenReturn(refreshToken);

            // When
            LoginOutput result = loginUseCase.execute(input);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.accessToken()).isEqualTo("jwt-token");
            assertThat(result.refreshToken()).isEqualTo("refresh-token");
            assertThat(result.tokenType()).isEqualTo("Bearer");
            assertThat(result.username()).isEqualTo("testuser");
            assertThat(result.role()).isEqualTo("ADMINISTRADOR");

            verify(userRepository).findByEmail(new Email("john@example.com"));
            verify(passwordEncoder).matches("password123", "hashedPassword");
            verify(tokenProvider).generateToken(anyString(), anyMap());
            verify(tokenProvider).generateRefreshToken(anyString(), anyMap());
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException with wrong password")
        void shouldThrowInvalidCredentialsExceptionWithWrongPassword() {
            // Given
            LoginInput input = new LoginInput("john@example.com", "wrongpassword");

            User user = User.reconstitute(
                new UserId(1L),
                "testuser",
                "hashedPassword",
                "John",
                "Doe",
                new Email("john@example.com"),
                Role.ADMINISTRADOR,
                UserStatus.ACTIVE,
                LocalDateTime.now()
            );
            when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongpassword", "hashedPassword")).thenReturn(false);

            // When/Then
            assertThatThrownBy(() -> loginUseCase.execute(input))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid credentials");
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException for inactive user")
        void shouldThrowInvalidCredentialsExceptionForInactiveUser() {
            // Given
            LoginInput input = new LoginInput("john@example.com", "password123");

            User inactiveUser = User.reconstitute(
                new UserId(1L),
                "testuser",
                "hashedPassword",
                "John",
                "Doe",
                new Email("john@example.com"),
                Role.ADMINISTRADOR,
                UserStatus.INACTIVE,
                LocalDateTime.now()
            );
            when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.of(inactiveUser));

            // When/Then
            assertThatThrownBy(() -> loginUseCase.execute(input))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessageContaining("deactivated");
        }

        @Test
        @DisplayName("should throw InvalidCredentialsException for non-existent email")
        void shouldThrowInvalidCredentialsExceptionForNonExistentEmail() {
            // Given
            LoginInput input = new LoginInput("nonexistent@example.com", "password123");

            when(userRepository.findByEmail(any(Email.class))).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> loginUseCase.execute(input))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid credentials");
        }
    }
}
