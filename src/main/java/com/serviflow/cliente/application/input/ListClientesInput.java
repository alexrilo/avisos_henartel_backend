package com.serviflow.cliente.application.input;

/**
 * Input record for listing clientes with filtering, pagination and sorting.
 * 
 * @param searchTerm optional text search (matches nombre or telefono)
 * @param estado optional status filter (ACTIVO or INACTIVO)
 * @param tipo optional type filter (PARTICULAR or EMPRESA)
 * @param page the page number (0-based)
 * @param size the page size
 * @param sortBy the field to sort by
 * @param sortDir the sort direction (ASC or DESC)
 */
public record ListClientesInput(
    String searchTerm,
    String estado,
    String tipo,
    int page,
    int size,
    String sortBy,
    String sortDir
) {
    /**
     * Compact constructor with validation and defaults.
     */
    public ListClientesInput {
        if (page < 0) page = 0;
        if (size < 1) size = 20;
        if (size > 100) size = 100;
        if (sortBy == null || sortBy.isBlank()) sortBy = "nombreOrazonSocial";
        if (sortDir == null || sortDir.isBlank()) sortDir = "ASC";
    }
}
