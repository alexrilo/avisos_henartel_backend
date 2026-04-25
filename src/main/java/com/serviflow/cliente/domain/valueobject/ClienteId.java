package com.serviflow.cliente.domain.valueobject;

import com.serviflow.shared.domain.exception.DomainException;

import java.util.Objects;

/**
 * Value object representing a Cliente's unique identifier.
 * Wraps a Long value and ensures it's positive.
 */
public final class ClienteId {

    private final Long value;

    public ClienteId(Long value) {
        Objects.requireNonNull(value, "ClienteId cannot be null");
        if (value <= 0) {
            throw new DomainException("ClienteId must be positive");
        }
        this.value = value;
    }

    /**
     * Factory method for creating a ClienteId from a Long value.
     */
    public static ClienteId of(Long value) {
        return new ClienteId(value);
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
        if (!(o instanceof ClienteId clienteId)) return false;
        return Objects.equals(value, clienteId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ClienteId(" + value + ")";
    }
}
