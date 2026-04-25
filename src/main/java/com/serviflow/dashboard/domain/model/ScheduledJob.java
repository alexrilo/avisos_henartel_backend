package com.serviflow.dashboard.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable value object representing an upcoming scheduled job.
 * Used in the upcoming jobs list.
 * 
 * Converted to Java record for automatic Jackson serialization.
 */
public record ScheduledJob(
    Long avisoId,
    String numeroCorrelativo,
    String clienteNombre,
    String direccion,
    LocalDateTime fechaProgramada,
    Long tecnicoId,
    String tecnicoNombre
) {
    /**
     * Canonical constructor with validation.
     */
    public ScheduledJob {
        Objects.requireNonNull(avisoId, "Aviso ID cannot be null");
        Objects.requireNonNull(numeroCorrelativo, "Correlativo cannot be null");
        Objects.requireNonNull(fechaProgramada, "Scheduled date cannot be null");
    }
}