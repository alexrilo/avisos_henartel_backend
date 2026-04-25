package com.serviflow.dashboard.infrastructure.web;

import com.serviflow.dashboard.application.GetDashboardMetricsUseCase;
import com.serviflow.dashboard.application.input.DashboardInput;
import com.serviflow.dashboard.application.output.DashboardOutput;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * REST controller for Dashboard operations.
 * This is a thin web layer that delegates to use cases.
 * 
 * Security: Only ADMINISTRADOR and COORDINADOR roles can access dashboard.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final GetDashboardMetricsUseCase getDashboardMetricsUseCase;

    public DashboardController(GetDashboardMetricsUseCase getDashboardMetricsUseCase) {
        this.getDashboardMetricsUseCase = getDashboardMetricsUseCase;
    }

    /**
     * Gets dashboard metrics and related data.
     * GET /api/dashboard
     * 
     * Query parameters:
     * - dateFrom: Start date (optional, defaults to today)
     * - dateTo: End date (optional, defaults to today)
     * - tecnicoId: Optional technician ID filter
     * - prioridad: Optional priority filter (e.g., "URGENTE")
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<DashboardOutput> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) String prioridad) {
        
        DashboardInput input = new DashboardInput(dateFrom, dateTo, tecnicoId, prioridad);
        DashboardOutput output = getDashboardMetricsUseCase.execute(input);
        
        return ResponseEntity.ok(output);
    }
}