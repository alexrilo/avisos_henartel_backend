package com.serviflow.cliente.application.exception;

import com.serviflow.shared.domain.exception.DomainException;

/**
 * Exception thrown when trying to create or update a Cliente with a duplicate phone number.
 */
public class DuplicateClienteException extends DomainException {

    public DuplicateClienteException(String field, String value) {
        super("Cliente already exists with " + field + ": " + value);
    }
}
