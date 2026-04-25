package com.serviflow.user.infrastructure.web;

import com.serviflow.user.application.CreateUserUseCase;
import com.serviflow.user.application.GetUserUseCase;
import com.serviflow.user.application.ListUsersUseCase;
import com.serviflow.user.application.ToggleUserActiveUseCase;
import com.serviflow.user.application.UpdateUserUseCase;
import com.serviflow.user.application.input.CreateUserInput;
import com.serviflow.user.application.input.UpdateUserInput;
import com.serviflow.user.application.output.UserOutput;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Web controller for user management operations.
 * Thin controller that delegates all business logic to use cases.
 */
@RestController
@RequestMapping("/api/usuarios")
@Validated
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final ToggleUserActiveUseCase toggleUserActiveUseCase;

    public UserController(CreateUserUseCase createUserUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          GetUserUseCase getUserUseCase,
                          ListUsersUseCase listUsersUseCase,
                          ToggleUserActiveUseCase toggleUserActiveUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.toggleUserActiveUseCase = toggleUserActiveUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserOutput> createUser(@Valid @RequestBody CreateUserRequest request) {
        CreateUserInput input = new CreateUserInput(
            request.username(),
            request.password(),
            request.nombre(),
            request.apellido(),
            request.email(),
            request.role()
        );
        UserOutput output = createUserUseCase.execute(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(output);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'COORDINADOR')")
    public ResponseEntity<List<UserOutput>> listUsers() {
        List<UserOutput> users = listUsersUseCase.execute();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserOutput> getUser(@PathVariable Long id) {
        UserOutput user = getUserUseCase.execute(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserOutput> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateUserRequest request) {
        UpdateUserInput input = new UpdateUserInput(
            id,
            request.nombre(),
            request.apellido(),
            request.email()
        );
        UserOutput output = updateUserUseCase.execute(input);
        return ResponseEntity.ok(output);
    }

    @PutMapping("/{id}/toggle-activo")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserOutput> toggleUserActive(@PathVariable Long id) {
        UserOutput output = toggleUserActiveUseCase.execute(id);
        return ResponseEntity.ok(output);
    }

    /**
     * Request DTO for creating a user.
     * Validation annotations here for HTTP layer.
     */
    public record CreateUserRequest(
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

        @NotBlank(message = "Role is required")
        String role
    ) {}

    /**
     * Request DTO for updating a user.
     */
    public record UpdateUserRequest(
        @NotBlank(message = "Nombre is required")
        String nombre,

        @NotBlank(message = "Apellido is required")
        String apellido,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
    ) {}
}