package com.serviflow.dashboard.application.output;

import com.serviflow.dashboard.domain.model.DashboardMetrics;
import com.serviflow.dashboard.domain.model.ScheduledJob;
import com.serviflow.dashboard.domain.model.TechnicianWorkload;
import com.serviflow.dashboard.domain.model.TodayJobSummary;
import com.serviflow.dashboard.domain.model.UrgentAvisoSummary;

import java.util.List;

/**
 * Output record for Dashboard queries.
 * Combines all domain models for presentation layer consumption.
 * 
 * @param metrics              Aggregated dashboard metrics (KPIs)
 * @param urgentList           List of urgent avisos (URGENTE priority)
 * @param todayJobs            List of jobs for today or in progress
 * @param technicianWorkload   Workload summary per technician
 * @param scheduledJobs        List of scheduled jobs in date range
 * @param chartByState         Chart data grouped by estado [estado, count]
 * @param chartByTechnician    Chart data grouped by technician [tecnicoId, tecnicoNombre, count]
 */
public record DashboardOutput(
    DashboardMetrics metrics,
    List<UrgentAvisoSummary> urgentList,
    List<TodayJobSummary> todayJobs,
    List<TechnicianWorkload> technicianWorkload,
    List<ScheduledJob> scheduledJobs,
    List<Object[]> chartByState,
    List<Object[]> chartByTechnician
) {}