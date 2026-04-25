package com.serviflow.cliente.domain.valueobject;

import com.serviflow.shared.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ClienteId value object.
 */
class ClienteIdTest {

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("should accept valid positive ID")
        void shouldAcceptValidPositiveId() {
            ClienteId id = new ClienteId(1L);

            assertEquals(1L, id.value());
        }

        @Test
        @DisplayName("should reject null ID")
        void shouldRejectNullId() {
            assertThrows(NullPointerException.class, () ->
                new ClienteId(null)
            );
        }

        @Test
        @DisplayName("should reject zero ID")
        void shouldRejectZeroId() {
            DomainException exception = assertThrows(DomainException.class, () ->
                new ClienteId(0L)
            );
            assertTrue(exception.getMessage().contains("must be positive"));
        }

        @Test
        @DisplayName("should reject negative ID")
        void shouldRejectNegativeId() {
            DomainException exception = assertThrows(DomainException.class, () ->
                new ClienteId(-1L)
            );
            assertTrue(exception.getMessage().contains("must be positive"));
        }

        @Test
        @DisplayName("should reject negative ID (-100)")
        void shouldRejectNegativeId100() {
            DomainException exception = assertThrows(DomainException.class, () ->
                new ClienteId(-100L)
            );
            assertTrue(exception.getMessage().contains("must be positive"));
        }
    }

    @Nested
    @DisplayName("Factory Method")
    class FactoryMethod {

        @Test
        @DisplayName("of should create ClienteId from Long")
        void of_shouldCreateClienteIdFromLong() {
            ClienteId id = ClienteId.of(42L);

            assertEquals(42L, id.value());
        }

        @Test
        @DisplayName("of should throw for null")
        void of_shouldThrowForNull() {
            assertThrows(NullPointerException.class, () ->
                ClienteId.of(null)
            );
        }

        @Test
        @DisplayName("of should throw for zero")
        void of_shouldThrowForZero() {
            assertThrows(DomainException.class, () ->
                ClienteId.of(0L)
            );
        }
    }

    @Nested
    @DisplayName("Equals and HashCode")
    class EqualsAndHashCode {

        @Test
        @DisplayName("equals should be true for same value")
        void equals_shouldBeTrueForSameValue() {
            ClienteId id1 = new ClienteId(1L);
            ClienteId id2 = new ClienteId(1L);

            assertEquals(id1, id2);
            assertEquals(id1.hashCode(), id2.hashCode());
        }

        @Test
        @DisplayName("equals should be false for different values")
        void equals_shouldBeFalseForDifferentValues() {
            ClienteId id1 = new ClienteId(1L);
            ClienteId id2 = new ClienteId(2L);

            assertNotEquals(id1, id2);
        }

        @Test
        @DisplayName("equals should be false for null")
        void equals_shouldBeFalseForNull() {
            ClienteId id = new ClienteId(1L);

            assertNotEquals(id, null);
        }

        @Test
        @DisplayName("equals should be false for different type")
        void equals_shouldBeFalseForDifferentType() {
            ClienteId id = new ClienteId(1L);

            assertNotEquals(id, "1");
        }
    }

    @Nested
    @DisplayName("ToString")
    class ToString {

        @Test
        @DisplayName("toString should return formatted value")
        void toString_shouldReturnFormattedValue() {
            ClienteId id = new ClienteId(1L);

            assertEquals("ClienteId(1)", id.toString());
        }
    }
}
