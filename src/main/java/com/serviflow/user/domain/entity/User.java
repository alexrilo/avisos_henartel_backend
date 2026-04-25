package com.serviflow.user.domain.entity;

import com.serviflow.user.domain.valueobject.Email;
import com.serviflow.user.domain.valueobject.UserId;
import com.serviflow.user.domain.valueobject.UserStatus;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rich domain model representing a User in the system.
 * Contains all business rules and validation logic.
 * This is a VALUE OBJECT pattern - immutable after creation.
 */
public final class User {

    private final UserId id;
    private final String username;
    private final String password;
    private final String nombre;
    private final String apellido;
    private final Email email;
    private final Role role;
    private final UserStatus status;
    private final LocalDateTime createdAt;

    /**
     * Private constructor - use factory methods to create instances.
     * ID can be null for new users that haven't been persisted yet.
     */
    private User(UserId id, String username, String password, String nombre, 
                 String apellido, Email email, Role role, UserStatus status, LocalDateTime createdAt) {
        this.id = id; // ID can be null for new users
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.password = Objects.requireNonNull(password, "Password cannot be null");
        this.nombre = Objects.requireNonNull(nombre, "Nombre cannot be null");
        this.apellido = Objects.requireNonNull(apellido, "Apellido cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.role = Objects.requireNonNull(role, "Role cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt cannot be null");
    }

    /**
     * Factory method for creating a new user.
     * The ID is null as it will be assigned by persistence.
     */
    public static User create(String username, String hashedPassword, String nombre, 
                               String apellido, Email email, Role role) {
        return new User(
            null,
            username,
            hashedPassword,
            nombre,
            apellido,
            email,
            role,
            UserStatus.ACTIVE,
            LocalDateTime.now()
        );
    }

    /**
     * Factory method for reconstituting a user from persistence.
     */
    public static User reconstitute(UserId id, String username, String password, String nombre,
                                    String apellido, Email email, Role role, UserStatus status, LocalDateTime createdAt) {
        return new User(id, username, password, nombre, apellido, email, role, status, createdAt);
    }

    // ==================== Business Methods - State Transitions ====================

    /**
     * Deactivates the user if currently active.
     * Returns a new User instance with the updated status (immutable).
     */
    public User deactivate() {
        if (this.status == UserStatus.INACTIVE) {
            return this;
        }
        return new User(this.id, this.username, this.password, this.nombre, this.apellido, 
                       this.email, this.role, UserStatus.INACTIVE, this.createdAt);
    }

    /**
     * Activates the user if currently inactive.
     * Returns a new User instance with the updated status (immutable).
     */
    public User activate() {
        if (this.status == UserStatus.ACTIVE) {
            return this;
        }
        return new User(this.id, this.username, this.password, this.nombre, this.apellido,
                       this.email, this.role, UserStatus.ACTIVE, this.createdAt);
    }

    /**
     * Checks if the user can be deactivated.
     */
    public boolean canBeDeactivated() {
        return this.status == UserStatus.ACTIVE;
    }

    /**
     * Checks if the user can be activated.
     */
    public boolean canBeActivated() {
        return this.status == UserStatus.INACTIVE;
    }

    /**
     * Checks if the user is currently active.
     */
    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    // ==================== Role-Based Access ====================

    /**
     * Checks if the user has the specified role.
     */
    public boolean hasRole(Role role) {
        return this.role == role;
    }

    /**
     * Checks if the user is an administrator.
     */
    public boolean isAdmin() {
        return this.role == Role.ADMINISTRADOR;
    }

    /**
     * Checks if the user is a coordinator.
     */
    public boolean isCoordinator() {
        return this.role == Role.COORDINADOR;
    }

    /**
     * Checks if the user is a technician.
     */
    public boolean isTechnician() {
        return this.role == Role.TECNICO;
    }

    // ==================== Update Methods ====================

    /**
     * Updates user information.
     * Returns a new User instance (immutable).
     */
    public User updateInfo(String nombre, String apellido, Email email) {
        return new User(this.id, this.username, this.password, nombre, apellido,
                       email, this.role, this.status, this.createdAt);
    }

    /**
     * Changes the user's role.
     * Returns a new User instance (immutable).
     */
    public User changeRole(Role newRole) {
        return new User(this.id, this.username, this.password, this.nombre, this.apellido,
                       this.email, newRole, this.status, this.createdAt);
    }

    /**
     * Updates the password.
     * Returns a new User instance (immutable).
     */
    public User changePassword(String newHashedPassword) {
        return new User(this.id, this.username, newHashedPassword, this.nombre, this.apellido,
                       this.email, this.role, this.status, this.createdAt);
    }

    // ==================== Getters ====================

    public UserId id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String nombre() {
        return nombre;
    }

    public String apellido() {
        return apellido;
    }

    public Email email() {
        return email;
    }

    public Role role() {
        return role;
    }

    public UserStatus status() {
        return status;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    // ==================== Object Methods ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", nombre='" + nombre + '\'' +
               ", apellido='" + apellido + '\'' +
               ", email=" + email +
               ", role=" + role +
               ", status=" + status +
               ", createdAt=" + createdAt +
               '}';
    }
}
