package com.serviflow.user.application;

import com.serviflow.user.application.output.UserOutput;
import com.serviflow.user.domain.entity.User;
import com.serviflow.user.domain.exception.UserNotFoundException;
import com.serviflow.user.domain.port.UserRepository;
import com.serviflow.user.domain.valueobject.UserId;
import jakarta.transaction.Transactional;

/**
 * Use case for toggling user active status.
 */
public class ToggleUserActiveUseCase {
    
    private final UserRepository userRepository;

    public ToggleUserActiveUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executes the use case to toggle user active status.
     * 
     * @param userId the user ID to toggle
     * @return UserOutput with the updated user data
     * @throws UserNotFoundException if user does not exist
     */
    @Transactional
    public UserOutput execute(Long userId) {
        UserId id = new UserId(userId);
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        User toggled = user.isActive() ? user.deactivate() : user.activate();
        User saved = userRepository.save(toggled);
        
        return UserOutput.fromDomain(saved);
    }
}
