package com.serviflow.auth.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Custom UserDetails implementation that carries the user ID.
 * Used to propagate the userId (which equals tecnicoId for technicians)
 * from the JWT through Spring Security's Authentication principal.
 */
public class UserPrincipal extends User {

    private final Long userId;

    public UserPrincipal(Long userId, String username, String password,
                         boolean enabled, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, true, true, true, authorities);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
