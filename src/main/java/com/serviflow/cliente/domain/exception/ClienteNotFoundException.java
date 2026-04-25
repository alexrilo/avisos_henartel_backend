package com.serviflow.cliente.domain.exception;

import com.serviflow.shared.domain.exception.EntityNotFoundException;

/**
 * Exception thrown when a Cliente is not found.
 */
public class ClienteNotFoundException extends EntityNotFoundException {

    public ClienteNotFoundException(Long id) {
        super("Cliente no encontrado con id: " + id);
    }

    public ClienteNotFoundException(String message) {
        super(message);
    }
}
