package com.serviflow.aviso.application.input;

/**
 * Input record for listing avisos with filtering and pagination.
 */
public record ListAvisosInput(
    Long clienteId,
    Long tecnicoId,
    String estado,
    String prioridad,
    String search,
    int page,
    int size,
    String sortBy,
    String sortDir
) {}