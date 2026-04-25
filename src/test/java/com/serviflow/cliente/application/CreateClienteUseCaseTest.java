package com.serviflow.cliente.application;

import com.serviflow.cliente.application.exception.DuplicateClienteException;
import com.serviflow.cliente.application.input.CreateClienteInput;
import com.serviflow.cliente.application.output.ClienteOutput;
import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.port.ClienteRepository;
import com.serviflow.cliente.domain.valueobject.ClienteId;
import com.serviflow.cliente.domain.valueobject.ClienteStatus;
import com.serviflow.cliente.domain.valueobject.TipoCliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CreateClienteUseCase.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CreateClienteUseCase")
class CreateClienteUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    private CreateClienteUseCase createClienteUseCase;

    @BeforeEach
    void setUp() {
        createClienteUseCase = new CreateClienteUseCase(clienteRepository);
    }

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("should create cliente with tipo PARTICULAR")
        void shouldCreateClienteWithTipoParticular() {
            // given
            CreateClienteInput input = new CreateClienteInput(
                "PARTICULAR",
                "Juan Pérez",
                "1234567890",
                "María López",
                "Notas importantes"
            );

            Cliente savedCliente = Cliente.reconstitute(
                new ClienteId(1L),
                TipoCliente.PARTICULAR,
                "Juan Pérez",
                "1234567890",
                "María López",
                "Notas importantes",
                ClienteStatus.ACTIVO,
                LocalDateTime.now(),
                null
            );

            when(clienteRepository.existsByTelefono("1234567890")).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenReturn(savedCliente);

            // when
            ClienteOutput result = createClienteUseCase.execute(input);

            // then
            assertNotNull(result);
            assertEquals("PARTICULAR", result.tipo());
            assertEquals("Juan Pérez", result.nombreOrazonSocial());
            assertEquals("1234567890", result.telefono());
            assertEquals("María López", result.personaContacto());
            assertEquals("Notas importantes", result.observaciones());
            assertEquals("ACTIVO", result.estado());

            verify(clienteRepository).existsByTelefono("1234567890");
            verify(clienteRepository).save(any(Cliente.class));
        }

        @Test
        @DisplayName("should create cliente with tipo EMPRESA")
        void shouldCreateClienteWithTipoEmpresa() {
            // given
            CreateClienteInput input = new CreateClienteInput(
                "EMPRESA",
                "Acme Corporation",
                "9876543210",
                "John Smith",
                "Important notes"
            );

            Cliente savedCliente = Cliente.reconstitute(
                new ClienteId(1L),
                TipoCliente.EMPRESA,
                "Acme Corporation",
                "9876543210",
                "John Smith",
                "Important notes",
                ClienteStatus.ACTIVO,
                LocalDateTime.now(),
                null
            );

            when(clienteRepository.existsByTelefono("9876543210")).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenReturn(savedCliente);

            // when
            ClienteOutput result = createClienteUseCase.execute(input);

            // then
            assertNotNull(result);
            assertEquals("EMPRESA", result.tipo());
            assertEquals("Acme Corporation", result.nombreOrazonSocial());
            assertEquals("9876543210", result.telefono());
            assertEquals("John Smith", result.personaContacto());
            assertEquals("Important notes", result.observaciones());
            assertEquals("ACTIVO", result.estado());

            verify(clienteRepository).existsByTelefono("9876543210");
            verify(clienteRepository).save(any(Cliente.class));
        }

        @Test
        @DisplayName("should create cliente with minimal fields")
        void shouldCreateClienteWithMinimalFields() {
            // given
            CreateClienteInput input = new CreateClienteInput(
                "PARTICULAR",
                "Test Client",
                "111222333",
                null,
                null
            );

            Cliente savedCliente = Cliente.reconstitute(
                new ClienteId(1L),
                TipoCliente.PARTICULAR,
                "Test Client",
                "111222333",
                null,
                null,
                ClienteStatus.ACTIVO,
                LocalDateTime.now(),
                null
            );

            when(clienteRepository.existsByTelefono("111222333")).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenReturn(savedCliente);

            // when
            ClienteOutput result = createClienteUseCase.execute(input);

            // then
            assertNotNull(result);
            assertNull(result.personaContacto());
            assertNull(result.observaciones());

            verify(clienteRepository).save(any(Cliente.class));
        }
    }

    @Nested
    @DisplayName("Duplicate Phone")
    class DuplicatePhone {

        @Test
        @DisplayName("should throw DuplicateClienteException when phone already exists")
        void shouldThrowDuplicateClienteExceptionWhenPhoneExists() {
            // given
            CreateClienteInput input = new CreateClienteInput(
                "PARTICULAR",
                "Juan Pérez",
                "1234567890",
                null,
                null
            );

            when(clienteRepository.existsByTelefono("1234567890")).thenReturn(true);

            // when/then
            DuplicateClienteException exception = assertThrows(
                DuplicateClienteException.class,
                () -> createClienteUseCase.execute(input)
            );

            assertTrue(exception.getMessage().contains("1234567890"));

            verify(clienteRepository).existsByTelefono("1234567890");
            verify(clienteRepository, never()).save(any(Cliente.class));
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("should throw NullPointerException when nombre is null")
        void shouldThrowNullPointerExceptionWhenNombreIsNull() {
            // given
            CreateClienteInput input = new CreateClienteInput(
                "PARTICULAR",
                null,
                "1234567890",
                null,
                null
            );

            when(clienteRepository.existsByTelefono("1234567890")).thenReturn(false);

            // when/then
            assertThrows(NullPointerException.class, () ->
                createClienteUseCase.execute(input)
            );
        }

        @Test
        @DisplayName("should throw IllegalArgumentException when nombre is blank")
        void shouldThrowIllegalArgumentExceptionWhenNombreIsBlank() {
            // given
            CreateClienteInput input = new CreateClienteInput(
                "PARTICULAR",
                "   ",
                "1234567890",
                null,
                null
            );

            when(clienteRepository.existsByTelefono("1234567890")).thenReturn(false);

            // when/then
            assertThrows(IllegalArgumentException.class, () ->
                createClienteUseCase.execute(input)
            );
        }
    }
}
