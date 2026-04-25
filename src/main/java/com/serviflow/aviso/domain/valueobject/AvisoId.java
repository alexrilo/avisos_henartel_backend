package com.serviflow.aviso.domain.valueobject;

import com.serviflow.shared.domain.exception.DomainException;

import java.util.Objects;

/**
 * Value object representing an Aviso's unique identifier.
 * Wraps a Long value and ensures it's positive.
 */
public final class AvisoId {

    private final Long value;

    public AvisoId(Long value) {
        Objects.requireNonNull(value, "AvisoId cannot be null");
        if (value <= 0) {
            throw new DomainException("AvisoId must be positive");
        }
        this.value = value;
    }

    /**
     * Factory method for creating an AvisoId from a Long value.
     */
    public static AvisoId of(Long value) {
        return new AvisoId(value);
    }

    /**
     * Returns the underlying Long value.
     */
    public Long value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AvisoId avisoId)) return false;
        return Objects.equals(value, avisoId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "AvisoId(" + value + ")";
    }
}
