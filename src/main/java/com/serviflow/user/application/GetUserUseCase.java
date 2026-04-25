package com.serviflow.user.application;

import com.serviflow.user.application.output.UserOutput;
import com.serviflow.user.domain.exception.UserNotFoundException;
import com.serviflow.user.domain.port.UserRepository;
import com.serviflow.user.domain.valueobject.UserId;

/**
 * Use case for retrieving a user by ID.
 */
public class GetUserUseCase {
    
    private final UserRepository userRepository;

    public GetUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executes the use case to get a user by ID.
     * 
     * @param userId the user ID to search for
     * @return UserOutput with the user data
     * @throws UserNotFoundException if user does not exist
     */
    public UserOutput execute(Long userId) {
        UserId id = new UserId(userId);
        return userRepository.findById(id)
            .map(UserOutput::fromDomain)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
