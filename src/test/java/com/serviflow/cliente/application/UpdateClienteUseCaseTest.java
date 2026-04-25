package com.serviflow.cliente.application;

import com.serviflow.cliente.application.exception.DuplicateClienteException;
import com.serviflow.cliente.application.input.UpdateClienteInput;
import com.serviflow.cliente.application.output.ClienteOutput;
import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.exception.ClienteNotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UpdateClienteUseCase.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateClienteUseCase")
class UpdateClienteUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    private UpdateClienteUseCase updateClienteUseCase;

    private Cliente existingCliente;

    @BeforeEach
    void setUp() {
        updateClienteUseCase = new UpdateClienteUseCase(clienteRepository);
        existingCliente = Cliente.reconstitute(
            new ClienteId(1L),
            TipoCliente.PARTICULAR,
            "Juan Pérez",
            "123456789",
            null,
            null,
            ClienteStatus.ACTIVO,
            LocalDateTime.now(),
            null
        );
    }

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("should update cliente when valid input")
        void shouldUpdateClienteWhenValidInput() {
            // given
            UpdateClienteInput input = new UpdateClienteInput(
                1L,
                "Juan Pérez Updated",
                "987654321",
                "María López",
                "Notas importantes"
            );

            when(clienteRepository.findById(new ClienteId(1L))).thenReturn(Optional.of(existingCliente));
            when(clienteRepository.existsByTelefonoAndIdNot("987654321", new ClienteId(1L))).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ClienteOutput result = updateClienteUseCase.execute(input);

            // then
            assertNotNull(result);
            assertEquals("Juan Pérez Updated", result.nombreOrazonSocial());
            assertEquals("987654321", result.telefono());
            assertEquals("María López", result.personaContacto());
            assertEquals("Notas importantes", result.observaciones());

            verify(clienteRepository).findById(new ClienteId(1L));
            verify(clienteRepository).existsByTelefonoAndIdNot("987654321", new ClienteId(1L));
            verify(clienteRepository).save(any(Cliente.class));
        }

        @Test
        @DisplayName("should update cliente with minimal fields")
        void shouldUpdateClienteWithMinimalFields() {
            // given
            UpdateClienteInput input = new UpdateClienteInput(
                1L,
                "Updated Name",
                "999888777",
                null,
                null
            );

            when(clienteRepository.findById(new ClienteId(1L))).thenReturn(Optional.of(existingCliente));
            when(clienteRepository.existsByTelefonoAndIdNot("999888777", new ClienteId(1L))).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ClienteOutput result = updateClienteUseCase.execute(input);

            // then
            assertNotNull(result);
            assertEquals("Updated Name", result.nombreOrazonSocial());
            assertNull(result.personaContacto());
            assertNull(result.observaciones());
        }

        @Test
        @DisplayName("should preserve tipo and status when updating")
        void shouldPreserveTipoAndStatusWhenUpdating() {
            // given
            UpdateClienteInput input = new UpdateClienteInput(
                1L,
                "New Name",
                "555555555",
                null,
                null
            );

            when(clienteRepository.findById(new ClienteId(1L))).thenReturn(Optional.of(existingCliente));
            when(clienteRepository.existsByTelefonoAndIdNot("555555555", new ClienteId(1L))).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ClienteOutput result = updateClienteUseCase.execute(input);

            // then
            assertEquals("PARTICULAR", result.tipo());
            assertEquals("ACTIVO", result.estado());
        }
    }

    @Nested
    @DisplayName("Cliente Not Found")
    class ClienteNotFound {

        @Test
        @DisplayName("should throw ClienteNotFoundException when cliente doesn't exist")
        void shouldThrowWhenClienteNotFound() {
            // given
            UpdateClienteInput input = new UpdateClienteInput(
                999L,
                "Non Existent",
                "123",
                null,
                null
            );

            when(clienteRepository.findById(new ClienteId(999L))).thenReturn(Optional.empty());

            // when/then
            ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> updateClienteUseCase.execute(input)
            );

            assertTrue(exception.getMessage().contains("999"));
            verify(clienteRepository, never()).save(any(Cliente.class));
        }
    }

    @Nested
    @DisplayName("Duplicate Phone")
    class DuplicatePhone {

        @Test
        @DisplayName("should throw DuplicateClienteException when phone already exists")
        void shouldThrowWhenDuplicatePhone() {
            // given
            UpdateClienteInput input = new UpdateClienteInput(
                1L,
                "Name",
                "DUPLICATE",
                null,
                null
            );

            when(clienteRepository.findById(new ClienteId(1L))).thenReturn(Optional.of(existingCliente));
            when(clienteRepository.existsByTelefonoAndIdNot("DUPLICATE", new ClienteId(1L))).thenReturn(true);

            // when/then
            DuplicateClienteException exception = assertThrows(
                DuplicateClienteException.class,
                () -> updateClienteUseCase.execute(input)
            );

            assertTrue(exception.getMessage().contains("DUPLICATE"));
            verify(clienteRepository, never()).save(any(Cliente.class));
        }

        @Test
        @DisplayName("should allow same phone when updating same cliente")
        void shouldAllowSamePhoneWhenUpdatingSameCliente() {
            // given
            UpdateClienteInput input = new UpdateClienteInput(
                1L,
                "Updated",
                "123456789", // same as existing
                null,
                null
            );

            when(clienteRepository.findById(new ClienteId(1L))).thenReturn(Optional.of(existingCliente));
            when(clienteRepository.existsByTelefonoAndIdNot("123456789", new ClienteId(1L))).thenReturn(false);
            when(clienteRepository.save(any(Cliente.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            ClienteOutput result = updateClienteUseCase.execute(input);

            // then
            assertNotNull(result);
            verify(clienteRepository).save(any(Cliente.class));
        }
    }
}
