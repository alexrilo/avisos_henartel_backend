package com.serviflow.aviso.application.input;

import java.time.LocalDateTime;

/**
 * Input record for rescheduling an Aviso.
 */
public record ReprogramarInput(
    Long avisoId,
    LocalDateTime nuevaFecha,
    Long nuevoTecnicoId,
    String usuario
) {}