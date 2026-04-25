package com.serviflow.aviso.domain.valueobject;

import com.serviflow.shared.domain.exception.DomainException;

import java.util.Objects;

/**
 * Value object representing a service address.
 * Immutable and self-validating.
 */
public final class DireccionServicio {

    private final String calle;
    private final String numero;
    private final String localidad;
    private final String provincia;
    private final String codigoPostal;

    public DireccionServicio(String calle, String numero, String localidad, 
                              String provincia, String codigoPostal) {
        this.calle = validate(calle, "Calle");
        this.numero = validate(numero, "Número");
        this.localidad = validate(localidad, "Localidad");
        this.provincia = validate(provincia, "Provincia");
        this.codigoPostal = validate(codigoPostal, "Código postal");
    }

    private static String validate(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");
        if (value.isBlank()) {
            throw new DomainException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    // Getters only - no setters for immutability
    public String calle() {
        return calle;
    }

    public String numero() {
        return numero;
    }

    public String localidad() {
        return localidad;
    }

    public String provincia() {
        return provincia;
    }

    public String codigoPostal() {
        return codigoPostal;
    }

    @Override
    public String toString() {
        return calle + " " + numero + ", " + localidad + ", " + provincia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DireccionServicio that)) return false;
        return Objects.equals(calle, that.calle) &&
               Objects.equals(numero, that.numero) &&
               Objects.equals(localidad, that.localidad) &&
               Objects.equals(provincia, that.provincia) &&
               Objects.equals(codigoPostal, that.codigoPostal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calle, numero, localidad, provincia, codigoPostal);
    }
}
