package com.serviflow.user.application.exception;

import com.serviflow.shared.domain.exception.DomainException;

/**
 * Exception thrown when user provides invalid credentials.
 */
public class InvalidCredentialsException extends DomainException {
    
    public InvalidCredentialsException() {
        super("Invalid credentials");
    }
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
