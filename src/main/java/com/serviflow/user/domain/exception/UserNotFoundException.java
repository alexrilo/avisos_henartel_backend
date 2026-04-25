package com.serviflow.user.domain.exception;

import com.serviflow.shared.domain.exception.EntityNotFoundException;

/**
 * Exception thrown when a User is not found.
 */
public class UserNotFoundException extends EntityNotFoundException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }

    public UserNotFoundException(String field, String value) {
        super("User not found with " + field + ": " + value);
    }
}
