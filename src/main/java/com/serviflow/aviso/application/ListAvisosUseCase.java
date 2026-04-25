package com.serviflow.aviso.application;

import com.serviflow.aviso.application.input.ListAvisosInput;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.application.output.PaginatedResponse;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.port.AvisoSearchCriteria;
import com.serviflow.aviso.domain.valueobject.EstadoAviso;
import com.serviflow.aviso.domain.valueobject.Prioridad;

import java.util.List;

/**
 * Use case for listing avisos with filtering and pagination.
 * Orchestrates domain logic but does not implement business rules.
 */
public class ListAvisosUseCase {

    private final AvisoRepository avisoRepository;

    public ListAvisosUseCase(AvisoRepository avisoRepository) {
        this.avisoRepository = avisoRepository;
    }

    /**
     * Executes the use case to list avisos with optional filtering.
     *
     * @param input the input data for filtering and pagination
     * @return paginated list of avisos
     */
    public PaginatedResponse<AvisoOutput> execute(ListAvisosInput input) {
        // Build search criteria from input
        EstadoAviso estado = input.estado() != null ? EstadoAviso.valueOf(input.estado()) : null;
        Prioridad prioridad = input.prioridad() != null ? Prioridad.valueOf(input.prioridad()) : null;

        AvisoSearchCriteria criteria = new AvisoSearchCriteria(
            estado,
            prioridad,
            input.tecnicoId(),
            input.clienteId(),
            input.search(),
            input.page(),
            input.size()
        );

        // Execute query
        List<Aviso> avisos = avisoRepository.findAll(criteria);
        long total = avisoRepository.count(criteria);

        // Convert to output and build response
        List<AvisoOutput> content = avisos.stream()
            .map(AvisoOutput::fromDomain)
            .toList();

        int totalPages = (int) Math.ceil((double) total / input.size());

        return new PaginatedResponse<>(
            content,
            total,
            totalPages,
            input.page(),
            input.size()
        );
    }
}