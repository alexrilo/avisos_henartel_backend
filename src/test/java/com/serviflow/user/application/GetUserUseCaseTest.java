package com.serviflow.user.application;

import com.serviflow.user.application.output.UserOutput;
import com.serviflow.user.domain.entity.Role;
import com.serviflow.user.domain.entity.User;
import com.serviflow.user.domain.exception.UserNotFoundException;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserUseCase")
class GetUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    private GetUserUseCase getUserUseCase;

    @BeforeEach
    void setUp() {
        getUserUseCase = new GetUserUseCase(userRepository);
    }

    @Nested
    @DisplayName("execute")
    class Execute {

        @Test
        @DisplayName("should return user by id")
        void shouldReturnUserById() {
            // Given
            Long userId = 1L;
            User user = User.reconstitute(
                new UserId(userId),
                "testuser",
                "hashedPassword",
                "John",
                "Doe",
                new Email("john@example.com"),
                Role.ADMINISTRADOR,
                UserStatus.ACTIVE,
                LocalDateTime.now()
            );
            when(userRepository.findById(new UserId(userId))).thenReturn(Optional.of(user));

            // When
            UserOutput result = getUserUseCase.execute(userId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(userId);
            assertThat(result.username()).isEqualTo("testuser");
            assertThat(result.email()).isEqualTo("john@example.com");
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user does not exist")
        void shouldThrowUserNotFoundException() {
            // Given
            Long userId = 999L;
            when(userRepository.findById(new UserId(userId))).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> getUserUseCase.execute(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("999");
        }
    }
}
