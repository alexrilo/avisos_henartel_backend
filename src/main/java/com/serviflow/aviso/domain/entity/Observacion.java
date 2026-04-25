package com.serviflow.aviso.domain.entity;

import com.serviflow.aviso.domain.valueobject.AvisoId;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Immutable entity representing an observation/audit log entry for an Aviso.
 * Records all state changes and important events.
 */
public final class Observacion {

    private final Long id;
    private final AvisoId avisoId;
    private final String contenido;
    private final String tipo; // OBSERVACION, ESTADO_CHANGE, REPROGRAMACION, CANCELACION
    private final String usuario;
    private final LocalDateTime fechaCreacion;

    private Observacion(Long id, AvisoId avisoId, String contenido, String tipo, 
                        String usuario, LocalDateTime fechaCreacion) {
        this.id = id;
        this.avisoId = Objects.requireNonNull(avisoId, "AvisoId cannot be null");
        this.contenido = Objects.requireNonNull(contenido, "Contenido cannot be null");
        this.tipo = Objects.requireNonNull(tipo, "Tipo cannot be null");
        this.usuario = Objects.requireNonNull(usuario, "Usuario cannot be null");
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion, "FechaCreacion cannot be null");
    }

    /**
     * Factory method for creating a new observation.
     */
    public static Observacion create(AvisoId avisoId, String contenido, String tipo, String usuario) {
        return new Observacion(null, avisoId, contenido, tipo, usuario, LocalDateTime.now());
    }

    /**
     * Factory method for reconstituting from persistence.
     */
    public static Observacion reconstitute(Long id, AvisoId avisoId, String contenido, 
                                           String tipo, String usuario, LocalDateTime fechaCreacion) {
        return new Observacion(id, avisoId, contenido, tipo, usuario, fechaCreacion);
    }

    // Getters
    public Long id() {
        return id;
    }

    public AvisoId avisoId() {
        return avisoId;
    }

    public String contenido() {
        return contenido;
    }

    public String tipo() {
        return tipo;
    }

    public String usuario() {
        return usuario;
    }

    public LocalDateTime fechaCreacion() {
        return fechaCreacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Observacion that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(avisoId, that.avisoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, avisoId);
    }

    @Override
    public String toString() {
        return "Observacion{" +
                "id=" + id +
                ", tipo='" + tipo + '\'' +
                ", usuario='" + usuario + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                '}';
    }
}
