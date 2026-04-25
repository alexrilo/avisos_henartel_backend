package com.serviflow.shared.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Domain Exception Tests")
class ExceptionTest {

    @Test
    @DisplayName("should create DomainException with message")
    void shouldCreateDomainExceptionWithMessage() {
        // Given
        String message = "Something went wrong";

        // When
        DomainException exception = new DomainException(message);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("should create DomainException with message and cause")
    void shouldCreateDomainExceptionWithMessageAndCause() {
        // Given
        String message = "Something went wrong";
        Throwable cause = new RuntimeException("Original cause");

        // When
        DomainException exception = new DomainException(message, cause);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("should create EntityNotFoundException with message")
    void shouldCreateEntityNotFoundExceptionWithMessage() {
        // Given
        String message = "Entity not found";

        // When
        EntityNotFoundException exception = new EntityNotFoundException(message);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should create EntityNotFoundException with cause")
    void shouldCreateEntityNotFoundExceptionWithCause() {
        // Given
        String message = "Entity not found";
        Throwable cause = new RuntimeException("Database error");

        // When
        EntityNotFoundException exception = new EntityNotFoundException(message, cause);

        // Then
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("should be able to catch DomainException")
    void shouldBeAbleToCatchDomainException() {
        // Given
        RuntimeException exception = new DomainException("Test");

        // Then
        assertThatThrownBy(() -> { throw exception; })
            .isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("should be able to catch EntityNotFoundException as DomainException")
    void shouldBeAbleToCatchEntityNotFoundExceptionAsDomainException() {
        // Given
        RuntimeException exception = new EntityNotFoundException("Test");

        // Then - can be caught as DomainException
        assertThatThrownBy(() -> { throw exception; })
            .isInstanceOf(DomainException.class);
    }
}
