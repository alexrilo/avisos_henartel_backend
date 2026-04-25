package com.serviflow.aviso.application.input;

/**
 * Input record for updating an existing Aviso.
 */
public record UpdateAvisoInput(
    Long avisoId,
    String descripcion,
    String prioridad,
    String calle,
    String numero,
    String localidad,
    String provincia,
    String codigoPostal,
    java.time.LocalDateTime fechaProgramada,
    String usuario
) {}