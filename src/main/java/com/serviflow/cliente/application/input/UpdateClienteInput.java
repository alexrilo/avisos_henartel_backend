package com.serviflow.cliente.application.input;

/**
 * Input record for updating an existing Cliente.
 * 
 * @param id the cliente ID
 * @param nombreOrazonSocial the client name or business name
 * @param telefono the phone number
 * @param personaContacto the contact person (optional)
 * @param observaciones observations/notes (optional)
 */
public record UpdateClienteInput(
    Long id,
    String nombreOrazonSocial,
    String telefono,
    String personaContacto,
    String observaciones
) {}
