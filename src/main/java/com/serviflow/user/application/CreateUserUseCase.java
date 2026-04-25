package com.serviflow.user.application;

import com.serviflow.shared.domain.exception.DomainException;
import com.serviflow.user.application.input.CreateUserInput;
import com.serviflow.user.application.output.UserOutput;
import com.serviflow.user.application.exception.DuplicateUserException;
import com.serviflow.user.domain.entity.Role;
import com.serviflow.user.domain.entity.User;
import com.serviflow.user.domain.port.PasswordEncoder;
import com.serviflow.user.domain.port.UserRepository;
import com.serviflow.user.domain.valueobject.Email;
import jakarta.transaction.Transactional;

/**
 * Use case for creating a new user.
 * Orchestrates the creation flow by validating duplicates and delegating to domain.
 */
public class CreateUserUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Executes the use case to create a new user.
     * 
     * @param input CreateUserInput containing user data
     * @return UserOutput with the created user data
     * @throws DuplicateUserException if email or username already exists
     * @throws DomainException if email format is invalid
     */
    @Transactional
    public UserOutput execute(CreateUserInput input) {
        Email email = new Email(input.email());
        
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateUserException("email", input.email());
        }
        if (userRepository.existsByUsername(input.username())) {
            throw new DuplicateUserException("username", input.username());
        }

        Role role = Role.valueOf(input.roleName());
        String hashedPassword = passwordEncoder.encode(input.password());
        
        User user = User.create(input.username(), hashedPassword, input.nombre(), 
                                input.apellido(), email, role);
        User saved = userRepository.save(user);
        
        return UserOutput.fromDomain(saved);
    }
}
