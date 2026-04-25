package com.serviflow.user.domain.valueobject;

import com.serviflow.shared.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Email Value Object Tests")
class EmailTest {

    @Test
    @DisplayName("should create email with valid format")
    void shouldCreateEmailWithValidFormat() {
        // Given
        String validEmail = "user@serviflow.com";

        // When
        Email email = Email.of(validEmail);

        // Then
        assertThat(email.value()).isEqualTo(validEmail);
    }

    @Test
    @DisplayName("should create email with subdomain")
    void shouldCreateEmailWithSubdomain() {
        // Given
        String validEmail = "user@subdomain.serviflow.com";

        // When
        Email email = Email.of(validEmail);

        // Then
        assertThat(email.value()).isEqualTo(validEmail);
    }

    @Test
    @DisplayName("should create email with plus sign")
    void shouldCreateEmailWithPlusSign() {
        // Given
        String validEmail = "user+tag@serviflow.com";

        // When
        Email email = Email.of(validEmail);

        // Then
        assertThat(email.value()).isEqualTo(validEmail);
    }

    @Test
    @DisplayName("should reject null email")
    void shouldRejectNullEmail() {
        // Then
        assertThatThrownBy(() -> Email.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("Email cannot be null");
    }

    @Test
    @DisplayName("should reject blank email")
    void shouldRejectBlankEmail() {
        // Then
        assertThatThrownBy(() -> Email.of("   "))
            .isInstanceOf(DomainException.class)
            .hasMessage("Email cannot be blank");
    }

    @Test
    @DisplayName("should reject email without @")
    void shouldRejectEmailWithoutAt() {
        // Then
        assertThatThrownBy(() -> Email.of("userserviflow.com"))
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("should reject email without domain")
    void shouldRejectEmailWithoutDomain() {
        // Then
        assertThatThrownBy(() -> Email.of("user@"))
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("should reject email without local part")
    void shouldRejectEmailWithoutLocalPart() {
        // Then
        assertThatThrownBy(() -> Email.of("@serviflow.com"))
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("should reject email with spaces")
    void shouldRejectEmailWithSpaces() {
        // Then
        assertThatThrownBy(() -> Email.of("user @serviflow.com"))
            .isInstanceOf(DomainException.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("should have proper equals and hashCode")
    void shouldHaveProperEqualsAndHashCode() {
        // Given
        Email email1 = Email.of("user@serviflow.com");
        Email email2 = Email.of("user@serviflow.com");
        Email email3 = Email.of("other@serviflow.com");

        // Then
        assertThat(email1).isEqualTo(email2);
        assertThat(email1).isNotEqualTo(email3);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }

    @Test
    @DisplayName("should return string representation")
    void shouldReturnStringRepresentation() {
        // Given
        Email email = Email.of("user@serviflow.com");

        // Then
        assertThat(email.toString()).isEqualTo("user@serviflow.com");
    }
}
