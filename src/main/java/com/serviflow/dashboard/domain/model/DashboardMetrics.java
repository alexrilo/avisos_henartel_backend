package com.serviflow.dashboard.domain.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Immutable value object containing all dashboard metrics.
 * Computed via aggregation queries in the infrastructure layer.
 * 
 * Primary KPIs:
 * - pendientes: NUEVO + ASIGNADO
 * - asignados: ASIGNADO
 * - enCurso: EN_CURSO
 * - completadosHoy: COMPLETADO + fechaFin = today
 * - urgentesPendientes: URGENTE + not COMPLETADO/CANCELADO
 * - enSeguimiento: PENDIENTE_SEGUIMIENTO
 * - tecnicosActivos: Unique tecnicoId with work today
 * 
 * Secondary Metrics:
 * - creadosHoy: COUNT created today
 * - cerradosHoy: COUNT closed today
 * 
 * Converted to Java record for automatic Jackson serialization.
 */
public record DashboardMetrics(
    // Primary KPIs
    int pendientes,
    int asignados,
    int enCurso,
    int completadosHoy,
    int urgentesPendientes,
    int enSeguimiento,
    int tecnicosActivos,
    // Secondary Metrics
    int creadosHoy,
    int cerradosHoy,
    // Related data
    List<TechnicianWorkload> technicianWorkload,
    List<ScheduledJob> upcomingJobs,
    // Filters applied
    LocalDate startDate,
    LocalDate endDate
) {
    /**
     * Canonical constructor that ensures immutable copies of lists.
     */
    public DashboardMetrics {
        // Defensive copy for lists - record parameters are final
        technicianWorkload = technicianWorkload != null ? List.copyOf(technicianWorkload) : List.of();
        upcomingJobs = upcomingJobs != null ? List.copyOf(upcomingJobs) : List.of();
    }
}