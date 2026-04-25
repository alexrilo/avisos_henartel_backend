package com.serviflow.aviso.application.exception;

import com.serviflow.shared.domain.exception.DomainException;

/**
 * Exception thrown when a Cliente is not found.
 */
public class ClienteNotFoundException extends DomainException {

    public ClienteNotFoundException(Long id) {
        super("Cliente not found with id: " + id);
    }
}