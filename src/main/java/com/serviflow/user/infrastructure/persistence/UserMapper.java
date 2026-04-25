package com.serviflow.user.infrastructure.persistence;

import com.serviflow.user.domain.entity.User;
import com.serviflow.user.domain.valueobject.Email;
import com.serviflow.user.domain.valueobject.UserId;
import com.serviflow.user.domain.valueobject.UserStatus;

import java.time.LocalDateTime;

/**
 * Mapper for converting between domain User and JpaUserEntity.
 */
public class UserMapper {

    private final RoleMapper roleMapper;

    public UserMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    /**
     * Converts a JpaUserEntity to domain User.
     */
    public User toDomain(JpaUserEntity entity) {
        if (entity == null) {
            return null;
        }

        UserId userId = entity.getId() != null ? UserId.of(entity.getId()) : null;
        Email email = Email.of(entity.getEmail());
        UserStatus status = entity.getActivo() ? UserStatus.ACTIVE : UserStatus.INACTIVE;
        
        return User.reconstitute(
            userId,
            entity.getUsername(),
            entity.getPassword(),
            entity.getNombre(),
            entity.getApellido(),
            email,
            roleMapper.toDomain(entity.getRole()),
            status,
            entity.getCreatedAt()
        );
    }

    /**
     * Converts a domain User to JpaUserEntity.
     * 
     * @param user The domain user entity
     * @param existingRole Optional pre-fetched JpaRoleEntity from database. 
     *                      If null, a new transient entity will be created (use with caution).
     */
    public JpaUserEntity toJpa(User user, JpaRoleEntity existingRole) {
        if (user == null) {
            return null;
        }

        JpaUserEntity entity = new JpaUserEntity();
        
        if (user.id() != null) {
            entity.setId(user.id().value());
        }
        
        entity.setUsername(user.username());
        entity.setPassword(user.password());
        entity.setNombre(user.nombre());
        entity.setApellido(user.apellido());
        entity.setEmail(user.email().value());
        
        // Use the existing role from DB if provided, otherwise create new (for backward compatibility)
        if (existingRole != null) {
            entity.setRole(existingRole);
        } else {
            entity.setRole(roleMapper.toJpaEntity(user.role()));
        }
        
        entity.setActivo(user.status() == UserStatus.ACTIVE);
        entity.setCreatedAt(user.createdAt());
        
        return entity;
    }

    /**
     * Converts a domain User to JpaUserEntity.
     * @deprecated Use {@link #toJpa(User, JpaRoleEntity)} to avoid TransientPropertyValueException.
     *             This method creates a new transient JpaRoleEntity which is not persisted.
     */
    public JpaUserEntity toJpa(User user) {
        return toJpa(user, null);
    }
}
