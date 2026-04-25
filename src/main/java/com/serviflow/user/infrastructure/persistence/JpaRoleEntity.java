package com.serviflow.user.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity for Role persistence.
 * Separate from domain Role enum - handles database mapping only.
 */
@Entity
@Table(name = "roles")
public class JpaRoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "nombre", nullable = false, unique = true, columnDefinition = "rol_nombre")
    private RoleType nombre;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Default constructor for JPA.
     */
    public JpaRoleEntity() {
    }

    /**
     * Constructor with role type.
     */
    public JpaRoleEntity(RoleType nombre) {
        this.nombre = nombre;
    }

    // ==================== Getters ====================

    public Long getId() {
        return id;
    }

    public RoleType getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ==================== Setters ====================

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(RoleType nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Enum representing role types in the database.
     * Maps to the domain Role enum.
     */
    public enum RoleType {
        ADMINISTRADOR,
        COORDINADOR,
        TECNICO
    }
}
