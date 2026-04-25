package com.serviflow.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for Role persistence.
 */
@Repository
public interface JpaRoleRepository extends JpaRepository<JpaRoleEntity, Long> {

    /**
     * Finds a role by name.
     * Uses native query with explicit CAST to handle PostgreSQL ENUM type.
     */
    @Query(value = "SELECT * FROM roles WHERE nombre = CAST(?1 AS rol_nombre)", nativeQuery = true)
    Optional<JpaRoleEntity> findByNombre(String nombre);

    /**
     * Checks if a role exists with the given name.
     * Uses native query with explicit CAST to handle PostgreSQL ENUM type.
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM roles WHERE nombre = CAST(?1 AS rol_nombre)", nativeQuery = true)
    boolean existsByNombre(String nombre);
}
