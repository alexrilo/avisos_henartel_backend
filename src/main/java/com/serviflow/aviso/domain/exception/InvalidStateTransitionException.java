package com.serviflow.aviso.domain.exception;

import com.serviflow.aviso.domain.valueobject.EstadoAviso;
import com.serviflow.shared.domain.exception.DomainException;

/**
 * Exception thrown when an invalid state transition is attempted.
 */
public class InvalidStateTransitionException extends DomainException {

    public InvalidStateTransitionException(EstadoAviso from, EstadoAviso to) {
        super("Invalid state transition: " + from + " → " + to);
    }

    public InvalidStateTransitionException(String message) {
        super(message);
    }
}
