package com.serviflow.aviso.infrastructure.web;

import com.serviflow.auth.infrastructure.security.UserPrincipal;
import com.serviflow.aviso.application.*;
import com.serviflow.aviso.application.input.*;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.application.output.PaginatedResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller for Aviso operations.
 * This is a thin web layer that delegates to use cases.
 */
@RestController
@RequestMapping("/api/avisos")
public class AvisoController {

    private final CreateAvisoUseCase createAvisoUseCase;
    private final UpdateAvisoUseCase updateAvisoUseCase;
    private final GetAvisoUseCase getAvisoUseCase;
    private final ListAvisosUseCase listAvisosUseCase;
    private final AssignTecnicoUseCase assignTecnicoUseCase;
    private final ChangeEstadoUseCase changeEstadoUseCase;
    private final ReprogramarAvisoUseCase reprogramarAvisoUseCase;
    private final CancelarAvisoUseCase cancelarAvisoUseCase;
    private final GetMisTrabajosUseCase getMisTrabajosUseCase;

    public AvisoController(
            CreateAvisoUseCase createAvisoUseCase,
            UpdateAvisoUseCase updateAvisoUseCase,
            GetAvisoUseCase getAvisoUseCase,
            ListAvisosUseCase listAvisosUseCase,
            AssignTecnicoUseCase assignTecnicoUseCase,
            ChangeEstadoUseCase changeEstadoUseCase,
            ReprogramarAvisoUseCase reprogramarAvisoUseCase,
            CancelarAvisoUseCase cancelarAvisoUseCase,
            GetMisTrabajosUseCase getMisTrabajosUseCase) {
        this.createAvisoUseCase = createAvisoUseCase;
        this.updateAvisoUseCase = updateAvisoUseCase;
        this.getAvisoUseCase = getAvisoUseCase;
        this.listAvisosUseCase = listAvisosUseCase;
        this.assignTecnicoUseCase = assignTecnicoUseCase;
        this.changeEstadoUseCase = changeEstadoUseCase;
        this.reprogramarAvisoUseCase = reprogramarAvisoUseCase;
        this.cancelarAvisoUseCase = cancelarAvisoUseCase;
        this.getMisTrabajosUseCase = getMisTrabajosUseCase;
    }

    /**
     * Extracts the username from the authentication context.
     */
    private String getUsername(Authentication auth) {
        return auth != null ? auth.getName() : "system";
    }

    /**
     * Extracts the current user ID from the authentication principal.
     * For TECNICO role, this equals the tecnicoId.
     */
    private Long getCurrentUserId(Authentication auth) {
        if (auth == null) {
            return null;
        }
        if (auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getUserId();
        }
        return null;
    }

    /**
     * Creates a new Aviso.
     * POST /api/avisos
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<AvisoOutput> createAviso(
            @Valid @RequestBody CreateAvisoRequest request,
            Authentication auth) {
        CreateAvisoInput input = new CreateAvisoInput(
            request.clienteId(),
            request.descripcion(),
            request.prioridad(),
            request.calle(),
            request.numero(),
            request.localidad(),
            request.provincia(),
            request.codigoPostal(),
            request.fechaProgramada(),
            getUsername(auth)
        );
        AvisoOutput output = createAvisoUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    /**
     * Lists avisos with optional filtering and pagination.
     * GET /api/avisos
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<PaginatedResponse<AvisoOutput>> listAvisos(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String prioridad,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "fechaCreacion") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        ListAvisosInput input = new ListAvisosInput(
            clienteId, null, estado, prioridad, search, page, size, sortBy, sortDir
        );
        PaginatedResponse<AvisoOutput> response = listAvisosUseCase.execute(input);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a specific aviso by ID.
     * GET /api/avisos/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR', 'TECNICO')")
    public ResponseEntity<AvisoOutput> getAviso(
            @PathVariable Long id,
            @RequestParam(required = false) Long tecnicoId,
            Authentication auth) {
        // Primary: extract from JWT principal. Fallback: query param (deprecated, for Angular compat).
        Long effectiveTecnicoId = getCurrentUserId(auth);
        if (effectiveTecnicoId == null && tecnicoId != null) {
            effectiveTecnicoId = tecnicoId;
        }
        AvisoOutput output = getAvisoUseCase.execute(id, effectiveTecnicoId);
        return ResponseEntity.ok(output);
    }

    /**
     * Updates an existing aviso.
     * PUT /api/avisos/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<AvisoOutput> updateAviso(
            @PathVariable Long id,
            @Valid @RequestBody UpdateAvisoRequest request,
            Authentication auth) {
        UpdateAvisoInput input = new UpdateAvisoInput(
            id,
            request.descripcion(),
            request.prioridad(),
            request.calle(),
            request.numero(),
            request.localidad(),
            request.provincia(),
            request.codigoPostal(),
            request.fechaProgramada(),
            getUsername(auth)
        );
        AvisoOutput output = updateAvisoUseCase.execute(input);
        return ResponseEntity.ok(output);
    }

    /**
     * Assigns a technician to an aviso.
     * POST /api/avisos/{id}/asignar
     */
    @PostMapping("/{id}/asignar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<AvisoOutput> asignarTecnico(
            @PathVariable Long id,
            @RequestBody AssignTecnicoRequest request,
            Authentication auth) {
        AssignTecnicoInput input = new AssignTecnicoInput(
            id,
            request.tecnicoId(),
            getUsername(auth)
        );
        AvisoOutput output = assignTecnicoUseCase.execute(input);
        return ResponseEntity.ok(output);
    }

    /**
     * Changes the state of an aviso.
     * POST /api/avisos/{id}/cambiar-estado
     */
    @PostMapping("/{id}/cambiar-estado")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR', 'TECNICO')")
    public ResponseEntity<AvisoOutput> cambiarEstado(
            @PathVariable Long id,
            @RequestBody ChangeEstadoRequest request,
            Authentication auth) {
        // Primary: extract tecnicoId from JWT principal. Fallback: request body (deprecated, for Angular compat).
        Long effectiveTecnicoId = getCurrentUserId(auth);
        if (effectiveTecnicoId == null && request.tecnicoId() != null) {
            effectiveTecnicoId = request.tecnicoId();
        }
        ChangeEstadoInput input = new ChangeEstadoInput(
            id,
            request.estado(),
            effectiveTecnicoId,
            getUsername(auth),
            request.observacion()
        );
        AvisoOutput output = changeEstadoUseCase.execute(input);
        return ResponseEntity.ok(output);
    }

    /**
     * Reschedules an aviso.
     * POST /api/avisos/{id}/reprogramar
     */
    @PostMapping("/{id}/reprogramar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<AvisoOutput> reprogramar(
            @PathVariable Long id,
            @RequestBody ReprogramarRequest request,
            Authentication auth) {
        ReprogramarInput input = new ReprogramarInput(
            id,
            request.nuevaFecha(),
            request.nuevoTecnicoId(),
            getUsername(auth)
        );
        AvisoOutput output = reprogramarAvisoUseCase.execute(input);
        return ResponseEntity.ok(output);
    }

    /**
     * Cancels an aviso.
     * POST /api/avisos/{id}/cancelar
     */
    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<AvisoOutput> cancelar(
            @PathVariable Long id,
            Authentication auth) {
        AvisoOutput output = cancelarAvisoUseCase.execute(id, getUsername(auth));
        return ResponseEntity.ok(output);
    }

    /**
     * Gets all avisos assigned to the current technician.
     * GET /api/avisos/mis-trabajos
     */
    @GetMapping("/mis-trabajos")
    @PreAuthorize("hasRole('TECNICO')")
    public ResponseEntity<List<AvisoOutput>> getMisTrabajos(
            @RequestParam(required = false) Long tecnicoId,
            Authentication auth) {
        // Primary: extract from JWT principal. Fallback: query param (deprecated, for Angular compat).
        Long effectiveTecnicoId = getCurrentUserId(auth);
        if (effectiveTecnicoId == null && tecnicoId != null) {
            effectiveTecnicoId = tecnicoId;
        }
        List<AvisoOutput> output = getMisTrabajosUseCase.execute(effectiveTecnicoId);
        return ResponseEntity.ok(output);
    }

    // ==================== Request DTOs ====================

    /**
     * Request body for creating an aviso.
     */
    public record CreateAvisoRequest(
        Long clienteId,
        String descripcion,
        String prioridad,
        String calle,
        String numero,
        String localidad,
        String provincia,
        String codigoPostal,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaProgramada
    ) {}

    /**
     * Request body for updating an aviso.
     */
    public record UpdateAvisoRequest(
        String descripcion,
        String prioridad,
        String calle,
        String numero,
        String localidad,
        String provincia,
        String codigoPostal,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaProgramada
    ) {}

    /**
     * Request body for assigning a technician.
     */
    public record AssignTecnicoRequest(Long tecnicoId) {}

    /**
     * Request body for changing estado.
     */
    public record ChangeEstadoRequest(String estado, Long tecnicoId, String observacion) {}

    /**
     * Request body for rescheduling.
     */
    public record ReprogramarRequest(
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime nuevaFecha,
        Long nuevoTecnicoId
    ) {}
}