package com.serviflow.user.application.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Input record for creating a new user.
 * Validated with Jakarta Bean Validation annotations.
 */
public record CreateUserInput(
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    String username,
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password,
    
    @NotBlank(message = "Nombre is required")
    String nombre,
    
    @NotBlank(message = "Apellido is required")
    String apellido,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotNull(message = "Role is required")
    String roleName
) {}
