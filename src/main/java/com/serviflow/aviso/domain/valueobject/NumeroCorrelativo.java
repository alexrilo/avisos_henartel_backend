package com.serviflow.aviso.domain.valueobject;

import com.serviflow.shared.domain.exception.DomainException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing the correlative number for an Aviso.
 * Format: AVI-YYYY-NNNN (e.g., AVI-2026-0001)
 */
public final class NumeroCorrelativo {

    private static final Pattern FORMAT = Pattern.compile("^AVI-\\d{4}-\\d{4,}$");
    private final String value;

    public NumeroCorrelativo(String value) {
        Objects.requireNonNull(value, "NumeroCorrelativo cannot be null");
        if (!FORMAT.matcher(value).matches()) {
            throw new DomainException("Invalid correlativo format: " + value + ". Expected: AVI-YYYY-NNNN");
        }
        // Validate sequence is not zero
        int seq = Integer.parseInt(value.substring(9));
        if (seq == 0) {
            throw new DomainException("Sequence cannot be zero");
        }
        this.value = value;
    }

    /**
     * Factory method for generating a new correlativo.
     */
    public static NumeroCorrelativo generate(int year, int sequence) {
        if (year < 2000 || year > 2100) {
            throw new DomainException("Invalid year: " + year);
        }
        if (sequence < 1) {
            throw new DomainException("Sequence must be positive");
        }
        return new NumeroCorrelativo(String.format("AVI-%04d-%04d", year, sequence));
    }

    /**
     * Returns the underlying string value.
     */
    public String value() {
        return value;
    }

    /**
     * Extracts the year from the correlativo.
     */
    public int year() {
        return Integer.parseInt(value.substring(4, 8));
    }

    /**
     * Extracts the sequence number from the correlativo.
     */
    public int sequence() {
        return Integer.parseInt(value.substring(9));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NumeroCorrelativo that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
