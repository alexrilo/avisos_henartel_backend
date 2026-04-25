package com.serviflow.cliente.domain.entity;

import com.serviflow.cliente.domain.valueobject.ClienteId;
import com.serviflow.cliente.domain.valueobject.ClienteStatus;
import com.serviflow.cliente.domain.valueobject.TipoCliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Cliente domain entity.
 */
class ClienteTest {

    @Nested
    @DisplayName("Factory Methods")
    class FactoryMethods {

        @Test
        @DisplayName("createParticular should create cliente with tipo PARTICULAR")
        void createParticular_shouldCreateClienteWithTipoParticular() {
            Cliente cliente = Cliente.createParticular("Juan Pérez", "1234567890");

            assertEquals(TipoCliente.PARTICULAR, cliente.tipo());
            assertEquals("Juan Pérez", cliente.nombreOrazonSocial());
            assertEquals("1234567890", cliente.telefono());
            assertEquals(ClienteStatus.ACTIVO, cliente.estado());
            assertNotNull(cliente.fechaCreacion());
        }

        @Test
        @DisplayName("createParticular with all fields should create cliente correctly")
        void createParticular_withAllFields_shouldCreateClienteCorrectly() {
            Cliente cliente = Cliente.createParticular("Juan Pérez", "1234567890", "María López", "Notas importantes");

            assertEquals(TipoCliente.PARTICULAR, cliente.tipo());
            assertEquals("Juan Pérez", cliente.nombreOrazonSocial());
            assertEquals("1234567890", cliente.telefono());
            assertEquals("María López", cliente.personaContacto());
            assertEquals("Notas importantes", cliente.observaciones());
            assertEquals(ClienteStatus.ACTIVO, cliente.estado());
        }

        @Test
        @DisplayName("createEmpresa should create cliente with tipo EMPRESA")
        void createEmpresa_shouldCreateClienteWithTipoEmpresa() {
            Cliente cliente = Cliente.createEmpresa("Acme Corporation", "9876543210");

            assertEquals(TipoCliente.EMPRESA, cliente.tipo());
            assertEquals("Acme Corporation", cliente.nombreOrazonSocial());
            assertEquals("9876543210", cliente.telefono());
            assertEquals(ClienteStatus.ACTIVO, cliente.estado());
        }

        @Test
        @DisplayName("createEmpresa with all fields should create cliente correctly")
        void createEmpresa_withAllFields_shouldCreateClienteCorrectly() {
            Cliente cliente = Cliente.createEmpresa("Acme Corporation", "9876543210", "John Smith", "Important notes");

            assertEquals(TipoCliente.EMPRESA, cliente.tipo());
            assertEquals("Acme Corporation", cliente.nombreOrazonSocial());
            assertEquals("9876543210", cliente.telefono());
            assertEquals("John Smith", cliente.personaContacto());
            assertEquals("Important notes", cliente.observaciones());
        }

        @Test
        @DisplayName("create generic should default to ACTIVO status")
        void create_shouldDefaultToActivoStatus() {
            Cliente cliente = Cliente.create(TipoCliente.PARTICULAR, "Test", "123");

            assertEquals(ClienteStatus.ACTIVO, cliente.estado());
        }

        @Test
        @DisplayName("reconstitute should create cliente with all fields from persistence")
        void reconstitute_shouldCreateClienteFromPersistence() {
            LocalDateTime now = LocalDateTime.now();
            ClienteId id = new ClienteId(1L);

            Cliente cliente = Cliente.reconstitute(
                id,
                TipoCliente.EMPRESA,
                "Empresa SA",
                "1234567890",
                "Contacto",
                "Notas",
                ClienteStatus.ACTIVO,
                now,
                now
            );

            assertEquals(id, cliente.id());
            assertEquals(TipoCliente.EMPRESA, cliente.tipo());
            assertEquals("Empresa SA", cliente.nombreOrazonSocial());
            assertEquals("1234567890", cliente.telefono());
            assertEquals("Contacto", cliente.personaContacto());
            assertEquals("Notas", cliente.observaciones());
            assertEquals(ClienteStatus.ACTIVO, cliente.estado());
            assertEquals(now, cliente.fechaCreacion());
            assertEquals(now, cliente.fechaModificacion());
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("should reject null nombre")
        void shouldRejectNullNombre() {
            // Objects.requireNonNull throws NullPointerException
            assertThrows(NullPointerException.class, () ->
                Cliente.create(TipoCliente.PARTICULAR, null, "1234567890")
            );
        }

        @Test
        @DisplayName("should reject blank nombre")
        void shouldRejectBlankNombre() {
            assertThrows(IllegalArgumentException.class, () ->
                Cliente.create(TipoCliente.PARTICULAR, "   ", "1234567890")
            );
        }

        @Test
        @DisplayName("should reject nombre exceeding 200 characters")
        void shouldRejectNombreExceeding200Characters() {
            String longName = "A".repeat(201);
            assertThrows(IllegalArgumentException.class, () ->
                Cliente.create(TipoCliente.PARTICULAR, longName, "1234567890")
            );
        }

        @Test
        @DisplayName("should reject null telefono")
        void shouldRejectNullTelefono() {
            // Objects.requireNonNull throws NullPointerException
            assertThrows(NullPointerException.class, () ->
                Cliente.create(TipoCliente.PARTICULAR, "Test", null)
            );
        }

        @Test
        @DisplayName("should reject blank telefono")
        void shouldRejectBlankTelefono() {
            assertThrows(IllegalArgumentException.class, () ->
                Cliente.create(TipoCliente.PARTICULAR, "Test", "   ")
            );
        }

        @Test
        @DisplayName("should reject telefono exceeding 50 characters")
        void shouldRejectTelefonoExceeding50Characters() {
            String longPhone = "1".repeat(51);
            assertThrows(IllegalArgumentException.class, () ->
                Cliente.create(TipoCliente.PARTICULAR, "Test", longPhone)
            );
        }

        @Test
        @DisplayName("should trim nombre")
        void shouldTrimNombre() {
            Cliente cliente = Cliente.create(TipoCliente.PARTICULAR, "  Juan Pérez  ", "1234567890");

            assertEquals("Juan Pérez", cliente.nombreOrazonSocial());
        }

        @Test
        @DisplayName("should trim telefono")
        void shouldTrimTelefono() {
            Cliente cliente = Cliente.create(TipoCliente.PARTICULAR, "Test", "  1234567890  ");

            assertEquals("1234567890", cliente.telefono());
        }

        @Test
        @DisplayName("should allow null optional fields")
        void shouldAllowNullOptionalFields() {
            Cliente cliente = Cliente.create(TipoCliente.PARTICULAR, "Test", "123", null, null);

            assertNull(cliente.personaContacto());
            assertNull(cliente.observaciones());
        }

        @Test
        @DisplayName("should trim optional fields")
        void shouldTrimOptionalFields() {
            Cliente cliente = Cliente.create(TipoCliente.PARTICULAR, "Test", "123", "  Contacto  ", "  Notas  ");

            assertEquals("Contacto", cliente.personaContacto());
            assertEquals("Notas", cliente.observaciones());
        }
    }

    @Nested
    @DisplayName("State Transitions")
    class StateTransitions {

        @Test
        @DisplayName("deactivate should change ACTIVO to INACTIVO")
        void deactivate_shouldChangeActivoToInactivo() {
            Cliente cliente = Cliente.createParticular("Test", "123");
            Cliente deactivated = cliente.deactivate();

            assertEquals(ClienteStatus.INACTIVO, deactivated.estado());
            assertEquals(ClienteStatus.ACTIVO, cliente.estado()); // Original unchanged
        }

        @Test
        @DisplayName("deactivate should return same instance if already INACTIVO")
        void deactivate_shouldReturnSameInstanceIfAlreadyInactivo() {
            Cliente cliente = Cliente.createParticular("Test", "123").deactivate();
            Cliente deactivated = cliente.deactivate();

            assertSame(cliente, deactivated);
        }

        @Test
        @DisplayName("activate should change INACTIVO to ACTIVO")
        void activate_shouldChangeInactivoToActivo() {
            Cliente cliente = Cliente.createParticular("Test", "123").deactivate();
            Cliente activated = cliente.activate();

            assertEquals(ClienteStatus.ACTIVO, activated.estado());
        }

        @Test
        @DisplayName("activate should return same instance if already ACTIVO")
        void activate_shouldReturnSameInstanceIfAlreadyActivo() {
            Cliente cliente = Cliente.createParticular("Test", "123");
            Cliente activated = cliente.activate();

            assertSame(cliente, activated);
        }

        @Test
        @DisplayName("toggleStatus should toggle ACTIVO to INACTIVO")
        void toggleStatus_shouldToggleActivoToInactivo() {
            Cliente cliente = Cliente.createParticular("Test", "123");
            Cliente toggled = cliente.toggleStatus();

            assertEquals(ClienteStatus.INACTIVO, toggled.estado());
        }

        @Test
        @DisplayName("toggleStatus should toggle INACTIVO to ACTIVO")
        void toggleStatus_shouldToggleInactivoToActivo() {
            Cliente cliente = Cliente.createParticular("Test", "123").deactivate();
            Cliente toggled = cliente.toggleStatus();

            assertEquals(ClienteStatus.ACTIVO, toggled.estado());
        }

        @Test
        @DisplayName("isActive should return true for ACTIVO")
        void isActive_shouldReturnTrueForActivo() {
            Cliente cliente = Cliente.createParticular("Test", "123");

            assertTrue(cliente.isActive());
        }

        @Test
        @DisplayName("isActive should return false for INACTIVO")
        void isActive_shouldReturnFalseForInactivo() {
            Cliente cliente = Cliente.createParticular("Test", "123").deactivate();

            assertFalse(cliente.isActive());
        }

        @Test
        @DisplayName("canBeDeactivated should return true for ACTIVO")
        void canBeDeactivated_shouldReturnTrueForActivo() {
            Cliente cliente = Cliente.createParticular("Test", "123");

            assertTrue(cliente.canBeDeactivated());
        }

        @Test
        @DisplayName("canBeDeactivated should return false for INACTIVO")
        void canBeDeactivated_shouldReturnFalseForInactivo() {
            Cliente cliente = Cliente.createParticular("Test", "123").deactivate();

            assertFalse(cliente.canBeDeactivated());
        }

        @Test
        @DisplayName("canBeActivated should return true for INACTIVO")
        void canBeActivated_shouldReturnTrueForInactivo() {
            Cliente cliente = Cliente.createParticular("Test", "123").deactivate();

            assertTrue(cliente.canBeActivated());
        }

        @Test
        @DisplayName("canBeActivated should return false for ACTIVO")
        void canBeActivated_shouldReturnFalseForActivo() {
            Cliente cliente = Cliente.createParticular("Test", "123");

            assertFalse(cliente.canBeActivated());
        }
    }

    @Nested
    @DisplayName("Update Methods")
    class UpdateMethods {

        @Test
        @DisplayName("updateInfo should return new instance with updated values")
        void updateInfo_shouldReturnNewInstanceWithUpdatedValues() {
            Cliente original = Cliente.createParticular("Juan", "123");
            Cliente updated = original.updateInfo("Juan Actualizado", "456", "Contacto", "Notas");

            assertEquals("Juan Actualizado", updated.nombreOrazonSocial());
            assertEquals("456", updated.telefono());
            assertEquals("Contacto", updated.personaContacto());
            assertEquals("Notas", updated.observaciones());
            assertEquals(TipoCliente.PARTICULAR, updated.tipo());
            assertEquals(ClienteStatus.ACTIVO, updated.estado());
        }

        @Test
        @DisplayName("updateInfo should keep original unchanged")
        void updateInfo_shouldKeepOriginalUnchanged() {
            Cliente original = Cliente.createParticular("Juan", "123");
            Cliente updated = original.updateInfo("Juan Updated", "456", null, null);

            assertEquals("Juan", original.nombreOrazonSocial());
            assertEquals("123", original.telefono());
        }

        @Test
        @DisplayName("updateInfo should update fechaModificacion")
        void updateInfo_shouldUpdateFechaModificacion() {
            Cliente original = Cliente.createParticular("Juan", "123");
            assertNull(original.fechaModificacion());

            Cliente updated = original.updateInfo("Juan Updated", "456", null, null);

            assertNotNull(updated.fechaModificacion());
        }

        @Test
        @DisplayName("changeTipo should return new instance with new tipo")
        void changeTipo_shouldReturnNewInstanceWithNewTipo() {
            Cliente original = Cliente.createParticular("Juan", "123");
            Cliente changed = original.changeTipo(TipoCliente.EMPRESA);

            assertEquals(TipoCliente.EMPRESA, changed.tipo());
            assertEquals(TipoCliente.PARTICULAR, original.tipo());
        }
    }

    @Nested
    @DisplayName("Default Status")
    class DefaultStatus {

        @Test
        @DisplayName("new cliente should default to ACTIVO status")
        void newCliente_shouldDefaultToActivoStatus() {
            Cliente particular = Cliente.createParticular("Juan", "123");
            Cliente empresa = Cliente.createEmpresa("Acme", "456");

            assertEquals(ClienteStatus.ACTIVO, particular.estado());
            assertEquals(ClienteStatus.ACTIVO, empresa.estado());
        }
    }

    @Nested
    @DisplayName("Object Methods")
    class ObjectMethods {

        @Test
        @DisplayName("equals should be true for same id and telefono")
        void equals_shouldBeTrueForSameIdAndTelefono() {
            ClienteId id = new ClienteId(1L);
            Cliente c1 = Cliente.reconstitute(id, TipoCliente.PARTICULAR, "Test", "123", null, null, ClienteStatus.ACTIVO, LocalDateTime.now(), null);
            Cliente c2 = Cliente.reconstitute(id, TipoCliente.PARTICULAR, "Test", "123", null, null, ClienteStatus.ACTIVO, LocalDateTime.now(), null);

            assertEquals(c1, c2);
        }

        @Test
        @DisplayName("equals should be false for different id")
        void equals_shouldBeFalseForDifferentId() {
            Cliente c1 = Cliente.reconstitute(new ClienteId(1L), TipoCliente.PARTICULAR, "Test", "123", null, null, ClienteStatus.ACTIVO, LocalDateTime.now(), null);
            Cliente c2 = Cliente.reconstitute(new ClienteId(2L), TipoCliente.PARTICULAR, "Test", "123", null, null, ClienteStatus.ACTIVO, LocalDateTime.now(), null);

            assertNotEquals(c1, c2);
        }

        @Test
        @DisplayName("equals should be false for different telefono")
        void equals_shouldBeFalseForDifferentTelefono() {
            Cliente c1 = Cliente.reconstitute(new ClienteId(1L), TipoCliente.PARTICULAR, "Test", "123", null, null, ClienteStatus.ACTIVO, LocalDateTime.now(), null);
            Cliente c2 = Cliente.reconstitute(new ClienteId(1L), TipoCliente.PARTICULAR, "Test", "456", null, null, ClienteStatus.ACTIVO, LocalDateTime.now(), null);

            assertNotEquals(c1, c2);
        }

        @Test
        @DisplayName("hashCode should be consistent")
        void hashCode_shouldBeConsistent() {
            Cliente c1 = Cliente.reconstitute(new ClienteId(1L), TipoCliente.PARTICULAR, "Test", "123", null, null, ClienteStatus.ACTIVO, LocalDateTime.now(), null);
            Cliente c2 = Cliente.reconstitute(new ClienteId(1L), TipoCliente.PARTICULAR, "Test", "123", null, null, ClienteStatus.ACTIVO, LocalDateTime.now(), null);

            assertEquals(c1.hashCode(), c2.hashCode());
        }

        @Test
        @DisplayName("toString should contain key fields")
        void toString_shouldContainKeyFields() {
            Cliente cliente = Cliente.createParticular("Juan", "123");
            String str = cliente.toString();

            assertTrue(str.contains("Cliente"));
            assertTrue(str.contains("Juan"));
            assertTrue(str.contains("123"));
        }
    }
}
