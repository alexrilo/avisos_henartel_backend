package com.serviflow.shared.domain.exception;

/**
 * Exception thrown when an entity is not found in the domain.
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
