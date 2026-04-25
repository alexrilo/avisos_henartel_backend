package com.serviflow.shared.domain.exception;

/**
 * Base exception for domain errors.
 * All domain-specific exceptions should extend this class.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
