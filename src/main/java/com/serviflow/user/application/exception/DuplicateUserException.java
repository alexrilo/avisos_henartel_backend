package com.serviflow.user.application.exception;

import com.serviflow.shared.domain.exception.DomainException;

/**
 * Exception thrown when attempting to create a user with duplicate field values.
 */
public class DuplicateUserException extends DomainException {
    
    public DuplicateUserException(String field, String value) {
        super("User already exists with " + field + ": " + value);
    }
}
