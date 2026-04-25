package com.serviflow.auth.application.output;

/**
 * Output record for current user profile response.
 */
public record UserOutput(
    Long id,
    String nombre,
    String apellido,
    String email,
    String role,
    Long tecnicoId
) {}
