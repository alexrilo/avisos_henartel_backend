package com.serviflow.aviso.application.input;

/**
 * Input record for assigning a technician to an Aviso.
 */
public record AssignTecnicoInput(
    Long avisoId,
    Long tecnicoId,
    String usuario
) {}