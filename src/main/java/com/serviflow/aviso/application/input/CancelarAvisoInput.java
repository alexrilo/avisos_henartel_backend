package com.serviflow.aviso.application.input;

/**
 * Input record for cancelling an Aviso.
 */
public record CancelarAvisoInput(
    Long avisoId,
    String usuario
) {}