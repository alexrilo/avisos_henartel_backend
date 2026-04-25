package com.serviflow.aviso.application.input;

import java.time.LocalDateTime;

/**
 * Input record for creating a new Aviso.
 */
public record CreateAvisoInput(
    Long clienteId,
    String descripcion,
    String prioridad,
    String calle,
    String numero,
    String localidad,
    String provincia,
    String codigoPostal,
    LocalDateTime fechaProgramada,
    String usuario
) {}