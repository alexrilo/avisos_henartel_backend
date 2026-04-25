package com.serviflow.auth.infrastructure.security;

import com.serviflow.user.domain.entity.User;
import com.serviflow.user.domain.port.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Security UserDetailsService adapter.
 * Bridges Spring Security infrastructure with domain UserRepository port.
 * 
 * This is infrastructure because it:
 * - Implements Spring Security's UserDetailsService (framework interface)
 * - Depends on SecurityFilter (infrastructure)
 * - Returns Spring Security's UserDetails (framework class)
 * 
 * Uses domain UserRepository port to keep infrastructure separate from domain.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new UserPrincipal(
                user.id() != null ? user.id().value() : null,
                user.username(),
                user.password(),
                user.isActive(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.role().name()))
        );
    }
}
