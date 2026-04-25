package com.serviflow.aviso.infrastructure.persistence;

import com.serviflow.aviso.domain.entity.Observacion;
import com.serviflow.aviso.domain.valueobject.AvisoId;

/**
 * Mapper for converting between domain Observacion and JPA JpaObservacionEntity.
 * This is a thin translation layer - no business logic.
 */
public class ObservacionMapper {

    /**
     * Converts a JPA entity to a domain entity.
     */
    public Observacion toDomain(JpaObservacionEntity entity) {
        return Observacion.reconstitute(
            entity.getId(),
            new AvisoId(entity.getAviso().getId()),
            entity.getContenido(),
            entity.getTipo(),
            entity.getUsuario(),
            entity.getFechaCreacion()
        );
    }

    /**
     * Converts a domain entity to a JPA entity.
     * Note: The avisoEntity parameter must be the parent entity for proper relationship mapping.
     */
    public JpaObservacionEntity toJpa(Observacion obs, JpaAvisoEntity avisoEntity) {
        JpaObservacionEntity entity = new JpaObservacionEntity();
        if (obs.id() != null) {
            entity.setId(obs.id());
        }
        entity.setAviso(avisoEntity);
        entity.setContenido(obs.contenido());
        entity.setTipo(obs.tipo());
        entity.setUsuario(obs.usuario());
        entity.setFechaCreacion(obs.fechaCreacion());
        return entity;
    }
}