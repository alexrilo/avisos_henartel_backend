package com.serviflow.cliente.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for Cliente entities.
 */
@Repository
public interface JpaClienteRepository extends JpaRepository<JpaClienteEntity, Long>, JpaSpecificationExecutor<JpaClienteEntity> {

    boolean existsByTelefono(String telefono);

    boolean existsByTelefonoAndIdNot(String telefono, Long id);
}
