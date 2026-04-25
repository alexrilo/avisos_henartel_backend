package com.serviflow.user.infrastructure.persistence;

import com.serviflow.user.domain.entity.User;
import com.serviflow.user.domain.port.UserRepository;
import com.serviflow.user.domain.valueobject.Email;
import com.serviflow.user.domain.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Adapter implementing the domain UserRepository port.
 * Bridges domain layer with JPA persistence.
 */
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaRepository;
    private final JpaRoleRepository jpaRoleRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    public UserRepositoryAdapter(JpaUserRepository jpaRepository, JpaRoleRepository jpaRoleRepository, UserMapper userMapper, RoleMapper roleMapper) {
        this.jpaRepository = jpaRepository;
        this.jpaRoleRepository = jpaRoleRepository;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public User save(User user) {
        // Fetch the existing role from database to avoid TransientPropertyValueException
        // The role must be persisted before associating it with the user
        JpaRoleEntity.RoleType roleType = roleMapper.toJpaRoleType(user.role());
        // Convert RoleType to String for PostgreSQL ENUM casting
        String roleName = roleType.name();
        JpaRoleEntity existingRole = jpaRoleRepository.findByNombre(roleName)
            .orElseThrow(() -> new IllegalStateException("Role not found in database: " + roleType));
        
        JpaUserEntity entity = userMapper.toJpa(user, existingRole);
        // Use saveAndFlush to ensure ID is generated immediately
        // This is critical when @Transactional doesn't work (e.g., use case instantiated with 'new')
        JpaUserEntity saved = jpaRepository.saveAndFlush(entity);
        return userMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UserId id) {
        return jpaRepository.findById(id.value())
            .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
            .map(userMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return jpaRepository.findByEmail(email.value())
            .map(userMapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll()
            .stream()
            .map(userMapper::toDomain)
            .toList();
    }

    @Override
    public boolean existsByEmail(Email email) {
        return jpaRepository.existsByEmail(email.value());
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public void deleteById(UserId id) {
        jpaRepository.deleteById(id.value());
    }
}
