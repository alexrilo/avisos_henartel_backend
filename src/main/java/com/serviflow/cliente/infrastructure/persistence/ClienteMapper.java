package com.serviflow.cliente.infrastructure.persistence;

import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.valueobject.ClienteId;
import com.serviflow.cliente.domain.valueobject.ClienteStatus;
import com.serviflow.cliente.domain.valueobject.TipoCliente;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Mapper for converting between JPA entity and domain entity.
 * Handles all translation between Infrastructure and Domain layers.
 */
@Component
public class ClienteMapper {

    /**
     * Converts a JPA entity to a domain Cliente.
     */
    public Cliente toDomain(JpaClienteEntity entity) {
        if (entity == null) {
            return null;
        }

        Optional<ClienteId> idOpt = entity.getId() != null
            ? Optional.of(new ClienteId(entity.getId()))
            : Optional.<ClienteId>empty();

        return Cliente.reconstitute(
            idOpt.orElse(null),
            TipoCliente.valueOf(entity.getTipo()),
            entity.getNombreOrazonSocial(),
            entity.getTelefono(),
            entity.getPersonaContacto(),
            entity.getObservaciones(),
            ClienteStatus.valueOf(entity.getEstado()),
            entity.getFechaCreacion(),
            entity.getFechaModificacion()
        );
    }

    /**
     * Converts a domain Cliente to a JPA entity.
     */
    public JpaClienteEntity toJpa(Cliente cliente) {
        if (cliente == null) {
            return null;
        }

        JpaClienteEntity entity = new JpaClienteEntity();

        if (cliente.id() != null) {
            entity.setId(cliente.id().value());
        }

        entity.setTipo(cliente.tipo().name());
        entity.setNombreOrazonSocial(cliente.nombreOrazonSocial());
        entity.setTelefono(cliente.telefono());
        entity.setPersonaContacto(cliente.personaContacto());
        entity.setObservaciones(cliente.observaciones());
        entity.setEstado(cliente.estado().name());
        entity.setFechaCreacion(cliente.fechaCreacion());
        entity.setFechaModificacion(cliente.fechaModificacion());

        return entity;
    }
}
