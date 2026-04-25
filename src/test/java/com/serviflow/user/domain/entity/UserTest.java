package com.serviflow.user.domain.entity;

import com.serviflow.user.domain.valueobject.Email;
import com.serviflow.user.domain.valueobject.UserId;
import com.serviflow.user.domain.valueobject.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("User Entity Tests")
class UserTest {

    @Test
    @DisplayName("should create a new user with factory method")
    void shouldCreateNewUser() {
        // Given
        String username = "johndoe";
        String password = "hashedPassword123";
        String nombre = "John";
        String apellido = "Doe";
        Email email = Email.of("john.doe@serviflow.com");
        Role role = Role.TECNICO;

        // When
        User user = User.create(username, password, nombre, apellido, email, role);

        // Then
        assertThat(user.username()).isEqualTo(username);
        assertThat(user.password()).isEqualTo(password);
        assertThat(user.nombre()).isEqualTo(nombre);
        assertThat(user.apellido()).isEqualTo(apellido);
        assertThat(user.email()).isEqualTo(email);
        assertThat(user.role()).isEqualTo(role);
        assertThat(user.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.id()).isNull();
        assertThat(user.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("should reconstitute a user from persistence")
    void shouldReconstituteUser() {
        // Given
        UserId id = UserId.of(1L);
        String username = "johndoe";
        String password = "hashedPassword123";
        String nombre = "John";
        String apellido = "Doe";
        Email email = Email.of("john.doe@serviflow.com");
        Role role = Role.TECNICO;
        UserStatus status = UserStatus.ACTIVE;
        LocalDateTime createdAt = LocalDateTime.now();

        // When
        User user = User.reconstitute(id, username, password, nombre, apellido, email, role, status, createdAt);

        // Then
        assertThat(user.id()).isEqualTo(id);
        assertThat(user.username()).isEqualTo(username);
        assertThat(user.status()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("should deactivate an active user")
    void shouldDeactivateActiveUser() {
        // Given
        User user = User.create("johndoe", "hash", "John", "Doe", 
                                 Email.of("john@serviflow.com"), Role.TECNICO);

        // When
        User deactivated = user.deactivate();

        // Then
        assertThat(deactivated.status()).isEqualTo(UserStatus.INACTIVE);
        assertThat(user.isActive()).isTrue(); // Original unchanged
        assertThat(deactivated.isActive()).isFalse();
    }

    @Test
    @DisplayName("should not change status when deactivating inactive user")
    void shouldNotDeactivateInactiveUser() {
        // Given
        User user = User.create("johndoe", "hash", "John", "Doe", 
                                 Email.of("john@serviflow.com"), Role.TECNICO);
        User inactive = user.deactivate();

        // When
        User deactivatedAgain = inactive.deactivate();

        // Then
        assertThat(deactivatedAgain).isSameAs(inactive); // Same instance returned
    }

    @Test
    @DisplayName("should activate an inactive user")
    void shouldActivateInactiveUser() {
        // Given
        User user = User.create("johndoe", "hash", "John", "Doe", 
                                 Email.of("john@serviflow.com"), Role.TECNICO);
        User inactive = user.deactivate();

        // When
        User activated = inactive.activate();

        // Then
        assertThat(activated.status()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    @DisplayName("should not change status when activating active user")
    void shouldNotActivateActiveUser() {
        // Given
        User user = User.create("johndoe", "hash", "John", "Doe", 
                                 Email.of("john@serviflow.com"), Role.TECNICO);

        // When
        User activatedAgain = user.activate();

        // Then
        assertThat(activatedAgain).isSameAs(user); // Same instance returned
    }

    @Test
    @DisplayName("should return true for canBeDeactivated when active")
    void shouldReturnTrueForCanBeDeactivatedWhenActive() {
        // Given
        User user = User.create("johndoe", "hash", "John", "Doe", 
                                 Email.of("john@serviflow.com"), Role.TECNICO);

        // Then
        assertThat(user.canBeDeactivated()).isTrue();
        assertThat(user.canBeActivated()).isFalse();
    }

    @Test
    @DisplayName("should return true for canBeActivated when inactive")
    void shouldReturnTrueForCanBeActivatedWhenInactive() {
        // Given
        User user = User.create("johndoe", "hash", "John", "Doe", 
                                 Email.of("john@serviflow.com"), Role.TECNICO);
        User inactive = user.deactivate();

        // Then
        assertThat(inactive.canBeActivated()).isTrue();
        assertThat(inactive.canBeDeactivated()).isFalse();
    }

    @Test
    @DisplayName("should check hasRole correctly")
    void shouldCheckHasRole() {
        // Given
        User user = User.create("johndoe", "hash", "John", "Doe", 
                                 Email.of("john@serviflow.com"), Role.TECNICO);

        // Then
        assertThat(user.hasRole(Role.TECNICO)).isTrue();
        assertThat(user.hasRole(Role.ADMINISTRADOR)).isFalse();
        assertThat(user.hasRole(Role.COORDINADOR)).isFalse();
    }

    @Test
    @DisplayName("should check isAdmin correctly")
    void shouldCheckIsAdmin() {
        // Given
        User admin = User.create("admin", "hash", "Admin", "User", 
                                  Email.of("admin@serviflow.com"), Role.ADMINISTRADOR);
        User tecnico = User.create("tecnico", "hash", "Tech", "User", 
                                    Email.of("tech@serviflow.com"), Role.TECNICO);

        // Then
        assertThat(admin.isAdmin()).isTrue();
        assertThat(tecnico.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("should check isCoordinator correctly")
    void shouldCheckIsCoordinator() {
        // Given
        User coordinator = User.create("coord", "hash", "Coord", "User", 
                                       Email.of("coord@serviflow.com"), Role.COORDINADOR);
        User tecnico = User.create("tecnico", "hash", "Tech", "User", 
                                    Email.of("tech@serviflow.com"), Role.TECNICO);

        // Then
        assertThat(coordinator.isCoordinator()).isTrue();
        assertThat(tecnico.isCoordinator()).isFalse();
    }

    @Test
    @DisplayName("should check isTechnician correctly")
    void shouldCheckIsTechnician() {
        // Given
        User tecnico = User.create("tecnico", "hash", "Tech", "User", 
                                    Email.of("tech@serviflow.com"), Role.TECNICO);

        // Then
        assertThat(tecnico.isTechnician()).isTrue();
    }

    @Test
    @DisplayName("should update info and return new instance")
    void shouldUpdateInfo() {
        // Given
        User user = User.create("johndoe", "hash", "John", "Doe", 
                                 Email.of("john@serviflow.com"), Role.TECNICO);

        // When
        User updated = user.updateInfo("Johnny", "Doe Jr", Email.of("johnny@serviflow.com"));

        // Then
        assertThat(updated.nombre()).isEqualTo("Johnny");
        assertThat(updated.apellido()).isEqualTo("Doe Jr");
        assertThat(updated.email().value()).isEqualTo("johnny@serviflow.com");
        // Original unchanged
        assertThat(user.nombre()).isEqualTo("John");
        assertThat(user.apellido()).isEqualTo("Doe");
    }

    @Test
    @DisplayName("should change role and return new instance")
    void shouldChangeRole() {
        // Given
        User tecnico = User.create("tecnico", "hash", "Tech", "User", 
                                    Email.of("tech@serviflow.com"), Role.TECNICO);

        // When
        User coordinator = tecnico.changeRole(Role.COORDINADOR);

        // Then
        assertThat(coordinator.role()).isEqualTo(Role.COORDINADOR);
        assertThat(tecnico.role()).isEqualTo(Role.TECNICO); // Original unchanged
    }

    @Test
    @DisplayName("should change password and return new instance")
    void shouldChangePassword() {
        // Given
        User user = User.create("johndoe", "oldHash", "John", "Doe", 
                                 Email.of("john@serviflow.com"), Role.TECNICO);

        // When
        User updated = user.changePassword("newHash");

        // Then
        assertThat(updated.password()).isEqualTo("newHash");
        assertThat(user.password()).isEqualTo("oldHash"); // Original unchanged
    }

    @Test
    @DisplayName("should have proper equals and hashCode")
    void shouldHaveProperEqualsAndHashCode() {
        // Given
        UserId id1 = UserId.of(1L);
        User user1 = User.reconstitute(id1, "johndoe", "hash", "John", "Doe",
                                        Email.of("john@serviflow.com"), Role.TECNICO, 
                                        UserStatus.ACTIVE, LocalDateTime.now());
        User user2 = User.reconstitute(id1, "johndoe", "hash", "John", "Doe",
                                        Email.of("john@serviflow.com"), Role.TECNICO, 
                                        UserStatus.ACTIVE, LocalDateTime.now());

        // Then
        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }
}
