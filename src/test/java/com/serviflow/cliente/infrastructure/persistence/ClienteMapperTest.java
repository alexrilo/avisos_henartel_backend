package com.serviflow.cliente.infrastructure.persistence;

import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.valueobject.ClienteId;
import com.serviflow.cliente.domain.valueobject.ClienteStatus;
import com.serviflow.cliente.domain.valueobject.TipoCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ClienteMapper.
 */
@DisplayName("ClienteMapper")
class ClienteMapperTest {

    private ClienteMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClienteMapper();
    }

    @Nested
    @DisplayName("toDomain")
    class ToDomain {

        @Test
        @DisplayName("should map JPA entity to domain correctly")
        void shouldMapJpaToDomain() {
            // given
            JpaClienteEntity entity = new JpaClienteEntity();
            entity.setId(1L);
            entity.setTipo("PARTICULAR");
            entity.setNombreOrazonSocial("Juan Pérez");
            entity.setTelefono("1234567890");
            entity.setPersonaContacto("María López");
            entity.setObservaciones("Notas importantes");
            entity.setEstado("ACTIVO");
            entity.setFechaCreacion(LocalDateTime.of(2024, 1, 1, 10, 30));
            entity.setFechaModificacion(null);

            // when
            Cliente result = mapper.toDomain(entity);

            // then
            assertNotNull(result);
            assertEquals(new ClienteId(1L), result.id());
            assertEquals(TipoCliente.PARTICULAR, result.tipo());
            assertEquals("Juan Pérez", result.nombreOrazonSocial());
            assertEquals("1234567890", result.telefono());
            assertEquals("María López", result.personaContacto());
            assertEquals("Notas importantes", result.observaciones());
            assertEquals(ClienteStatus.ACTIVO, result.estado());
            assertEquals(LocalDateTime.of(2024, 1, 1, 10, 30), result.fechaCreacion());
            assertNull(result.fechaModificacion());
        }

        @Test
        @DisplayName("should map EMPRESA tipo correctly")
        void shouldMapEmpresaTipoCorrectly() {
            // given
            JpaClienteEntity entity = new JpaClienteEntity();
            entity.setId(2L);
            entity.setTipo("EMPRESA");
            entity.setNombreOrazonSocial("Acme Corp");
            entity.setTelefono("9876543210");
            entity.setPersonaContacto("John Smith");
            entity.setObservaciones("Important notes");
            entity.setEstado("INACTIVO");
            entity.setFechaCreacion(LocalDateTime.now());
            entity.setFechaModificacion(LocalDateTime.now());

            // when
            Cliente result = mapper.toDomain(entity);

            // then
            assertEquals(TipoCliente.EMPRESA, result.tipo());
            assertEquals(ClienteStatus.INACTIVO, result.estado());
        }

        @Test
        @DisplayName("should return null when entity is null")
        void shouldReturnNullWhenEntityIsNull() {
            // when
            Cliente result = mapper.toDomain(null);

            // then
            assertNull(result);
        }

        @Test
        @DisplayName("should handle entity with null id")
        void shouldHandleEntityWithNullId() {
            // given
            JpaClienteEntity entity = new JpaClienteEntity();
            entity.setId(null);
            entity.setTipo("PARTICULAR");
            entity.setNombreOrazonSocial("Test");
            entity.setTelefono("123");
            entity.setEstado("ACTIVO");
            entity.setFechaCreacion(LocalDateTime.now());

            // when
            Cliente result = mapper.toDomain(entity);

            // then
            assertNotNull(result);
            assertNull(result.id());
        }

        @Test
        @DisplayName("should handle entity with null optional fields")
        void shouldHandleEntityWithNullOptionalFields() {
            // given
            JpaClienteEntity entity = new JpaClienteEntity();
            entity.setId(1L);
            entity.setTipo("PARTICULAR");
            entity.setNombreOrazonSocial("Test");
            entity.setTelefono("123");
            entity.setPersonaContacto(null);
            entity.setObservaciones(null);
            entity.setEstado("ACTIVO");
            entity.setFechaCreacion(LocalDateTime.now());
            entity.setFechaModificacion(null);

            // when
            Cliente result = mapper.toDomain(entity);

            // then
            assertNull(result.personaContacto());
            assertNull(result.observaciones());
            assertNull(result.fechaModificacion());
        }
    }

    @Nested
    @DisplayName("toJpa")
    class ToJpa {

        @Test
        @DisplayName("should map domain to JPA entity correctly")
        void shouldMapDomainToJpa() {
            // given
            Cliente cliente = Cliente.reconstitute(
                new ClienteId(1L),
                TipoCliente.PARTICULAR,
                "Juan Pérez",
                "1234567890",
                "María López",
                "Notas importantes",
                ClienteStatus.ACTIVO,
                LocalDateTime.of(2024, 1, 1, 10, 30),
                null
            );

            // when
            JpaClienteEntity result = mapper.toJpa(cliente);

            // then
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("PARTICULAR", result.getTipo());
            assertEquals("Juan Pérez", result.getNombreOrazonSocial());
            assertEquals("1234567890", result.getTelefono());
            assertEquals("María López", result.getPersonaContacto());
            assertEquals("Notas importantes", result.getObservaciones());
            assertEquals("ACTIVO", result.getEstado());
            assertEquals(LocalDateTime.of(2024, 1, 1, 10, 30), result.getFechaCreacion());
            assertNull(result.getFechaModificacion());
        }

        @Test
        @DisplayName("should map EMPRESA tipo correctly")
        void shouldMapEmpresaTipoCorrectly() {
            // given
            Cliente cliente = Cliente.reconstitute(
                new ClienteId(2L),
                TipoCliente.EMPRESA,
                "Acme Corp",
                "9876543210",
                "John Smith",
                "Notes",
                ClienteStatus.INACTIVO,
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            // when
            JpaClienteEntity result = mapper.toJpa(cliente);

            // then
            assertEquals("EMPRESA", result.getTipo());
            assertEquals("INACTIVO", result.getEstado());
        }

        @Test
        @DisplayName("should return null when cliente is null")
        void shouldReturnNullWhenClienteIsNull() {
            // when
            JpaClienteEntity result = mapper.toJpa(null);

            // then
            assertNull(result);
        }

        @Test
        @DisplayName("should handle cliente with null id")
        void shouldHandleClienteWithNullId() {
            // given
            Cliente cliente = Cliente.reconstitute(
                null,
                TipoCliente.PARTICULAR,
                "Test",
                "123",
                null,
                null,
                ClienteStatus.ACTIVO,
                LocalDateTime.now(),
                null
            );

            // when
            JpaClienteEntity result = mapper.toJpa(cliente);

            // then
            assertNotNull(result);
            assertNull(result.getId());
        }

        @Test
        @DisplayName("should handle cliente with null optional fields")
        void shouldHandleClienteWithNullOptionalFields() {
            // given
            Cliente cliente = Cliente.reconstitute(
                new ClienteId(1L),
                TipoCliente.PARTICULAR,
                "Test",
                "123",
                null,
                null,
                ClienteStatus.ACTIVO,
                LocalDateTime.now(),
                null
            );

            // when
            JpaClienteEntity result = mapper.toJpa(cliente);

            // then
            assertNull(result.getPersonaContacto());
            assertNull(result.getObservaciones());
            assertNull(result.getFechaModificacion());
        }
    }

    @Nested
    @DisplayName("Roundtrip")
    class Roundtrip {

        @Test
        @DisplayName("should preserve all fields through roundtrip")
        void shouldPreserveAllFieldsThroughRoundtrip() {
            // given
            Cliente original = Cliente.reconstitute(
                new ClienteId(5L),
                TipoCliente.PARTICULAR,
                "Test Client",
                "111222333",
                "Contact Person",
                "Some notes",
                ClienteStatus.INACTIVO,
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 6, 15, 12, 0)
            );

            // when
            JpaClienteEntity entity = mapper.toJpa(original);
            Cliente restored = mapper.toDomain(entity);

            // then
            assertEquals(original.id(), restored.id());
            assertEquals(original.tipo(), restored.tipo());
            assertEquals(original.nombreOrazonSocial(), restored.nombreOrazonSocial());
            assertEquals(original.telefono(), restored.telefono());
            assertEquals(original.personaContacto(), restored.personaContacto());
            assertEquals(original.observaciones(), restored.observaciones());
            assertEquals(original.estado(), restored.estado());
            assertEquals(original.fechaCreacion(), restored.fechaCreacion());
            assertEquals(original.fechaModificacion(), restored.fechaModificacion());
        }

        @Test
        @DisplayName("should preserve EMPRESA roundtrip correctly")
        void shouldPreserveEmpresaRoundtripCorrectly() {
            // given
            Cliente original = Cliente.reconstitute(
                new ClienteId(10L),
                TipoCliente.EMPRESA,
                "Enterprise Inc",
                "555666777",
                "CEO Name",
                "Corporate notes",
                ClienteStatus.ACTIVO,
                LocalDateTime.now(),
                null
            );

            // when
            JpaClienteEntity entity = mapper.toJpa(original);
            Cliente restored = mapper.toDomain(entity);

            // then
            assertEquals(TipoCliente.EMPRESA, restored.tipo());
            assertEquals(ClienteStatus.ACTIVO, restored.estado());
        }
    }
}
