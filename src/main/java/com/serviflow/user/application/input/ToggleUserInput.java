package com.serviflow.user.application.input;

import jakarta.validation.constraints.NotNull;

/**
 * Input record for toggling user active status.
 */
public record ToggleUserInput(
    
    @NotNull(message = "User ID is required")
    Long id
) {}
