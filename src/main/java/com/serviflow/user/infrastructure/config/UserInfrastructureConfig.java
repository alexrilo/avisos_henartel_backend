package com.serviflow.user.infrastructure.config;

import com.serviflow.user.domain.port.UserRepository;
import com.serviflow.user.infrastructure.persistence.JpaRoleRepository;
import com.serviflow.user.infrastructure.persistence.JpaUserRepository;
import com.serviflow.user.infrastructure.persistence.RoleMapper;
import com.serviflow.user.infrastructure.persistence.UserMapper;
import com.serviflow.user.infrastructure.persistence.UserRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for User infrastructure components.
 * Wires adapters and mappers as Spring beans.
 */
@Configuration
public class UserInfrastructureConfig {

    @Bean
    public RoleMapper roleMapper() {
        return new RoleMapper();
    }

    @Bean
    public UserMapper userMapper(RoleMapper roleMapper) {
        return new UserMapper(roleMapper);
    }

    @Bean
    public UserRepository userRepository(JpaUserRepository jpaRepository, JpaRoleRepository jpaRoleRepository, UserMapper mapper, RoleMapper roleMapper) {
        return new UserRepositoryAdapter(jpaRepository, jpaRoleRepository, mapper, roleMapper);
    }
}
