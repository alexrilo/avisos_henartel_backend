package com.serviflow.dashboard.application;

import com.serviflow.dashboard.application.input.DashboardInput;
import com.serviflow.dashboard.application.output.DashboardOutput;
import com.serviflow.dashboard.domain.model.DashboardFilters;
import com.serviflow.dashboard.domain.port.DashboardRepository;

/**
 * Use Case for retrieving dashboard metrics and related data.
 * Orchestrates the aggregation of KPIs, lists, and chart data.
 * 
 * This is a pure application layer component - no framework dependencies.
 * Follows Clean Architecture: orchestrates domain entities without implementing business logic.
 */
public class GetDashboardMetricsUseCase {

    private final DashboardRepository dashboardRepository;

    /**
     * Constructs the Use Case with required repository dependency.
     * 
     * @param dashboardRepository Repository port for dashboard queries
     */
    public GetDashboardMetricsUseCase(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    /**
     * Executes the dashboard metrics aggregation.
     * 
     * @param input Filter criteria for dashboard queries
     * @return DashboardOutput containing all aggregated data
     */
    public DashboardOutput execute(DashboardInput input) {
        // Transform input to domain filters
        DashboardFilters filters = new DashboardFilters(
            input.dateFrom(),
            input.dateTo(),
            input.tecnicoId(),
            input.prioridad()
        );

        // Orchestrate all repository queries
        return new DashboardOutput(
            dashboardRepository.getMetrics(filters),
            dashboardRepository.getUrgentList(filters),
            dashboardRepository.getTodayJobs(filters),
            dashboardRepository.getTechnicianWorkload(filters),
            dashboardRepository.getScheduledJobs(filters),
            dashboardRepository.getChartByState(filters),
            dashboardRepository.getChartByTechnician(filters)
        );
    }
}