package com.serviflow.user.domain.valueobject;

import com.serviflow.shared.domain.exception.DomainException;

import java.util.Objects;

/**
 * Value object representing a User's unique identifier.
 * Wraps a Long value and ensures it's positive.
 */
public final class UserId {

    private final Long value;

    public UserId(Long value) {
        Objects.requireNonNull(value, "UserId cannot be null");
        if (value <= 0) {
            throw new DomainException("UserId must be positive");
        }
        this.value = value;
    }

    /**
     * Factory method for creating a UserId from a Long value.
     */
    public static UserId of(Long value) {
        return new UserId(value);
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
        if (!(o instanceof UserId userId)) return false;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "UserId(" + value + ")";
    }
}
