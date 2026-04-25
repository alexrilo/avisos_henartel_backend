package com.serviflow.user.infrastructure.persistence;

import com.serviflow.user.domain.entity.Role;

/**
 * Mapper for converting between domain Role and JpaRoleEntity.
 */
public class RoleMapper {

    /**
     * Converts a JpaRoleEntity to domain Role enum.
     */
    public Role toDomain(JpaRoleEntity entity) {
        if (entity == null) {
            return null;
        }
        return Role.valueOf(entity.getNombre().name());
    }

    /**
     * Converts a domain Role enum to JpaRoleEntity.RoleType.
     */
    public JpaRoleEntity.RoleType toJpaRoleType(Role role) {
        if (role == null) {
            return null;
        }
        return JpaRoleEntity.RoleType.valueOf(role.name());
    }

    /**
     * Creates a JpaRoleEntity from domain Role (for building new entities).
     */
    public JpaRoleEntity toJpaEntity(Role role) {
        if (role == null) {
            return null;
        }
        return new JpaRoleEntity(toJpaRoleType(role));
    }
}
