package com.serviflow.aviso.application.output;

import com.serviflow.aviso.domain.valueobject.DireccionServicio;

/**
 * Output record for address data.
 */
public record DireccionOutput(
    String calle,
    String numero,
    String localidad,
    String provincia,
    String codigoPostal
) {
    /**
     * Converts from domain DireccionServicio value object.
     */
    public static DireccionOutput from(DireccionServicio direccion) {
        return new DireccionOutput(
            direccion.calle(),
            direccion.numero(),
            direccion.localidad(),
            direccion.provincia(),
            direccion.codigoPostal()
        );
    }
}