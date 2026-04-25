package com.serviflow.aviso.domain.port;

import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.valueobject.AvisoId;
import com.serviflow.aviso.domain.valueobject.EstadoAviso;
import com.serviflow.aviso.domain.valueobject.Prioridad;

import java.util.List;
import java.util.Optional;

/**
 * Port interface for Aviso repository.
 * Pure Java interface with no framework dependencies.
 */
public interface AvisoRepository {

    /**
     * Saves an aviso (create or update).
     */
    Aviso save(Aviso aviso);

    /**
     * Finds an aviso by its ID.
     */
    Optional<Aviso> findById(AvisoId id);

    /**
     * Finds all avisos matching the search criteria.
     */
    List<Aviso> findAll(AvisoSearchCriteria criteria);

    /**
     * Counts avisos matching the search criteria.
     */
    long count(AvisoSearchCriteria criteria);

    /**
     * Finds an aviso by its correlative number.
     */
    Optional<Aviso> findByNumeroCorrelativo(String numeroCorrelativo);

    /**
     * Finds avisos assigned to a specific technician with optional estado filter.
     */
    List<Aviso> findByTecnicoId(Long tecnicoId, EstadoAviso estado);
}
