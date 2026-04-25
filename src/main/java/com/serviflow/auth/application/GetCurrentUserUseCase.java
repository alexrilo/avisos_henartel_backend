package com.serviflow.auth.application;

import com.serviflow.auth.application.output.UserOutput;
import com.serviflow.user.domain.entity.User;
import com.serviflow.user.domain.port.UserRepository;
import com.serviflow.user.domain.valueobject.Email;

/**
 * Use case for retrieving the current authenticated user's profile.
 */
public class GetCurrentUserUseCase {

    private final UserRepository userRepository;

    public GetCurrentUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executes the use case to get current user by username.
     *
     * @param username the username from the authentication principal
     * @return UserOutput with profile data
     */
    public UserOutput execute(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new com.serviflow.user.domain.exception.UserNotFoundException(username));

        Long tecnicoId = user.isTechnician() && user.id() != null ? user.id().value() : null;

        return new UserOutput(
            user.id() != null ? user.id().value() : null,
            user.nombre(),
            user.apellido(),
            user.email().value(),
            user.role().name(),
            tecnicoId
        );
    }
}
