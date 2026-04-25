package com.serviflow.aviso.domain.exception;

import com.serviflow.shared.domain.exception.EntityNotFoundException;

/**
 * Exception thrown when an Aviso is not found.
 */
public class AvisoNotFoundException extends EntityNotFoundException {

    public AvisoNotFoundException(Long id) {
        super("Aviso not found with id: " + id);
    }

    public AvisoNotFoundException(String numero) {
        super("Aviso not found with number: " + numero);
    }
}
