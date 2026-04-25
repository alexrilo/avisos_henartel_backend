package com.serviflow.cliente.domain.entity;

import com.serviflow.cliente.domain.valueobject.ClienteId;
import com.serviflow.cliente.domain.valueobject.ClienteStatus;
import com.serviflow.cliente.domain.valueobject.TipoCliente;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rich domain model for Cliente.
 * Contains all business rules, validation logic, and state transitions.
 * This is an IMMUTABLE entity - all modifications return new instances.
 */
public final class Cliente {

    private final ClienteId id;
    private final TipoCliente tipo;
    private final String nombreOrazonSocial;
    private final String telefono;
    private final String personaContacto;
    private final String observaciones;
    private final ClienteStatus estado;
    private final LocalDateTime fechaCreacion;
    private final LocalDateTime fechaModificacion;

    /**
     * Private constructor - use factory methods to create instances.
     */
    private Cliente(ClienteId id, TipoCliente tipo, String nombreOrazonSocial, String telefono,
                    String personaContacto, String observaciones, ClienteStatus estado,
                    LocalDateTime fechaCreacion, LocalDateTime fechaModificacion) {
        this.id = id;
        this.tipo = Objects.requireNonNull(tipo, "Tipo cannot be null");
        this.nombreOrazonSocial = validateNombre(nombreOrazonSocial);
        this.telefono = validateTelefono(telefono);
        this.personaContacto = normalizeOptional(personaContacto);
        this.observaciones = normalizeOptional(observaciones);
        this.estado = Objects.requireNonNull(estado, "Estado cannot be null");
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion, "FechaCreacion cannot be null");
        this.fechaModificacion = fechaModificacion;
    }

    // ==================== Factory Methods ====================

    /**
     * Factory method for creating a new PARTICULAR client.
     * The ID is null as it will be assigned by persistence.
     */
    public static Cliente createParticular(String nombre, String telefono) {
        return createParticular(nombre, telefono, null, null);
    }

    /**
     * Factory method for creating a new PARTICULAR client with optional fields.
     */
    public static Cliente createParticular(String nombre, String telefono,
                                            String personaContacto, String observaciones) {
        return new Cliente(
            null,
            TipoCliente.PARTICULAR,
            nombre,
            telefono,
            personaContacto,
            observaciones,
            ClienteStatus.ACTIVO,
            LocalDateTime.now(),
            null
        );
    }

    /**
     * Factory method for creating a new EMPRESA client.
     * The ID is null as it will be assigned by persistence.
     */
    public static Cliente createEmpresa(String razonSocial, String telefono) {
        return createEmpresa(razonSocial, telefono, null, null);
    }

    /**
     * Factory method for creating a new EMPRESA client with optional fields.
     */
    public static Cliente createEmpresa(String razonSocial, String telefono,
                                         String personaContacto, String observaciones) {
        return new Cliente(
            null,
            TipoCliente.EMPRESA,
            razonSocial,
            telefono,
            personaContacto,
            observaciones,
            ClienteStatus.ACTIVO,
            LocalDateTime.now(),
            null
        );
    }

    /**
     * Generic factory method for creating a new Cliente.
     */
    public static Cliente create(TipoCliente tipo, String nombreOrazonSocial, String telefono) {
        return new Cliente(
            null,
            tipo,
            nombreOrazonSocial,
            telefono,
            null,
            null,
            ClienteStatus.ACTIVO,
            LocalDateTime.now(),
            null
        );
    }

    /**
     * Generic factory method for creating a new Cliente with all fields.
     */
    public static Cliente create(TipoCliente tipo, String nombreOrazonSocial, String telefono,
                                  String personaContacto, String observaciones) {
        return new Cliente(
            null,
            tipo,
            nombreOrazonSocial,
            telefono,
            personaContacto,
            observaciones,
            ClienteStatus.ACTIVO,
            LocalDateTime.now(),
            null
        );
    }

    /**
     * Factory method for reconstituting a Cliente from persistence.
     */
    public static Cliente reconstitute(ClienteId id, TipoCliente tipo, String nombreOrazonSocial,
                                       String telefono, String personaContacto, String observaciones,
                                       ClienteStatus estado, LocalDateTime fechaCreacion,
                                       LocalDateTime fechaModificacion) {
        return new Cliente(id, tipo, nombreOrazonSocial, telefono, personaContacto,
                          observaciones, estado, fechaCreacion, fechaModificacion);
    }

    // ==================== Validation ====================

    private static String validateNombre(String nombre) {
        Objects.requireNonNull(nombre, "Nombre or razón social cannot be null");
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre or razón social cannot be blank");
        }
        if (nombre.length() > 200) {
            throw new IllegalArgumentException("Nombre or razón social cannot exceed 200 characters");
        }
        return nombre.trim();
    }

    private static String validateTelefono(String telefono) {
        Objects.requireNonNull(telefono, "Teléfono cannot be null");
        if (telefono.isBlank()) {
            throw new IllegalArgumentException("Teléfono cannot be blank");
        }
        if (telefono.length() > 50) {
            throw new IllegalArgumentException("Teléfono cannot exceed 50 characters");
        }
        return telefono.trim();
    }

    private static String normalizeOptional(String value) {
        return value != null && !value.isBlank() ? value.trim() : null;
    }

    // ==================== Business Methods - State Transitions ====================

    /**
     * Deactivates the cliente if currently active.
     * Returns a new Cliente instance with the updated status (immutable).
     */
    public Cliente deactivate() {
        if (this.estado == ClienteStatus.INACTIVO) {
            return this;
        }
        return new Cliente(this.id, this.tipo, this.nombreOrazonSocial, this.telefono,
                          this.personaContacto, this.observaciones, ClienteStatus.INACTIVO,
                          this.fechaCreacion, LocalDateTime.now());
    }

    /**
     * Activates the cliente if currently inactive.
     * Returns a new Cliente instance with the updated status (immutable).
     */
    public Cliente activate() {
        if (this.estado == ClienteStatus.ACTIVO) {
            return this;
        }
        return new Cliente(this.id, this.tipo, this.nombreOrazonSocial, this.telefono,
                          this.personaContacto, this.observaciones, ClienteStatus.ACTIVO,
                          this.fechaCreacion, LocalDateTime.now());
    }

    /**
     * Toggles the status between ACTIVO and INACTIVO.
     * Returns a new Cliente instance with the toggled status.
     */
    public Cliente toggleStatus() {
        return this.estado == ClienteStatus.ACTIVO ? deactivate() : activate();
    }

    /**
     * Checks if the cliente can be deactivated.
     */
    public boolean canBeDeactivated() {
        return this.estado == ClienteStatus.ACTIVO;
    }

    /**
     * Checks if the cliente can be activated.
     */
    public boolean canBeActivated() {
        return this.estado == ClienteStatus.INACTIVO;
    }

    /**
     * Checks if the cliente is currently active.
     */
    public boolean isActive() {
        return this.estado == ClienteStatus.ACTIVO;
    }

    // ==================== Update Methods ====================

    /**
     * Updates cliente information.
     * Returns a new Cliente instance (immutable).
     */
    public Cliente actualizarDatos(String nombreOrazonSocial, String telefono,
                                    String personaContacto, String observaciones) {
        return new Cliente(this.id, this.tipo, nombreOrazonSocial, telefono,
                          personaContacto, observaciones, this.estado,
                          this.fechaCreacion, LocalDateTime.now());
    }

    /**
     * Updates cliente information.
     * Returns a new Cliente instance (immutable).
     */
    public Cliente updateInfo(String nombreOrazonSocial, String telefono,
                               String personaContacto, String observaciones) {
        return new Cliente(this.id, this.tipo, nombreOrazonSocial, telefono,
                          personaContacto, observaciones, this.estado,
                          this.fechaCreacion, LocalDateTime.now());
    }

    /**
     * Changes the cliente type.
     * Returns a new Cliente instance (immutable).
     */
    public Cliente changeTipo(TipoCliente newTipo) {
        return new Cliente(this.id, newTipo, this.nombreOrazonSocial, this.telefono,
                          this.personaContacto, this.observaciones, this.estado,
                          this.fechaCreacion, LocalDateTime.now());
    }

    // ==================== Getters ====================

    public ClienteId id() {
        return id;
    }

    public TipoCliente tipo() {
        return tipo;
    }

    public String nombreOrazonSocial() {
        return nombreOrazonSocial;
    }

    public String telefono() {
        return telefono;
    }

    public String personaContacto() {
        return personaContacto;
    }

    public String observaciones() {
        return observaciones;
    }

    public ClienteStatus estado() {
        return estado;
    }

    public LocalDateTime fechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime fechaModificacion() {
        return fechaModificacion;
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente cliente)) return false;
        return Objects.equals(id, cliente.id) && Objects.equals(telefono, cliente.telefono);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, telefono);
    }

    @Override
    public String toString() {
        return "Cliente{" +
               "id=" + id +
               ", tipo=" + tipo +
               ", nombreOrazonSocial='" + nombreOrazonSocial + '\'' +
               ", telefono='" + telefono + '\'' +
               ", estado=" + estado +
               ", fechaCreacion=" + fechaCreacion +
               '}';
    }
}
