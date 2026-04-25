package com.serviflow.aviso.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity for Observacion persistence.
 * This entity is part of the infrastructure layer and should NOT import domain classes.
 */
@Entity
@Table(name = "observaciones")
public class JpaObservacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aviso_id", nullable = false)
    private JpaAvisoEntity aviso;

    @Column(nullable = false, length = 1000)
    private String contenido;

    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(nullable = false, length = 100)
    private String usuario;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    public JpaObservacionEntity() {
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JpaAvisoEntity getAviso() {
        return aviso;
    }

    public void setAviso(JpaAvisoEntity aviso) {
        this.aviso = aviso;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}