package com.serviflow.user.application.input;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Input record for updating user information.
 */
public record UpdateUserInput(
    
    @NotNull(message = "User ID is required")
    Long id,
    
    @NotBlank(message = "Nombre is required")
    String nombre,
    
    @NotBlank(message = "Apellido is required")
    String apellido,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email
) {}
