package com.serviflow.user.application.output;

import com.serviflow.user.domain.entity.User;

/**
 * Output record for user data presentation.
 * Transforms domain User entity to presentation-ready format.
 */
public record UserOutput(
    Long id,
    String username,
    String nombre,
    String apellido,
    String email,
    String role,
    String status,
    String createdAt
) {
    
    /**
     * Factory method to create UserOutput from domain User entity.
     */
    public static UserOutput fromDomain(User user) {
        return new UserOutput(
            user.id() != null ? user.id().value() : null,
            user.username(),
            user.nombre(),
            user.apellido(),
            user.email().value(),
            user.role().name(),
            user.status().name(),
            user.createdAt().toString()
        );
    }
}
