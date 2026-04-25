package com.serviflow.user.application;

import com.serviflow.user.application.output.UserOutput;
import com.serviflow.user.domain.port.UserRepository;
import java.util.List;

/**
 * Use case for listing all users.
 */
public class ListUsersUseCase {
    
    private final UserRepository userRepository;

    public ListUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executes the use case to list all users.
     * 
     * @return List of UserOutput records
     */
    public List<UserOutput> execute() {
        return userRepository.findAll()
            .stream()
            .map(UserOutput::fromDomain)
            .toList();
    }
}
