package com.serviflow.cliente.domain.valueobject;

/**
 * Enum representing the type of Client.
 */
public enum TipoCliente {
    PARTICULAR,
    EMPRESA;

    /**
     * Parses a string value to TipoCliente.
     * 
     * @param value the string to parse
     * @return the corresponding TipoCliente
     * @throws IllegalArgumentException if the value is invalid
     */
    public static TipoCliente from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("TipoCliente cannot be null or blank");
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("TipoCliente inválido: " + value);
        }
    }
}
