package com.serviflow.dashboard.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable value object representing an urgent aviso summary.
 * Used in the urgent list display (avisos with URGENTE priority).
 * 
 * Converted to Java record for automatic Jackson serialization.
 */
public record UrgentAvisoSummary(
    Long avisoId,
    String numeroCorrelativo,
    String clienteNombre,
    String estado,
    String tecnicoNombre,
    LocalDateTime fechaProgramada,
    String prioridad
) {
    /**
     * Canonical constructor with validation.
     */
    public UrgentAvisoSummary {
        Objects.requireNonNull(avisoId, "Aviso ID cannot be null");
        Objects.requireNonNull(numeroCorrelativo, "Correlativo cannot be null");
        Objects.requireNonNull(estado, "Estado cannot be null");
        Objects.requireNonNull(prioridad, "Prioridad cannot be null");
    }
}