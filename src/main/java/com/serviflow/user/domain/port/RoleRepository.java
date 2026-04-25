package com.serviflow.user.domain.port;

import com.serviflow.user.domain.entity.Role;

import java.util.Optional;

/**
 * Output port for Role persistence operations.
 * Pure interface with no framework dependencies.
 */
public interface RoleRepository {

    /**
     * Finds a role by name.
     */
    Optional<Role> findByName(String name);

    /**
     * Checks if a role exists with the given name.
     */
    boolean existsByName(String name);
}
