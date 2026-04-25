package com.serviflow.aviso.application.output;

import com.serviflow.aviso.domain.entity.Aviso;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Output record for Aviso data, ready for presentation.
 */
public record AvisoOutput(
    Long id,
    String numeroCorrelativo,
    Long clienteId,
    String clienteNombre,
    String clienteTelefono,
    String direccionCompleta,
    String descripcion,
    String prioridad,
    String estado,
    DireccionOutput direccion,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaProgramada,
    Long tecnicoId,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFin,
    List<ObservacionOutput> observaciones
) {
    /**
     * Converts from domain Aviso entity.
     */
    public static AvisoOutput fromDomain(Aviso aviso) {
        return fromDomain(aviso, null, null);
    }

    /**
     * Converts from domain Aviso entity with optional client enrichment.
     *
     * @param aviso the domain aviso
     * @param clienteNombre client name (nullable)
     * @param clienteTelefono client phone (nullable)
     * @return enriched AvisoOutput
     */
    public static AvisoOutput fromDomain(Aviso aviso, String clienteNombre, String clienteTelefono) {
        DireccionOutput dir = DireccionOutput.from(aviso.direccionServicio());
        String direccionCompleta = formatDireccion(aviso.direccionServicio());

        return new AvisoOutput(
            aviso.id() != null ? aviso.id().value() : null,
            aviso.numeroCorrelativo().value(),
            aviso.clienteId(),
            clienteNombre,
            clienteTelefono,
            direccionCompleta,
            aviso.descripcion(),
            aviso.prioridad().name(),
            aviso.estado().name(),
            dir,
            aviso.fechaCreacion(),
            aviso.fechaProgramada(),
            aviso.tecnicoId(),
            aviso.fechaInicio(),
            aviso.fechaFin(),
            aviso.observaciones().stream()
                .map(ObservacionOutput::from)
                .toList()
        );
    }

    private static String formatDireccion(com.serviflow.aviso.domain.valueobject.DireccionServicio d) {
        if (d == null) return null;
        StringBuilder sb = new StringBuilder();
        if (d.calle() != null) sb.append(d.calle());
        if (d.numero() != null) sb.append(" ").append(d.numero());
        if (d.localidad() != null) {
            if (!sb.isEmpty()) sb.append(", ");
            sb.append(d.localidad());
        }
        if (d.provincia() != null) {
            if (!sb.isEmpty() && d.localidad() == null) sb.append(", ");
            else if (!sb.isEmpty()) sb.append(", ");
            sb.append(d.provincia());
        }
        return sb.toString().trim();
    }
}