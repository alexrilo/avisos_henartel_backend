package com.serviflow.aviso.domain.port;

import com.serviflow.aviso.domain.valueobject.EstadoAviso;
import com.serviflow.aviso.domain.valueobject.Prioridad;

import java.util.List;
import java.util.Optional;

/**
 * Search criteria for filtering avisos.
 */
public record AvisoSearchCriteria(
    EstadoAviso estado,
    Prioridad prioridad,
    Long tecnicoId,
    Long clienteId,
    String keyword,
    int page,
    int size
) {
    public AvisoSearchCriteria {
        if (page < 0) page = 0;
        if (size < 1) size = 20;
        if (size > 100) size = 100;
    }

    public static AvisoSearchCriteria empty() {
        return new AvisoSearchCriteria(null, null, null, null, null, 0, 20);
    }

    public static AvisoSearchCriteria byTecnico(Long tecnicoId) {
        return new AvisoSearchCriteria(null, null, tecnicoId, null, null, 0, 100);
    }

    public static AvisoSearchCriteria byEstado(EstadoAviso estado) {
        return new AvisoSearchCriteria(estado, null, null, null, null, 0, 100);
    }

    public boolean hasFilters() {
        return estado != null || prioridad != null || tecnicoId != null || 
               clienteId != null || (keyword != null && !keyword.isBlank());
    }

    public int offset() {
        return page * size;
    }

    public List<String> toKeywords() {
        if (keyword == null || keyword.isBlank()) {
            return List.of();
        }
        return List.of(keyword.trim().split("\\s+"));
    }
}
