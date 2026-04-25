package com.serviflow.cliente.domain.valueobject;

/**
 * Enum representing the status of a Cliente.
 */
public enum ClienteStatus {
    ACTIVO,
    INACTIVO;

    /**
     * Toggles the status between ACTIVO and INACTIVO.
     * 
     * @return the toggled status
     */
    public ClienteStatus toggle() {
        return this == ACTIVO ? INACTIVO : ACTIVO;
    }

    /**
     * Parses a string value to ClienteStatus.
     * 
     * @param value the string to parse
     * @return the corresponding ClienteStatus
     * @throws IllegalArgumentException if the value is invalid
     */
    public static ClienteStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("ClienteStatus cannot be null or blank");
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ClienteStatus inválido: " + value);
        }
    }
}
