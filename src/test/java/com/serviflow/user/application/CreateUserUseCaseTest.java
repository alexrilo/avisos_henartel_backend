package com.serviflow.user.application;

import com.serviflow.user.application.exception.DuplicateUserException;
import com.serviflow.user.application.input.CreateUserInput;
import com.serviflow.user.application.output.UserOutput;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CreateUserUseCase")
class CreateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CreateUserUseCase createUserUseCase;

    @BeforeEach
    void setUp() {
        createUserUseCase = new CreateUserUseCase(userRepository, passwordEncoder);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("should create user with valid data")
        void shouldCreateUserWithValidData() {
            // Given
            CreateUserInput input = new CreateUserInput(
                "testuser",
                "password123",
                "John",
                "Doe",
                "john@example.com",
                "ADMINISTRADOR"
            );

            when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");

            User savedUser = User.reconstitute(
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
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            // When
            UserOutput result = createUserUseCase.execute(input);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.username()).isEqualTo("testuser");
            assertThat(result.nombre()).isEqualTo("John");
            assertThat(result.apellido()).isEqualTo("Doe");
            assertThat(result.email()).isEqualTo("john@example.com");
            assertThat(result.role()).isEqualTo("ADMINISTRADOR");
            assertThat(result.status()).isEqualTo("ACTIVE");

            verify(userRepository).existsByEmail(any(Email.class));
            verify(userRepository).existsByUsername("testuser");
            verify(passwordEncoder).encode("password123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("should throw DuplicateUserException when email already exists")
        void shouldThrowDuplicateUserExceptionWhenEmailExists() {
            // Given
            CreateUserInput input = new CreateUserInput(
                "testuser",
                "password123",
                "John",
                "Doe",
                "john@example.com",
                "ADMINISTRADOR"
            );

            when(userRepository.existsByEmail(any(Email.class))).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> createUserUseCase.execute(input))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessageContaining("email")
                .hasMessageContaining("john@example.com");
        }

        @Test
        @DisplayName("should throw DuplicateUserException when username already exists")
        void shouldThrowDuplicateUserExceptionWhenUsernameExists() {
            // Given
            CreateUserInput input = new CreateUserInput(
                "existinguser",
                "password123",
                "John",
                "Doe",
                "john@example.com",
                "ADMINISTRADOR"
            );

            when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> createUserUseCase.execute(input))
                .isInstanceOf(DuplicateUserException.class)
                .hasMessageContaining("username")
                .hasMessageContaining("existinguser");
        }

        @Test
        @DisplayName("should throw exception for invalid email format")
        void shouldThrowExceptionForInvalidEmail() {
            // Given
            CreateUserInput input = new CreateUserInput(
                "testuser",
                "password123",
                "John",
                "Doe",
                "invalid-email",
                "ADMINISTRADOR"
            );

            when(userRepository.existsByEmail(any(Email.class))).thenReturn(false);
            when(userRepository.existsByUsername(anyString())).thenReturn(false);

            // When/Then
            assertThatThrownBy(() -> createUserUseCase.execute(input))
                .hasMessageContaining("Invalid email format");
        }
    }
}
