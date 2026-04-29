package com.serviflow.aviso.infrastructure.persistence;

import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.entity.Observacion;
import com.serviflow.aviso.domain.valueobject.DireccionServicio;
import com.serviflow.aviso.domain.valueobject.NumeroCorrelativo;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between domain Aviso and JPA JpaAvisoEntity.
 * This is a thin translation layer - no business logic.
 */
@Component
public class AvisoMapper {

    private final ObservacionMapper observacionMapper = new ObservacionMapper();

    /**
     * Converts a JPA entity to a domain entity.
     */
    public Aviso toDomain(JpaAvisoEntity entity) {
        DireccionServicio direccion = new DireccionServicio(
            entity.getCalle(),
            entity.getNumero(),
            entity.getLocalidad(),
            entity.getProvincia(),
            entity.getCodigoPostal()
        );

        List<Observacion> observaciones = entity.getObservaciones() != null
            ? entity.getObservaciones().stream()
                .map(observacionMapper::toDomain)
                .collect(Collectors.toList())
            : List.of();

        return Aviso.reconstitute(
            entity.getId() != null ? new com.serviflow.aviso.domain.valueobject.AvisoId(entity.getId()) : null,
            entity.getClienteId(),
            new NumeroCorrelativo(entity.getNumeroCorrelativo()),
            entity.getDescripcion(),
            com.serviflow.aviso.domain.valueobject.Prioridad.valueOf(entity.getPrioridad()),
            com.serviflow.aviso.domain.valueobject.EstadoAviso.valueOf(entity.getEstado()),
            direccion,
            entity.getFechaCreacion(),
            entity.getFechaProgramada(),
            entity.getTecnicoId(),
            entity.getFechaInicio(),
            entity.getFechaFin(),
            observaciones,
            entity.getMaterialesUsados()
        );
    }

    /**
     * Converts a domain entity to a JPA entity.
     */
    public JpaAvisoEntity toJpa(Aviso aviso) {
        JpaAvisoEntity entity = new JpaAvisoEntity();
        if (aviso.id() != null) {
            entity.setId(aviso.id().value());
        }
        entity.setClienteId(aviso.clienteId());
        entity.setNumeroCorrelativo(aviso.numeroCorrelativo().value());
        entity.setDescripcion(aviso.descripcion());
        entity.setPrioridad(aviso.prioridad().name());
        entity.setEstado(aviso.estado().name());
        entity.setCalle(aviso.direccionServicio().calle());
        entity.setNumero(aviso.direccionServicio().numero());
        entity.setLocalidad(aviso.direccionServicio().localidad());
        entity.setProvincia(aviso.direccionServicio().provincia());
        entity.setCodigoPostal(aviso.direccionServicio().codigoPostal());
        entity.setFechaCreacion(aviso.fechaCreacion());
        entity.setFechaProgramada(aviso.fechaProgramada());
        entity.setTecnicoId(aviso.tecnicoId());
        entity.setFechaInicio(aviso.fechaInicio());
        entity.setFechaFin(aviso.fechaFin());
        entity.setMaterialesUsados(aviso.materialesUsados());

        // Map observaciones
        if (!aviso.observaciones().isEmpty()) {
            List<JpaObservacionEntity> obsEntities = aviso.observaciones().stream()
                .map(o -> observacionMapper.toJpa(o, entity))
                .collect(Collectors.toList());
            entity.setObservaciones(obsEntities);
        }

        return entity;
    }
}
