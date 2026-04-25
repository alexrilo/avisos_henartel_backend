package com.serviflow.aviso.domain.valueobject;

/**
 * Enum representing the state of an Aviso.
 * Follows a state machine with defined transitions.
 */
public enum EstadoAviso {
    NUEVO,
    ASIGNADO,
    EN_CURSO,
    COMPLETADO,
    CANCELADO,
    PENDIENTE_SEGUIMIENTO;

    /**
     * Checks if this state is terminal (no further transitions possible).
     */
    public boolean isTerminal() {
        return this == COMPLETADO || this == CANCELADO;
    }

    /**
     * Validates if a transition to the target state is allowed.
     */
    public boolean canTransitionTo(EstadoAviso target) {
        return switch (this) {
            case NUEVO -> target == ASIGNADO || target == CANCELADO;
            case ASIGNADO -> target == EN_CURSO || target == CANCELADO;
            case EN_CURSO -> target == COMPLETADO || target == CANCELADO || target == PENDIENTE_SEGUIMIENTO;
            case PENDIENTE_SEGUIMIENTO -> target == ASIGNADO || target == CANCELADO;
            case COMPLETADO, CANCELADO -> false;
        };
    }
}
