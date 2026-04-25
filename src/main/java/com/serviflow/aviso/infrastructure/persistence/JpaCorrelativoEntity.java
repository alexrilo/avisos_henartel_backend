package com.serviflow.aviso.infrastructure.persistence;

import jakarta.persistence.*;

/**
 * JPA entity for storing annual correlative sequence numbers.
 * This entity is part of the infrastructure layer and should NOT import domain classes.
 */
@Entity
@Table(name = "aviso_correlativos")
public class JpaCorrelativoEntity {

    @Id
    private Integer year;

    @Column(nullable = false)
    private Integer lastSequence;

    public JpaCorrelativoEntity() {
    }

    public JpaCorrelativoEntity(Integer year, Integer lastSequence) {
        this.year = year;
        this.lastSequence = lastSequence;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getLastSequence() {
        return lastSequence;
    }

    public void setLastSequence(Integer lastSequence) {
        this.lastSequence = lastSequence;
    }
}