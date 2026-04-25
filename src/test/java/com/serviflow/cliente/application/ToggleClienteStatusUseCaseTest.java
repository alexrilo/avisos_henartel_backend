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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ToggleClienteStatusUseCase.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ToggleClienteStatusUseCase")
class ToggleClienteStatusUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ToggleClienteStatusUseCase toggleClienteStatusUseCase;

    @BeforeEach
    void setUp() {
        toggleClienteStatusUseCase = new ToggleClienteStatusUseCase(clienteRepository);
    }

    @Nested
    @DisplayName("Toggle ACTIVO to INACTIVO")
    class ToggleActivoToInactivo {

        @Test
        @DisplayName("should toggle ACTIVO to INACTIVO")
        void shouldToggleActivoToInactivo() {
            // given
            Long clienteId = 1L;
            Cliente existingCliente = Cliente.reconstitute(
                new ClienteId(clienteId),
                TipoCliente.PARTICULAR,
                "Juan Pérez",
                "1234567890",
                null,
                null,
                ClienteStatus.ACTIVO,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                null
            );

            Cliente toggledCliente = Cliente.reconstitute(
                new ClienteId(clienteId),
                TipoCliente.PARTICULAR,
                "Juan Pérez",
                "1234567890",
                null,
                null,
                ClienteStatus.INACTIVO,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.now()
            );

            when(clienteRepository.findById(new ClienteId(clienteId)))
                .thenReturn(Optional.of(existingCliente));
            when(clienteRepository.save(any(Cliente.class))).thenReturn(toggledCliente);

            // when
            ClienteOutput result = toggleClienteStatusUseCase.execute(clienteId);

            // then
            assertNotNull(result);
            assertEquals(clienteId, result.id());
            assertEquals("INACTIVO", result.estado());

            verify(clienteRepository).findById(new ClienteId(clienteId));
            verify(clienteRepository).save(any(Cliente.class));
        }
    }

    @Nested
    @DisplayName("Toggle INACTIVO to ACTIVO")
    class ToggleInactivoToActivo {

        @Test
        @DisplayName("should toggle INACTIVO to ACTIVO")
        void shouldToggleInactivoToActivo() {
            // given
            Long clienteId = 1L;
            Cliente existingCliente = Cliente.reconstitute(
                new ClienteId(clienteId),
                TipoCliente.EMPRESA,
                "Acme Corporation",
                "9876543210",
                "John Smith",
                "Important notes",
                ClienteStatus.INACTIVO,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.of(2024, 1, 15, 14, 30)
            );

            Cliente toggledCliente = Cliente.reconstitute(
                new ClienteId(clienteId),
                TipoCliente.EMPRESA,
                "Acme Corporation",
                "9876543210",
                "John Smith",
                "Important notes",
                ClienteStatus.ACTIVO,
                LocalDateTime.of(2024, 1, 1, 10, 0),
                LocalDateTime.now()
            );

            when(clienteRepository.findById(new ClienteId(clienteId)))
                .thenReturn(Optional.of(existingCliente));
            when(clienteRepository.save(any(Cliente.class))).thenReturn(toggledCliente);

            // when
            ClienteOutput result = toggleClienteStatusUseCase.execute(clienteId);

            // then
            assertNotNull(result);
            assertEquals(clienteId, result.id());
            assertEquals("ACTIVO", result.estado());
            assertEquals("Acme Corporation", result.nombreOrazonSocial());

            verify(clienteRepository).findById(new ClienteId(clienteId));
            verify(clienteRepository).save(any(Cliente.class));
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
                () -> toggleClienteStatusUseCase.execute(clienteId)
            );

            assertTrue(exception.getMessage().contains("999"));

            verify(clienteRepository).findById(new ClienteId(clienteId));
            verify(clienteRepository, never()).save(any(Cliente.class));
        }
    }
}
