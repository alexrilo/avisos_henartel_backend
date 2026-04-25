package com.serviflow.cliente.application.output;

import com.serviflow.cliente.domain.entity.Cliente;

import java.time.LocalDateTime;

/**
 * Output record for Cliente data.
 * 
 * @param id the cliente ID
 * @param tipo the client type
 * @param nombreOrazonSocial the client name or business name
 * @param telefono the phone number
 * @param personaContacto the contact person
 * @param observaciones observations/notes
 * @param estado the client status
 * @param fechaCreacion creation timestamp
 * @param fechaModificacion last modification timestamp
 */
public record ClienteOutput(
    Long id,
    String tipo,
    String nombreOrazonSocial,
    String telefono,
    String personaContacto,
    String observaciones,
    String estado,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaModificacion
) {
    /**
     * Creates a ClienteOutput from a domain Cliente entity.
     * 
     * @param cliente the domain entity
     * @return the output record
     */
    public static ClienteOutput fromDomain(Cliente cliente) {
        return new ClienteOutput(
            cliente.id() != null ? cliente.id().value() : null,
            cliente.tipo().name(),
            cliente.nombreOrazonSocial(),
            cliente.telefono(),
            cliente.personaContacto(),
            cliente.observaciones(),
            cliente.estado().name(),
            cliente.fechaCreacion(),
            cliente.fechaModificacion()
        );
    }
}
