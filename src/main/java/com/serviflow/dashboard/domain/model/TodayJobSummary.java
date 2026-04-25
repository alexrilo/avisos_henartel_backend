package com.serviflow.dashboard.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable value object representing today's job summary.
 * Used in the today's jobs list display.
 * 
 * Converted to Java record for automatic Jackson serialization.
 */
public record TodayJobSummary(
    Long avisoId,
    String numeroCorrelativo,
    String clienteNombre,
    String direccion,
    String tecnicoNombre,
    String estado,
    LocalDateTime horaInicio,
    String prioridad
) {
    /**
     * Canonical constructor with validation.
     */
    public TodayJobSummary {
        Objects.requireNonNull(avisoId, "Aviso ID cannot be null");
        Objects.requireNonNull(numeroCorrelativo, "Correlativo cannot be null");
        Objects.requireNonNull(estado, "Estado cannot be null");
        Objects.requireNonNull(prioridad, "Prioridad cannot be null");
    }
}