package com.serviflow.user.domain.valueobject;

import com.serviflow.shared.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("UserId Value Object Tests")
class UserIdTest {

    @Test
    @DisplayName("should create UserId with positive value")
    void shouldCreateUserIdWithPositiveValue() {
        // Given
        Long value = 1L;

        // When
        UserId userId = UserId.of(value);

        // Then
        assertThat(userId.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("should create UserId with large value")
    void shouldCreateUserIdWithLargeValue() {
        // Given
        Long value = 999999999L;

        // When
        UserId userId = UserId.of(value);

        // Then
        assertThat(userId.value()).isEqualTo(value);
    }

    @Test
    @DisplayName("should reject null value")
    void shouldRejectNullValue() {
        // Then
        assertThatThrownBy(() -> UserId.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("UserId cannot be null");
    }

    @Test
    @DisplayName("should reject zero value")
    void shouldRejectZeroValue() {
        // Then
        assertThatThrownBy(() -> UserId.of(0L))
            .isInstanceOf(DomainException.class)
            .hasMessage("UserId must be positive");
    }

    @Test
    @DisplayName("should reject negative value")
    void shouldRejectNegativeValue() {
        // Then
        assertThatThrownBy(() -> UserId.of(-1L))
            .isInstanceOf(DomainException.class)
            .hasMessage("UserId must be positive");
    }

    @Test
    @DisplayName("should have proper equals and hashCode")
    void shouldHaveProperEqualsAndHashCode() {
        // Given
        UserId userId1 = UserId.of(1L);
        UserId userId2 = UserId.of(1L);
        UserId userId3 = UserId.of(2L);

        // Then
        assertThat(userId1).isEqualTo(userId2);
        assertThat(userId1).isNotEqualTo(userId3);
        assertThat(userId1.hashCode()).isEqualTo(userId2.hashCode());
    }

    @Test
    @DisplayName("should return string representation")
    void shouldReturnStringRepresentation() {
        // Given
        UserId userId = UserId.of(1L);

        // Then
        assertThat(userId.toString()).isEqualTo("UserId(1)");
    }
}
