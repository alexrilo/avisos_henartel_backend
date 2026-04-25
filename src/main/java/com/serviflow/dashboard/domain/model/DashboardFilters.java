package com.serviflow.dashboard.domain.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Immutable value object representing dashboard filter criteria.
 * Used for filtering dashboard metrics and lists.
 * 
 * Converted to Java record for automatic Jackson serialization.
 */
public record DashboardFilters(
    LocalDate dateFrom,
    LocalDate dateTo,
    Long tecnicoId,
    String prioridad
) {
    /**
     * Canonical constructor with validation.
     */
    public DashboardFilters {
        Objects.requireNonNull(dateFrom, "dateFrom cannot be null");
        Objects.requireNonNull(dateTo, "dateTo cannot be null");
        
        if (dateTo.isBefore(dateFrom)) {
            throw new IllegalArgumentException("dateTo must be >= dateFrom");
        }
    }

    /**
     * Creates a DashboardFilters with required date range only.
     */
    public DashboardFilters(LocalDate dateFrom, LocalDate dateTo) {
        this(dateFrom, dateTo, null, null);
    }

    /**
     * Checks if a technician filter is applied.
     */
    public boolean hasTechnicianFilter() {
        return tecnicoId != null;
    }

    /**
     * Checks if a priority filter is applied.
     */
    public boolean hasPriorityFilter() {
        return prioridad != null && !prioridad.isBlank();
    }
}