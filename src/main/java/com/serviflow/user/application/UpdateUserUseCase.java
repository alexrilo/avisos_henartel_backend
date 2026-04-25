package com.serviflow.user.application;

import com.serviflow.user.application.input.UpdateUserInput;
import com.serviflow.user.application.output.UserOutput;
import com.serviflow.user.domain.entity.User;
import com.serviflow.user.domain.exception.UserNotFoundException;
import com.serviflow.user.domain.port.UserRepository;
import com.serviflow.user.domain.valueobject.Email;
import com.serviflow.user.domain.valueobject.UserId;
import jakarta.transaction.Transactional;

/**
 * Use case for updating user information.
 */
public class UpdateUserUseCase {
    
    private final UserRepository userRepository;

    public UpdateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Executes the use case to update user information.
     * 
     * @param input UpdateUserInput containing user ID and new data
     * @return UserOutput with the updated user data
     * @throws UserNotFoundException if user does not exist
     * @throws DomainException if email format is invalid
     */
    @Transactional
    public UserOutput execute(UpdateUserInput input) {
        UserId id = new UserId(input.id());
        Email email = new Email(input.email());
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(input.id()));
        
        User updated = user.updateInfo(input.nombre(), input.apellido(), email);
        User saved = userRepository.save(updated);
        
        return UserOutput.fromDomain(saved);
    }
}
