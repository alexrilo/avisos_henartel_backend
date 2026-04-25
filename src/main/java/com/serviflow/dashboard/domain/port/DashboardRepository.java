package com.serviflow.dashboard.domain.port;

import com.serviflow.dashboard.domain.model.DashboardFilters;
import com.serviflow.dashboard.domain.model.DashboardMetrics;
import com.serviflow.dashboard.domain.model.ScheduledJob;
import com.serviflow.dashboard.domain.model.TechnicianWorkload;
import com.serviflow.dashboard.domain.model.TodayJobSummary;
import com.serviflow.dashboard.domain.model.UrgentAvisoSummary;

import java.util.List;

/**
 * Port interface for Dashboard aggregation queries.
 * Pure Java interface with no framework dependencies (Clean Architecture).
 * 
 * Implemented by infrastructure layer using JDBC or JPA.
 */
public interface DashboardRepository {

    /**
     * Gets all dashboard metrics aggregated by the given filters.
     * 
     * @param filters Date range and optional filters (technician, priority)
     * @return DashboardMetrics with all KPI counts and related data
     */
    DashboardMetrics getMetrics(DashboardFilters filters);

    /**
     * Gets the list of urgent avisos (URGENTE priority, not COMPLETADO/CANCELADO).
     * 
     * @param filters Date range and optional filters
     * @return List of urgent avisos (max 50)
     */
    List<UrgentAvisoSummary> getUrgentList(DashboardFilters filters);

    /**
     * Gets today's jobs list (fechaInicio = today OR estado = EN_CURSO).
     * 
     * @param filters Date range and optional filters
     * @return List of today's jobs (max 50), ordered by horaInicio ASC
     */
    List<TodayJobSummary> getTodayJobs(DashboardFilters filters);

    /**
     * Gets technician workload summary.
     * 
     * @param filters Date range and optional filters
     * @return List of TechnicianWorkload sorted by activeJobsCount DESC
     */
    List<TechnicianWorkload> getTechnicianWorkload(DashboardFilters filters);

    /**
     * Gets chart data grouped by estado.
     * 
     * @param filters Date range and optional filters
     * @return List of Object[] with [estado, count] pairs
     */
    List<Object[]> getChartByState(DashboardFilters filters);

    /**
     * Gets chart data grouped by technician.
     * 
     * @param filters Date range and optional filters
     * @return List of Object[] with [tecnicoId, tecnicoNombre, count] pairs
     */
    List<Object[]> getChartByTechnician(DashboardFilters filters);

    /**
     * Gets scheduled jobs within the date range.
     * 
     * @param filters Date range and optional filters
     * @return List of scheduled jobs
     */
    List<ScheduledJob> getScheduledJobs(DashboardFilters filters);
}