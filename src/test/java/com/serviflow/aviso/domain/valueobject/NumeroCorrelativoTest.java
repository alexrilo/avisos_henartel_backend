package com.serviflow.aviso.domain.valueobject;

import com.serviflow.shared.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for NumeroCorrelativo value object.
 */
class NumeroCorrelativoTest {

    @Nested
    @DisplayName("Constructor Validation")
    class ConstructorValidation {

        @Test
        @DisplayName("should accept valid format")
        void shouldAcceptValidFormat() {
            NumeroCorrelativo correlativo = new NumeroCorrelativo("AVI-2026-0001");

            assertEquals("AVI-2026-0001", correlativo.value());
        }

        @Test
        @DisplayName("should accept format with more than 4 digits in sequence")
        void shouldAcceptMoreThanFourDigitsInSequence() {
            NumeroCorrelativo correlativo = new NumeroCorrelativo("AVI-2026-12345");

            assertEquals("AVI-2026-12345", correlativo.value());
        }

        @Test
        @DisplayName("should reject null value")
        void shouldRejectNullValue() {
            assertThrows(NullPointerException.class, () ->
                new NumeroCorrelativo(null)
            );
        }

        @Test
        @DisplayName("should reject invalid format - missing AVI prefix")
        void shouldRejectMissingPrefix() {
            assertThrows(DomainException.class, () ->
                new NumeroCorrelativo("2026-0001")
            );
        }

        @Test
        @DisplayName("should reject invalid format - missing hyphen")
        void shouldRejectMissingHyphen() {
            assertThrows(DomainException.class, () ->
                new NumeroCorrelativo("AVI20260001")
            );
        }

        @Test
        @DisplayName("should reject invalid format - wrong year")
        void shouldRejectWrongYear() {
            assertThrows(DomainException.class, () ->
                new NumeroCorrelativo("AVI-26-0001")
            );
        }

        @Test
        @DisplayName("should reject invalid format - zero sequence")
        void shouldRejectZeroSequence() {
            assertThrows(DomainException.class, () ->
                new NumeroCorrelativo("AVI-2026-0000")
            );
        }

        @Test
        @DisplayName("should reject invalid format - negative sequence")
        void shouldRejectNegativeSequence() {
            assertThrows(DomainException.class, () ->
                new NumeroCorrelativo("AVI-2026--001")
            );
        }

        @Test
        @DisplayName("should reject invalid format - letters in sequence")
        void shouldRejectLettersInSequence() {
            assertThrows(DomainException.class, () ->
                new NumeroCorrelativo("AVI-2026-00AB")
            );
        }
    }

    @Nested
    @DisplayName("Factory Method")
    class FactoryMethod {

        @Test
        @DisplayName("generate should create valid correlativo")
        void generate_shouldCreateValidCorrelativo() {
            NumeroCorrelativo correlativo = NumeroCorrelativo.generate(2026, 1);

            assertEquals("AVI-2026-0001", correlativo.value());
        }

        @Test
        @DisplayName("generate should handle large sequence numbers")
        void generate_shouldHandleLargeSequence() {
            NumeroCorrelativo correlativo = NumeroCorrelativo.generate(2026, 12345);

            assertEquals("AVI-2026-12345", correlativo.value());
        }

        @Test
        @DisplayName("generate should reject invalid year too low")
        void generate_shouldRejectYearTooLow() {
            assertThrows(DomainException.class, () ->
                NumeroCorrelativo.generate(1999, 1)
            );
        }

        @Test
        @DisplayName("generate should reject invalid year too high")
        void generate_shouldRejectYearTooHigh() {
            assertThrows(DomainException.class, () ->
                NumeroCorrelativo.generate(2101, 1)
            );
        }

        @Test
        @DisplayName("generate should reject zero sequence")
        void generate_shouldRejectZeroSequence() {
            assertThrows(DomainException.class, () ->
                NumeroCorrelativo.generate(2026, 0)
            );
        }

        @Test
        @DisplayName("generate should reject negative sequence")
        void generate_shouldRejectNegativeSequence() {
            assertThrows(DomainException.class, () ->
                NumeroCorrelativo.generate(2026, -1)
            );
        }
    }

    @Nested
    @DisplayName("Extraction Methods")
    class ExtractionMethods {

        @Test
        @DisplayName("year should extract year correctly")
        void year_shouldExtractYearCorrectly() {
            NumeroCorrelativo correlativo = new NumeroCorrelativo("AVI-2026-0001");

            assertEquals(2026, correlativo.year());
        }

        @Test
        @DisplayName("sequence should extract sequence correctly")
        void sequence_shouldExtractSequenceCorrectly() {
            NumeroCorrelativo correlativo = new NumeroCorrelativo("AVI-2026-1234");

            assertEquals(1234, correlativo.sequence());
        }

        @Test
        @DisplayName("sequence should handle large sequence numbers")
        void sequence_shouldHandleLargeSequence() {
            NumeroCorrelativo correlativo = new NumeroCorrelativo("AVI-2026-12345");

            assertEquals(12345, correlativo.sequence());
        }
    }

    @Nested
    @DisplayName("Object Methods")
    class ObjectMethods {

        @Test
        @DisplayName("equals should be true for same value")
        void equals_shouldBeTrueForSameValue() {
            NumeroCorrelativo c1 = new NumeroCorrelativo("AVI-2026-0001");
            NumeroCorrelativo c2 = new NumeroCorrelativo("AVI-2026-0001");

            assertEquals(c1, c2);
        }

        @Test
        @DisplayName("equals should be false for different value")
        void equals_shouldBeFalseForDifferentValue() {
            NumeroCorrelativo c1 = new NumeroCorrelativo("AVI-2026-0001");
            NumeroCorrelativo c2 = new NumeroCorrelativo("AVI-2026-0002");

            assertNotEquals(c1, c2);
        }

        @Test
        @DisplayName("hashCode should be consistent")
        void hashCode_shouldBeConsistent() {
            NumeroCorrelativo c1 = new NumeroCorrelativo("AVI-2026-0001");
            NumeroCorrelativo c2 = new NumeroCorrelativo("AVI-2026-0001");

            assertEquals(c1.hashCode(), c2.hashCode());
        }

        @Test
        @DisplayName("toString should return value")
        void toString_shouldReturnValue() {
            NumeroCorrelativo correlativo = new NumeroCorrelativo("AVI-2026-0001");

            assertEquals("AVI-2026-0001", correlativo.toString());
        }
    }
}
