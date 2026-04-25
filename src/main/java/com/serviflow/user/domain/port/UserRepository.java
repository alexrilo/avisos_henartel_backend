package com.serviflow.user.domain.port;

import com.serviflow.user.domain.entity.User;
import com.serviflow.user.domain.valueobject.Email;
import com.serviflow.user.domain.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Output port for User persistence operations.
 * Pure interface with no framework dependencies.
 */
public interface UserRepository {

    /**
     * Saves a user (create or update).
     */
    User save(User user);

    /**
     * Finds a user by ID.
     */
    Optional<User> findById(UserId id);

    /**
     * Finds a user by username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by email.
     */
    Optional<User> findByEmail(Email email);

    /**
     * Finds all users.
     */
    List<User> findAll();

    /**
     * Checks if a user exists with the given email.
     */
    boolean existsByEmail(Email email);

    /**
     * Checks if a user exists with the given username.
     */
    boolean existsByUsername(String username);

    /**
     * Deletes a user by ID.
     */
    void deleteById(UserId id);
}
