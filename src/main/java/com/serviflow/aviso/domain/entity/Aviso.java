package com.serviflow.aviso.domain.entity;

import com.serviflow.aviso.domain.valueobject.AvisoId;
import com.serviflow.aviso.domain.valueobject.DireccionServicio;
import com.serviflow.aviso.domain.valueobject.EstadoAviso;
import com.serviflow.aviso.domain.valueobject.NumeroCorrelativo;
import com.serviflow.aviso.domain.valueobject.Prioridad;
import com.serviflow.shared.domain.exception.DomainException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Rich domain model for Aviso (service request/work order).
 * Contains all business rules, validation logic, and state machine transitions.
 * This entity is IMMUTABLE - all modifications return new instances.
 */
public final class Aviso {

    private final AvisoId id;
    private final Long clienteId;
    private final NumeroCorrelativo numeroCorrelativo;
    private final String descripcion;
    private final Prioridad prioridad;
    private EstadoAviso estado;
    private final DireccionServicio direccionServicio;
    private final LocalDateTime fechaCreacion;
    private LocalDateTime fechaProgramada;
    private Long tecnicoId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private final List<Observacion> observaciones;

    private Aviso(AvisoId id, Long clienteId, NumeroCorrelativo numeroCorrelativo,
                  String descripcion, Prioridad prioridad, EstadoAviso estado,
                  DireccionServicio direccionServicio, LocalDateTime fechaCreacion,
                  LocalDateTime fechaProgramada, Long tecnicoId,
                  LocalDateTime fechaInicio, LocalDateTime fechaFin,
                  List<Observacion> observaciones) {
        this.id = id;
        this.clienteId = Objects.requireNonNull(clienteId, "ClienteId cannot be null");
        this.numeroCorrelativo = Objects.requireNonNull(numeroCorrelativo, "NumeroCorrelativo cannot be null");
        this.descripcion = validateDescripcion(descripcion);
        this.prioridad = Objects.requireNonNull(prioridad, "Prioridad cannot be null");
        this.estado = Objects.requireNonNull(estado, "Estado cannot be null");
        this.direccionServicio = Objects.requireNonNull(direccionServicio, "DireccionServicio cannot be null");
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion, "FechaCreacion cannot be null");
        this.fechaProgramada = fechaProgramada;
        this.tecnicoId = tecnicoId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.observaciones = observaciones != null ? new ArrayList<>(observaciones) : new ArrayList<>();
    }

    // ==================== Factory Methods ====================

    /**
     * Factory method for creating a new Aviso.
     */
    public static Aviso create(Long clienteId, NumeroCorrelativo numeroCorrelativo,
                                String descripcion, Prioridad prioridad,
                                DireccionServicio direccionServicio, LocalDateTime fechaProgramada) {
        return new Aviso(null, clienteId, numeroCorrelativo, descripcion, prioridad,
                        EstadoAviso.NUEVO, direccionServicio, LocalDateTime.now(),
                        fechaProgramada, null, null, null, new ArrayList<>());
    }

    /**
     * Factory method for reconstituting from persistence.
     */
    public static Aviso reconstitute(AvisoId id, Long clienteId, NumeroCorrelativo numeroCorrelativo,
                                      String descripcion, Prioridad prioridad, EstadoAviso estado,
                                      DireccionServicio direccionServicio, LocalDateTime fechaCreacion,
                                      LocalDateTime fechaProgramada, Long tecnicoId,
                                      LocalDateTime fechaInicio, LocalDateTime fechaFin,
                                      List<Observacion> observaciones) {
        return new Aviso(id, clienteId, numeroCorrelativo, descripcion, prioridad, estado,
                        direccionServicio, fechaCreacion, fechaProgramada, tecnicoId,
                        fechaInicio, fechaFin, observaciones);
    }

    // ==================== Validation ====================

    private static String validateDescripcion(String desc) {
        Objects.requireNonNull(desc, "Descripcion cannot be null");
        if (desc.isBlank()) {
            throw new DomainException("Descripcion cannot be blank");
        }
        if (desc.length() > 2000) {
            throw new DomainException("Descripcion cannot exceed 2000 characters");
        }
        return desc.trim();
    }

    // ==================== State Machine - Business Methods ====================

    /**
     * Assigns a technician to the aviso.
     * Transition: NUEVO -> ASIGNADO
     */
    public void assignTecnico(Long tecnicoId, String usuario) {
        if (!estado.canTransitionTo(EstadoAviso.ASIGNADO)) {
            throw new DomainException("Cannot assign tecnico from state: " + estado);
        }
        this.tecnicoId = Objects.requireNonNull(tecnicoId, "TecnicoId cannot be null");
        this.estado = EstadoAviso.ASIGNADO;
        addObservacion("Técnico asignado. Estado: NUEVO → ASIGNADO", "ESTADO_CHANGE", usuario);
    }

    /**
     * Starts the work on the aviso.
     * Transition: ASIGNADO -> EN_CURSO
     */
    public void startWork(String usuario) {
        if (!estado.canTransitionTo(EstadoAviso.EN_CURSO)) {
            throw new DomainException("Cannot start work from state: " + estado);
        }
        this.estado = EstadoAviso.EN_CURSO;
        this.fechaInicio = LocalDateTime.now();
        addObservacion("Trabajo iniciado. Estado: ASIGNADO → EN_CURSO", "ESTADO_CHANGE", usuario);
    }

    /**
     * Completes the work on the aviso.
     * Transition: EN_CURSO -> COMPLETADO
     */
    public void completeWork(String usuario) {
        if (!estado.canTransitionTo(EstadoAviso.COMPLETADO)) {
            throw new DomainException("Cannot complete from state: " + estado);
        }
        this.estado = EstadoAviso.COMPLETADO;
        this.fechaFin = LocalDateTime.now();
        addObservacion("Trabajo completado. Estado: EN_CURSO → COMPLETADO", "ESTADO_CHANGE", usuario);
    }

    /**
     * Sets the aviso as pending follow-up.
     * Transition: EN_CURSO -> PENDIENTE_SEGUIMIENTO
     */
    public void pendingFollowUp(String usuario) {
        if (!estado.canTransitionTo(EstadoAviso.PENDIENTE_SEGUIMIENTO)) {
            throw new DomainException("Cannot set pending follow-up from state: " + estado);
        }
        this.estado = EstadoAviso.PENDIENTE_SEGUIMIENTO;
        addObservacion("Pendiente de seguimiento. Estado: EN_CURSO → PENDIENTE_SEGUIMIENTO", "ESTADO_CHANGE", usuario);
    }

    /**
     * Cancels the aviso.
     * Can transition from any non-terminal state to CANCELADO.
     */
    public void cancel(String usuario) {
        if (estado == EstadoAviso.COMPLETADO) {
            throw new DomainException("Cannot cancel a completed aviso");
        }
        if (estado == EstadoAviso.CANCELADO) {
            throw new DomainException("Aviso is already cancelled");
        }
        EstadoAviso previous = this.estado;
        this.estado = EstadoAviso.CANCELADO;
        addObservacion("Aviso cancelado. Estado: " + previous + " → CANCELADO", "CANCELACION", usuario);
    }

    /**
     * Reassigns the aviso to a different date and/or technician.
     */
    public void reprogramar(LocalDateTime nuevaFecha, Long nuevoTecnicoId, String usuario) {
        if (estado.isTerminal()) {
            throw new DomainException("Cannot reprogram a terminal state: " + estado);
        }
        this.fechaProgramada = nuevaFecha;
        if (nuevoTecnicoId != null && !nuevoTecnicoId.equals(this.tecnicoId)) {
            Long oldTecnico = this.tecnicoId;
            this.tecnicoId = nuevoTecnicoId;
            addObservacion("Reprogramado: fecha=" + nuevaFecha + ", técnico=" + oldTecnico + " → " + nuevoTecnicoId, "REPROGRAMACION", usuario);
        } else {
            addObservacion("Reprogramado: fecha=" + nuevaFecha, "REPROGRAMACION", usuario);
        }
    }

    /**
     * Adds an observation to the aviso.
     * Only adds observation if the aviso has an ID (i.e., it's persisted).
     */
    public void addObservacion(String contenido, String tipo, String usuario) {
        Objects.requireNonNull(contenido, "Contenido cannot be null");
        Objects.requireNonNull(tipo, "Tipo cannot be null");
        Objects.requireNonNull(usuario, "Usuario cannot be null");
        // Only add observation if the aviso has an ID (i.e., it's persisted)
        if (this.id != null) {
            this.observaciones.add(Observacion.create(this.id, contenido, tipo, usuario));
        }
    }

    /**
     * Updates info fields (descripcion, prioridad, direccion, fechaProgramada).
     * Only allowed when aviso is in NUEVO or ASIGNADO state.
     * Returns a new immutable Aviso instance.
     */
    public Aviso updateInfo(String descripcion, Prioridad prioridad, DireccionServicio direccion, LocalDateTime fechaProgramada) {
        if (this.estado == EstadoAviso.EN_CURSO || this.estado == EstadoAviso.COMPLETADO || this.estado == EstadoAviso.CANCELADO) {
            throw new DomainException("Cannot update aviso in state: " + estado);
        }
        return new Aviso(this.id, this.clienteId, this.numeroCorrelativo,
            validateDescripcion(descripcion), prioridad, this.estado, direccion,
            this.fechaCreacion, fechaProgramada, this.tecnicoId, this.fechaInicio, this.fechaFin,
            new ArrayList<>(this.observaciones));
    }

    // ==================== Getters ====================

    public AvisoId id() {
        return id;
    }

    public Long clienteId() {
        return clienteId;
    }

    public NumeroCorrelativo numeroCorrelativo() {
        return numeroCorrelativo;
    }

    public String descripcion() {
        return descripcion;
    }

    public Prioridad prioridad() {
        return prioridad;
    }

    public EstadoAviso estado() {
        return estado;
    }

    public DireccionServicio direccionServicio() {
        return direccionServicio;
    }

    public LocalDateTime fechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime fechaProgramada() {
        return fechaProgramada;
    }

    public Long tecnicoId() {
        return tecnicoId;
    }

    public LocalDateTime fechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime fechaFin() {
        return fechaFin;
    }

    public List<Observacion> observaciones() {
        return Collections.unmodifiableList(observaciones);
    }

    // ==================== Query Methods ====================

    /**
     * Checks if the aviso has a technician assigned.
     */
    public boolean isAssigned() {
        return tecnicoId != null;
    }

    /**
     * Checks if the aviso is in a terminal state.
     */
    public boolean isTerminal() {
        return estado.isTerminal();
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Aviso aviso)) return false;
        return Objects.equals(id, aviso.id) && Objects.equals(numeroCorrelativo, aviso.numeroCorrelativo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numeroCorrelativo);
    }

    @Override
    public String toString() {
        return "Aviso{" +
                "id=" + id +
                ", numeroCorrelativo=" + numeroCorrelativo +
                ", estado=" + estado +
                ", prioridad=" + prioridad +
                ", clienteId=" + clienteId +
                '}';
    }
}
