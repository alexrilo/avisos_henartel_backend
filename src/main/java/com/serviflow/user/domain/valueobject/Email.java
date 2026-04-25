package com.serviflow.user.domain.valueobject;

import com.serviflow.shared.domain.exception.DomainException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing an Email address.
 * Self-validates the email format in the constructor.
 */
public final class Email {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    
    private final String value;

    public Email(String value) {
        Objects.requireNonNull(value, "Email cannot be null");
        if (value.isBlank()) {
            throw new DomainException("Email cannot be blank");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new DomainException("Invalid email format: " + value);
        }
        this.value = value;
    }

    /**
     * Factory method for creating an Email from a String.
     */
    public static Email of(String value) {
        return new Email(value);
    }

    /**
     * Returns the underlying String value.
     */
    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return Objects.equals(value, email.value);
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
