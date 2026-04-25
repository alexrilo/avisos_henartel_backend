package com.serviflow.dashboard.application;

import com.serviflow.dashboard.application.input.DashboardInput;
import com.serviflow.dashboard.application.output.DashboardOutput;
import com.serviflow.dashboard.domain.model.DashboardFilters;
import com.serviflow.dashboard.domain.model.DashboardMetrics;
import com.serviflow.dashboard.domain.model.ScheduledJob;
import com.serviflow.dashboard.domain.model.TechnicianWorkload;
import com.serviflow.dashboard.domain.model.TodayJobSummary;
import com.serviflow.dashboard.domain.model.UrgentAvisoSummary;
import com.serviflow.dashboard.domain.port.DashboardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetDashboardMetricsUseCaseTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @InjectMocks
    private GetDashboardMetricsUseCase useCase;

    private DashboardMetrics mockMetrics;
    private DashboardFilters filters;

    @BeforeEach
    void setUp() {
        LocalDate today = LocalDate.now();
        List<TechnicianWorkload> workload = List.of(
            new TechnicianWorkload(1L, "Juan Pérez", 3)
        );
        List<ScheduledJob> upcomingJobs = List.of(
            new ScheduledJob(1L, "AV-001", "Cliente A", "Calle 123", 
                today.atStartOfDay(), 1L, "Juan Pérez")
        );

        mockMetrics = new DashboardMetrics(
            5, 2, 3, 1, 2, 1, 4,
            10, 1,
            workload, upcomingJobs,
            today, today
        );

        filters = new DashboardFilters(today, today, null, null);
    }

    @Test
    void shouldReturnDashboardOutputWithAllData() {
        // Given
        DashboardInput input = new DashboardInput(LocalDate.now(), LocalDate.now(), null, null);
        when(dashboardRepository.getMetrics(any())).thenReturn(mockMetrics);
        when(dashboardRepository.getUrgentList(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getTodayJobs(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getTechnicianWorkload(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getScheduledJobs(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getChartByState(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getChartByTechnician(any())).thenReturn(Collections.emptyList());

        // When
        DashboardOutput result = useCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.metrics()).isEqualTo(mockMetrics);
        assertThat(result.metrics().pendientes()).isEqualTo(5);
        assertThat(result.metrics().asignados()).isEqualTo(2);
        assertThat(result.metrics().enCurso()).isEqualTo(3);
        assertThat(result.metrics().completadosHoy()).isEqualTo(1);
        assertThat(result.metrics().urgentesPendientes()).isEqualTo(2);
        assertThat(result.metrics().enSeguimiento()).isEqualTo(1);
        assertThat(result.metrics().tecnicosActivos()).isEqualTo(4);
    }

    @Test
    void shouldReturnDashboardOutputWithEmptyLists() {
        // Given
        DashboardInput input = new DashboardInput(LocalDate.now(), LocalDate.now(), null, null);
        when(dashboardRepository.getMetrics(any())).thenReturn(mockMetrics);
        when(dashboardRepository.getUrgentList(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getTodayJobs(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getTechnicianWorkload(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getScheduledJobs(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getChartByState(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getChartByTechnician(any())).thenReturn(Collections.emptyList());

        // When
        DashboardOutput result = useCase.execute(input);

        // Then
        assertThat(result.urgentList()).isEmpty();
        assertThat(result.todayJobs()).isEmpty();
        assertThat(result.technicianWorkload()).isEmpty();
        assertThat(result.scheduledJobs()).isEmpty();
    }

    @Test
    void shouldTransformInputToFilters() {
        // Given - Input with null dates should apply defaults
        DashboardInput input = new DashboardInput(null, null, 1L, "URGENTE");
        when(dashboardRepository.getMetrics(any())).thenReturn(mockMetrics);
        when(dashboardRepository.getUrgentList(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getTodayJobs(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getTechnicianWorkload(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getScheduledJobs(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getChartByState(any())).thenReturn(Collections.emptyList());
        when(dashboardRepository.getChartByTechnician(any())).thenReturn(Collections.emptyList());

        // When
        DashboardOutput result = useCase.execute(input);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.metrics()).isEqualTo(mockMetrics);
    }
}