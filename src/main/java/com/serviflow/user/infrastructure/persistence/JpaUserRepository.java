package com.serviflow.user.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for User persistence.
 */
@Repository
public interface JpaUserRepository extends JpaRepository<JpaUserEntity, Long> {

    /**
     * Finds a user by username.
     */
    Optional<JpaUserEntity> findByUsername(String username);

    /**
     * Finds a user by email.
     */
    Optional<JpaUserEntity> findByEmail(String email);

    /**
     * Checks if a user exists with the given email.
     */
    boolean existsByEmail(String email);

    /**
     * Checks if a user exists with the given username.
     */
    boolean existsByUsername(String username);
}
