package com.serviflow.dashboard.infrastructure.persistence;

import com.serviflow.dashboard.domain.model.DashboardFilters;
import com.serviflow.dashboard.domain.model.DashboardMetrics;
import com.serviflow.dashboard.domain.model.ScheduledJob;
import com.serviflow.dashboard.domain.model.TechnicianWorkload;
import com.serviflow.dashboard.domain.model.TodayJobSummary;
import com.serviflow.dashboard.domain.model.UrgentAvisoSummary;
import com.serviflow.dashboard.domain.port.DashboardRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Adapter that implements the DashboardRepository port using JdbcTemplate.
 * Uses direct SQL queries for optimal aggregation performance.
 * 
 * This is the Infrastructure layer implementation of the Domain port.
 */
@Repository
public class DashboardRepositoryAdapter implements DashboardRepository {

    private static final Logger log = LoggerFactory.getLogger(DashboardRepositoryAdapter.class);

    private final JdbcTemplate jdbcTemplate;

    public DashboardRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DashboardMetrics getMetrics(DashboardFilters filters) {
        log.debug("getMetrics with filters: dateFrom={}, dateTo={}", filters.dateFrom(), filters.dateTo());
        
        LocalDate dateFrom = filters.dateFrom();
        LocalDate dateTo = filters.dateTo();

        // Primary KPIs
        int pendientes = countByEstados(filters, List.of("NUEVO", "ASIGNADO"));
        int asignados = countByEstado(filters, "ASIGNADO");
        int enCurso = countByEstado(filters, "EN_CURSO");
        int completadosHoy = countCompletadosHoy(filters);
        int urgentesPendientes = countUrgentesPendientes(filters);
        int enSeguimiento = countByEstado(filters, "PENDIENTE_SEGUIMIENTO");
        int tecnicosActivos = countTecnicosActivos(filters);

        // Secondary Metrics
        int creadosHoy = countCreadosHoy(filters);
        int cerradosHoy = countCerradosHoy(filters);

        // Related data - use cached queries
        List<TechnicianWorkload> technicianWorkload = getTechnicianWorkload(filters);
        List<ScheduledJob> upcomingJobs = getScheduledJobs(filters);

        return new DashboardMetrics(
            pendientes,
            asignados,
            enCurso,
            completadosHoy,
            urgentesPendientes,
            enSeguimiento,
            tecnicosActivos,
            creadosHoy,
            cerradosHoy,
            technicianWorkload,
            upcomingJobs,
            dateFrom,
            dateTo
        );
    }

    @Override
    public List<UrgentAvisoSummary> getUrgentList(DashboardFilters filters) {
        log.debug("getUrgentList with filters: {}", filters);
        
        StringBuilder sql = new StringBuilder("""
            SELECT a.id, a.numero_correlativo, c.nombre_o_razon_social as cliente_nombre, 
                   a.estado, u.nombre as tecnico_nombre, a.fecha_programada, a.prioridad
            FROM avisos a
            LEFT JOIN clientes c ON a.cliente_id = c.id
            LEFT JOIN usuarios u ON a.tecnico_id = u.id
            WHERE a.prioridad = 'URGENTE'
            AND a.estado NOT IN ('COMPLETADO', 'CANCELADO')
            """);
        
        applyDateRangeParams(sql, filters);
        
        sql.append(" ORDER BY a.fecha_programada ASC NULLS LAST LIMIT 50");

        return jdbcTemplate.query(sql.toString(), this::mapToUrgentAvisoSummary, 
            filters.dateFrom(), filters.dateTo());
    }

    @Override
    public List<TodayJobSummary> getTodayJobs(DashboardFilters filters) {
        log.debug("getTodayJobs with filters: {}", filters);
        
        LocalDate today = LocalDate.now();
        
        StringBuilder sql = new StringBuilder("""
            SELECT a.id, a.numero_correlativo, c.nombre_o_razon_social as cliente_nombre, 
                   CONCAT(a.calle, ' ', a.numero) as cliente_direccion, u.nombre as tecnico_nombre,
                   a.estado, a.fecha_inicio as hora_inicio, a.prioridad
            FROM avisos a
            LEFT JOIN clientes c ON a.cliente_id = c.id
            LEFT JOIN usuarios u ON a.tecnico_id = u.id
            WHERE (a.fecha_inicio IS NULL OR DATE(a.fecha_inicio) = ? OR a.estado = 'EN_CURSO')
            AND a.estado NOT IN ('COMPLETADO', 'CANCELADO')
            """);
        
        // Build dynamic parameter list
        List<Object> params = new java.util.ArrayList<>();
        params.add(today);
        
        applyTechnicianFilter(sql, filters, params);
        applyPriorityFilter(sql, filters, params);
        
        sql.append(" ORDER BY a.fecha_inicio ASC NULLS LAST LIMIT 50");

        return jdbcTemplate.query(sql.toString(), this::mapToTodayJobSummary, 
            params.toArray());
    }

    @Override
    public List<TechnicianWorkload> getTechnicianWorkload(DashboardFilters filters) {
        log.debug("getTechnicianWorkload with filters: {}", filters);
        
        StringBuilder sql = new StringBuilder("""
            SELECT u.id as tecnico_id, u.nombre, COUNT(a.id) as active_jobs_count
            FROM usuarios u
            LEFT JOIN avisos a ON u.id = a.tecnico_id 
                AND a.estado NOT IN ('COMPLETADO', 'CANCELADO')
            WHERE u.rol_id = (SELECT id FROM roles WHERE nombre = 'TECNICO')
            AND u.activo = true
            """);
        
        List<Object> params = new java.util.ArrayList<>();
        applyTechnicianFilter(sql, filters, params);
        
        sql.append(" GROUP BY u.id, u.nombre ORDER BY active_jobs_count DESC");

        return jdbcTemplate.query(sql.toString(), this::mapToTechnicianWorkload,
            params.toArray());
    }

    @Override
    public List<Object[]> getChartByState(DashboardFilters filters) {
        log.debug("getChartByState with filters: {}", filters);
        
        StringBuilder sql = new StringBuilder("""
            SELECT a.estado, COUNT(*) as count
            FROM avisos a
            WHERE 1=1
            """);
        
        applyDateRangeConditions(sql, filters);
        
        sql.append(" GROUP BY a.estado ORDER BY count DESC");

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new Object[]{
            rs.getString("estado"),
            rs.getLong("count")
        }, filters.dateFrom(), filters.dateTo());
    }

    @Override
    public List<Object[]> getChartByTechnician(DashboardFilters filters) {
        log.debug("getChartByTechnician with filters: {}", filters);
        
        StringBuilder sql = new StringBuilder("""
            SELECT u.id as tecnico_id, u.nombre as tecnico_nombre, COUNT(a.id) as count
            FROM usuarios u
            LEFT JOIN avisos a ON u.id = a.tecnico_id
            WHERE u.rol_id = (SELECT id FROM roles WHERE nombre = 'TECNICO')
            AND u.activo = true
            """);
        
        applyDateRangeConditions(sql, filters);
        
        sql.append(" GROUP BY u.id, u.nombre ORDER BY count DESC");

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new Object[]{
            rs.getLong("tecnico_id"),
            rs.getString("tecnico_nombre"),
            rs.getLong("count")
        }, filters.dateFrom(), filters.dateTo());
    }

    @Override
    public List<ScheduledJob> getScheduledJobs(DashboardFilters filters) {
        log.debug("getScheduledJobs with filters: {}", filters);
        
        StringBuilder sql = new StringBuilder("""
            SELECT a.id, a.numero_correlativo, c.nombre_o_razon_social as cliente_nombre,
                   CONCAT(a.calle, ' ', a.numero) as direccion,
                   a.fecha_programada, a.tecnico_id, u.nombre as tecnico_nombre
            FROM avisos a
            LEFT JOIN clientes c ON a.cliente_id = c.id
            LEFT JOIN usuarios u ON a.tecnico_id = u.id
            WHERE a.fecha_programada IS NOT NULL
            AND a.estado NOT IN ('COMPLETADO', 'CANCELADO')
            AND DATE(a.fecha_programada) BETWEEN ? AND ?
            """);
        
        List<Object> params = new java.util.ArrayList<>();
        params.add(filters.dateFrom());
        params.add(filters.dateTo());
        applyTechnicianFilter(sql, filters, params);
        applyPriorityFilter(sql, filters, params);
        
        sql.append(" ORDER BY a.fecha_programada ASC LIMIT 20");

        return jdbcTemplate.query(sql.toString(), this::mapToScheduledJob,
            params.toArray());
    }

    // ==================== Private Helper Methods ====================

    private int countByEstado(DashboardFilters filters, String estado) {
        String sql = """
            SELECT COUNT(*) FROM avisos a
            WHERE a.estado = ?
            AND DATE(a.fecha_creacion) BETWEEN ? AND ?
            """;
        return jdbcTemplate.queryForObject(sql, Integer.class, estado, 
            filters.dateFrom(), filters.dateTo());
    }

    private int countByEstados(DashboardFilters filters, List<String> estados) {
        if (estados.isEmpty()) return 0;
        
        String placeholders = String.join(",", estados.stream()
            .map(s -> "?").toList());
        String sql = String.format("""
            SELECT COUNT(*) FROM avisos a
            WHERE a.estado IN (%s)
            AND DATE(a.fecha_creacion) BETWEEN ? AND ?
            """, placeholders);
        
        Object[] params = new Object[estados.size() + 2];
        for (int i = 0; i < estados.size(); i++) {
            params[i] = estados.get(i);
        }
        params[estados.size()] = filters.dateFrom();
        params[estados.size() + 1] = filters.dateTo();
        
        return jdbcTemplate.queryForObject(sql, Integer.class, params);
    }

    private int countCompletadosHoy(DashboardFilters filters) {
        LocalDate today = LocalDate.now();
        String sql = """
            SELECT COUNT(*) FROM avisos a
            WHERE a.estado = 'COMPLETADO'
            AND DATE(a.fecha_fin) = ?
            """;
        return jdbcTemplate.queryForObject(sql, Integer.class, today);
    }

    private int countUrgentesPendientes(DashboardFilters filters) {
        String sql = """
            SELECT COUNT(*) FROM avisos a
            WHERE a.prioridad = 'URGENTE'
            AND a.estado NOT IN ('COMPLETADO', 'CANCELADO')
            AND DATE(a.fecha_creacion) BETWEEN ? AND ?
            """;
        return jdbcTemplate.queryForObject(sql, Integer.class, 
            filters.dateFrom(), filters.dateTo());
    }

    private int countTecnicosActivos(DashboardFilters filters) {
        String sql = """
            SELECT COUNT(DISTINCT tecnico_id) FROM avisos a
            WHERE a.tecnico_id IS NOT NULL
            AND a.estado NOT IN ('COMPLETADO', 'CANCELADO')
            AND DATE(a.fecha_creacion) BETWEEN ? AND ?
            """;
        return jdbcTemplate.queryForObject(sql, Integer.class, 
            filters.dateFrom(), filters.dateTo());
    }

    private int countCreadosHoy(DashboardFilters filters) {
        LocalDate today = LocalDate.now();
        String sql = """
            SELECT COUNT(*) FROM avisos a
            WHERE DATE(a.fecha_creacion) = ?
            """;
        return jdbcTemplate.queryForObject(sql, Integer.class, today);
    }

    private int countCerradosHoy(DashboardFilters filters) {
        LocalDate today = LocalDate.now();
        String sql = """
            SELECT COUNT(*) FROM avisos a
            WHERE a.estado IN ('COMPLETADO', 'CANCELADO')
            AND DATE(a.fecha_fin) = ?
            """;
        return jdbcTemplate.queryForObject(sql, Integer.class, today);
    }

    private void applyDateRangeParams(StringBuilder sql, DashboardFilters filters) {
        sql.append(" AND DATE(a.fecha_creacion) BETWEEN ? AND ?");
    }

    private void applyDateRangeConditions(StringBuilder sql, DashboardFilters filters) {
        sql.append(" AND DATE(a.fecha_creacion) BETWEEN ? AND ?");
    }

private void applyTechnicianFilter(StringBuilder sql, DashboardFilters filters, List<Object> params) {
        if (filters.hasTechnicianFilter()) {
            sql.append(" AND a.tecnico_id = ?");
            params.add(filters.tecnicoId());
        }
    }
    
    private void applyPriorityFilter(StringBuilder sql, DashboardFilters filters, List<Object> params) {
        if (filters.hasPriorityFilter()) {
            sql.append(" AND a.prioridad = ?");
            params.add(filters.prioridad());
        }
    }

    // ==================== Row Mappers ====================

    private UrgentAvisoSummary mapToUrgentAvisoSummary(ResultSet rs, int rowNum) throws SQLException {
        var fechaProgramadaTs = rs.getTimestamp("fecha_programada");
        java.time.LocalDateTime fechaProgramada = fechaProgramadaTs != null 
            ? fechaProgramadaTs.toLocalDateTime() 
            : null;
        
        return new UrgentAvisoSummary(
            rs.getLong("id"),
            rs.getString("numero_correlativo"),
            rs.getString("cliente_nombre"),
            rs.getString("estado"),
            rs.getString("tecnico_nombre"),
            fechaProgramada,
            rs.getString("prioridad")
        );
    }

    private TodayJobSummary mapToTodayJobSummary(ResultSet rs, int rowNum) throws SQLException {
        var horaInicioTs = rs.getTimestamp("hora_inicio");
        java.time.LocalDateTime horaInicio = horaInicioTs != null 
            ? horaInicioTs.toLocalDateTime() 
            : null;
        
        return new TodayJobSummary(
            rs.getLong("id"),
            rs.getString("numero_correlativo"),
            rs.getString("cliente_nombre"),
            rs.getString("cliente_direccion"),
            rs.getString("tecnico_nombre"),
            rs.getString("estado"),
            horaInicio,
            rs.getString("prioridad")
        );
    }

    private TechnicianWorkload mapToTechnicianWorkload(ResultSet rs, int rowNum) throws SQLException {
        return new TechnicianWorkload(
            rs.getLong("tecnico_id"),
            rs.getString("nombre"),
            rs.getInt("active_jobs_count")
        );
    }

    private ScheduledJob mapToScheduledJob(ResultSet rs, int rowNum) throws SQLException {
        var fechaProgramadaTs = rs.getTimestamp("fecha_programada");
        java.time.LocalDateTime fechaProgramada = fechaProgramadaTs != null 
            ? fechaProgramadaTs.toLocalDateTime() 
            : null;
        
        Long tecnicoId = rs.getLong("tecnico_id");
        tecnicoId = rs.wasNull() ? null : tecnicoId;
        
        return new ScheduledJob(
            rs.getLong("id"),
            rs.getString("numero_correlativo"),
            rs.getString("cliente_nombre"),
            rs.getString("direccion"),
            fechaProgramada,
            tecnicoId,
            rs.getString("tecnico_nombre")
        );
    }
}