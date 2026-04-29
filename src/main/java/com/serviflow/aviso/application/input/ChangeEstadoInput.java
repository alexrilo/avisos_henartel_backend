package com.serviflow.aviso.application.input;

/**
 * Input record for changing the estado of an Aviso.
 */
public record ChangeEstadoInput(
    Long avisoId,
    String estado,
    Long tecnicoId,
    String usuario,
    String observacion,
    String materialesUsados
) {}
