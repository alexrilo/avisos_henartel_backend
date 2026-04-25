package com.serviflow.dashboard.domain.model;

import java.util.Objects;

/**
 * Immutable value object representing a technician's workload.
 * Used in the technician workload table, sorted by active job count descending.
 * 
 * Converted to Java record for automatic Jackson serialization.
 */
public record TechnicianWorkload(
    Long tecnicoId,
    String nombre,
    int activeJobsCount
) {
    /**
     * Canonical constructor with validation.
     */
    public TechnicianWorkload {
        Objects.requireNonNull(tecnicoId, "Technician ID cannot be null");
        Objects.requireNonNull(nombre, "Technician name cannot be null");
    }
}