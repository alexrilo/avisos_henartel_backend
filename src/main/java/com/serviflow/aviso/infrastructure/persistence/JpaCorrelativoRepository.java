package com.serviflow.aviso.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for JpaCorrelativoEntity.
 */
@Repository
public interface JpaCorrelativoRepository extends JpaRepository<JpaCorrelativoEntity, Integer> {
}