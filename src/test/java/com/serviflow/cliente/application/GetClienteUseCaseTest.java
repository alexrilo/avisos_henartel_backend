package com.serviflow.cliente.application;

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
import static org.mockito.Mockito.*;

/**
 * Unit tests for GetClienteUseCase.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GetClienteUseCase")
class GetClienteUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    private GetClienteUseCase getClienteUseCase;

    @BeforeEach
    void setUp() {
        getClienteUseCase = new GetClienteUseCase(clienteRepository);
    }

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("should return cliente by id")
        void shouldReturnClienteById() {
            // given
            Long clienteId = 1L;
            Cliente cliente = Cliente.reconstitute(
                new ClienteId(clienteId),
                TipoCliente.PARTICULAR,
                "Juan Pérez",
                "1234567890",
                "María López",
                "Notas importantes",
                ClienteStatus.ACTIVO,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 15, 14, 30)
            );

            when(clienteRepository.findById(new ClienteId(clienteId)))
                .thenReturn(Optional.of(cliente));

            // when
            ClienteOutput result = getClienteUseCase.execute(clienteId);

            // then
            assertNotNull(result);
            assertEquals(clienteId, result.id());
            assertEquals("PARTICULAR", result.tipo());
            assertEquals("Juan Pérez", result.nombreOrazonSocial());
            assertEquals("1234567890", result.telefono());
            assertEquals("María López", result.personaContacto());
            assertEquals("Notas importantes", result.observaciones());
            assertEquals("ACTIVO", result.estado());
            assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), result.fechaCreacion());
            assertEquals(LocalDateTime.of(2024, 1, 15, 14, 30), result.fechaModificacion());

            verify(clienteRepository).findById(new ClienteId(clienteId));
        }

        @Test
        @DisplayName("should return cliente with null optional fields")
        void shouldReturnClienteWithNullOptionalFields() {
            // given
            Long clienteId = 1L;
            Cliente cliente = Cliente.reconstitute(
                new ClienteId(clienteId),
                TipoCliente.EMPRESA,
                "Acme Corporation",
                "9876543210",
                null,
                null,
                ClienteStatus.INACTIVO,
                LocalDateTime.now(),
                null
            );

            when(clienteRepository.findById(new ClienteId(clienteId)))
                .thenReturn(Optional.of(cliente));

            // when
            ClienteOutput result = getClienteUseCase.execute(clienteId);

            // then
            assertNotNull(result);
            assertEquals("EMPRESA", result.tipo());
            assertEquals("Acme Corporation", result.nombreOrazonSocial());
            assertNull(result.personaContacto());
            assertNull(result.observaciones());
            assertEquals("INACTIVO", result.estado());
            assertNull(result.fechaModificacion());
        }
    }

    @Nested
    @DisplayName("Not Found")
    class NotFound {

        @Test
        @DisplayName("should throw ClienteNotFoundException when cliente not found")
        void shouldThrowClienteNotFoundExceptionWhenClienteNotFound() {
            // given
            Long clienteId = 999L;
            when(clienteRepository.findById(new ClienteId(clienteId)))
                .thenReturn(Optional.empty());

            // when/then
            ClienteNotFoundException exception = assertThrows(
                ClienteNotFoundException.class,
                () -> getClienteUseCase.execute(clienteId)
            );

            assertTrue(exception.getMessage().contains("999"));

            verify(clienteRepository).findById(new ClienteId(clienteId));
        }
    }
}
