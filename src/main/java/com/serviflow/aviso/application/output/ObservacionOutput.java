package com.serviflow.aviso.application.output;

import com.serviflow.aviso.domain.entity.Observacion;

import java.time.LocalDateTime;

/**
 * Output record for observation data.
 */
public record ObservacionOutput(
    Long id,
    String contenido,
    String tipo,
    String usuario,
    LocalDateTime fechaCreacion
) {
    /**
     * Converts from domain Observacion entity.
     */
    public static ObservacionOutput from(Observacion observacion) {
        return new ObservacionOutput(
            observacion.id(),
            observacion.contenido(),
            observacion.tipo(),
            observacion.usuario(),
            observacion.fechaCreacion()
        );
    }
}