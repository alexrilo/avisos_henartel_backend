package com.serviflow.cliente.application.input;

/**
 * Input record for creating a new Cliente.
 * 
 * @param tipo the client type (PARTICULAR or EMPRESA)
 * @param nombreOrazonSocial the client name or business name
 * @param telefono the phone number
 * @param personaContacto the contact person (optional)
 * @param observaciones observations/notes (optional)
 */
public record CreateClienteInput(
    String tipo,
    String nombreOrazonSocial,
    String telefono,
    String personaContacto,
    String observaciones
) {}
