package com.serviflow.aviso.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for JpaAvisoEntity.
 * Provides standard CRUD operations and custom queries.
 */
@Repository
public interface JpaAvisoRepository extends JpaRepository<JpaAvisoEntity, Long>, JpaSpecificationExecutor<JpaAvisoEntity> {

    /**
     * Finds an aviso by its correlative number.
     */
    Optional<JpaAvisoEntity> findByNumeroCorrelativo(String numeroCorrelativo);

    /**
     * Finds all avisos assigned to a specific technician.
     */
    List<JpaAvisoEntity> findByTecnicoId(Long tecnicoId);

    /**
     * Counts avisos created in a specific year.
     */
    @Query("SELECT COALESCE(MAX(a.id), 0) FROM JpaAvisoEntity a WHERE YEAR(a.fechaCreacion) = :year")
    int countByYear(@Param("year") int year);
}