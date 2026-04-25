package com.serviflow.cliente.infrastructure.web;

import com.serviflow.cliente.application.*;
import com.serviflow.cliente.application.input.CreateClienteInput;
import com.serviflow.cliente.application.input.ListClientesInput;
import com.serviflow.cliente.application.input.UpdateClienteInput;
import com.serviflow.cliente.application.output.ClienteOutput;
import com.serviflow.cliente.application.output.PaginatedResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Cliente operations.
 * Thin controller - only handles HTTP concerns and delegates to use cases.
 */
@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final CreateClienteUseCase createClienteUseCase;
    private final UpdateClienteUseCase updateClienteUseCase;
    private final GetClienteUseCase getClienteUseCase;
    private final ListClientesUseCase listClientesUseCase;
    private final ToggleClienteStatusUseCase toggleClienteStatusUseCase;

    public ClienteController(CreateClienteUseCase createClienteUseCase,
                             UpdateClienteUseCase updateClienteUseCase,
                             GetClienteUseCase getClienteUseCase,
                             ListClientesUseCase listClientesUseCase,
                             ToggleClienteStatusUseCase toggleClienteStatusUseCase) {
        this.createClienteUseCase = createClienteUseCase;
        this.updateClienteUseCase = updateClienteUseCase;
        this.getClienteUseCase = getClienteUseCase;
        this.listClientesUseCase = listClientesUseCase;
        this.toggleClienteStatusUseCase = toggleClienteStatusUseCase;
    }

    /**
     * Creates a new cliente.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<ClienteOutput> createCliente(@Valid @RequestBody CreateClienteRequest request) {
        CreateClienteInput input = new CreateClienteInput(
            request.tipo(),
            request.nombreOrazonSocial(),
            request.telefono(),
            request.personaContacto(),
            request.observaciones()
        );
        ClienteOutput output = createClienteUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    /**
     * Lists clientes with filtering, pagination and sorting.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<PaginatedResponse<ClienteOutput>> listClientes(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String tipo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "nombreOrazonSocial") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {
        ListClientesInput input = new ListClientesInput(search, estado, tipo, page, size, sortBy, sortDir);
        PaginatedResponse<ClienteOutput> response = listClientesUseCase.execute(input);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets a cliente by ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<ClienteOutput> getCliente(@PathVariable Long id) {
        ClienteOutput output = getClienteUseCase.execute(id);
        return ResponseEntity.ok(output);
    }

    /**
     * Updates an existing cliente.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<ClienteOutput> updateCliente(@PathVariable Long id,
                                                        @Valid @RequestBody UpdateClienteRequest request) {
        UpdateClienteInput input = new UpdateClienteInput(
            id,
            request.nombreOrazonSocial(),
            request.telefono(),
            request.personaContacto(),
            request.observaciones()
        );
        ClienteOutput output = updateClienteUseCase.execute(input);
        return ResponseEntity.ok(output);
    }

    /**
     * Toggles a cliente's status between ACTIVO and INACTIVO.
     */
    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<ClienteOutput> toggleClienteStatus(@PathVariable Long id) {
        ClienteOutput output = toggleClienteStatusUseCase.execute(id);
        return ResponseEntity.ok(output);
    }

    // ==================== Request DTOs ====================

    /**
     * Request DTO for creating a cliente.
     */
    public record CreateClienteRequest(
        @NotBlank(message = "Tipo es requerido")
        @Size(max = 20, message = "Tipo no puede exceder 20 caracteres")
        String tipo,

        @NotBlank(message = "Nombre o razón social es requerido")
        @Size(max = 200, message = "Nombre no puede exceder 200 caracteres")
        String nombreOrazonSocial,

        @NotBlank(message = "Teléfono es requerido")
        @Size(max = 50, message = "Teléfono no puede exceder 50 caracteres")
        String telefono,

        @Size(max = 200, message = "Persona de contacto no puede exceder 200 caracteres")
        String personaContacto,

        @Size(max = 1000, message = "Observaciones no pueden exceder 1000 caracteres")
        String observaciones
    ) {}

    /**
     * Request DTO for updating a cliente.
     */
    public record UpdateClienteRequest(
        @NotBlank(message = "Nombre o razón social es requerido")
        @Size(max = 200, message = "Nombre no puede exceder 200 caracteres")
        String nombreOrazonSocial,

        @NotBlank(message = "Teléfono es requerido")
        @Size(max = 50, message = "Teléfono no puede exceder 50 caracteres")
        String telefono,

        @Size(max = 200, message = "Persona de contacto no puede exceder 200 caracteres")
        String personaContacto,

        @Size(max = 1000, message = "Observaciones no pueden exceder 1000 caracteres")
        String observaciones
    ) {}
}
