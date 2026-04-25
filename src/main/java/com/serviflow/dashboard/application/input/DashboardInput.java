package com.serviflow.dashboard.application.input;

import java.time.LocalDate;

/**
 * Input record for Dashboard queries.
 * Contains filter criteria for dashboard metrics aggregation.
 * 
 * @param dateFrom  Start date for filtering (defaults to today if null)
 * @param dateTo    End date for filtering (defaults to today if null)
 * @param tecnicoId Optional technician ID filter
 * @param prioridad Optional priority filter (e.g., "URGENTE")
 */
public record DashboardInput(
    LocalDate dateFrom,
    LocalDate dateTo,
    Long tecnicoId,
    String prioridad
) {
    /**
     * Compact constructor with validation and default values.
     * Ensures date range is valid and provides sensible defaults.
     */
    public DashboardInput {
        // Apply defaults before validation
        LocalDate today = LocalDate.now();
        if (dateFrom == null) dateFrom = today;
        if (dateTo == null) dateTo = today;
        
        // Validate date range
        if (dateTo.isBefore(dateFrom)) {
            throw new IllegalArgumentException("dateTo cannot be before dateFrom");
        }
    }
}