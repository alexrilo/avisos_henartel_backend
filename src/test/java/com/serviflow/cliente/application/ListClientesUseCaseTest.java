package com.serviflow.cliente.application;

import com.serviflow.cliente.application.input.ListClientesInput;
import com.serviflow.cliente.application.output.ClienteOutput;
import com.serviflow.cliente.application.output.PaginatedResponse;
import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.port.ClienteRepository;
import com.serviflow.cliente.domain.port.ClienteSearchCriteria;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ListClientesUseCase.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ListClientesUseCase")
class ListClientesUseCaseTest {

    @Mock
    private ClienteRepository clienteRepository;

    private ListClientesUseCase listClientesUseCase;

    private List<Cliente> clientes;

    @BeforeEach
    void setUp() {
        listClientesUseCase = new ListClientesUseCase(clienteRepository);

        clientes = List.of(
            Cliente.reconstitute(
                new ClienteId(1L),
                TipoCliente.PARTICULAR,
                "Juan Pérez",
                "111222333",
                "María López",
                "Notas 1",
                ClienteStatus.ACTIVO,
                LocalDateTime.now(),
                null
            ),
            Cliente.reconstitute(
                new ClienteId(2L),
                TipoCliente.EMPRESA,
                "Empresa SA",
                "444555666",
                "John Smith",
                "Notas 2",
                ClienteStatus.ACTIVO,
                LocalDateTime.now(),
                null
            ),
            Cliente.reconstitute(
                new ClienteId(3L),
                TipoCliente.PARTICULAR,
                "Pedro Gómez",
                "777888999",
                null,
                null,
                ClienteStatus.INACTIVO,
                LocalDateTime.now(),
                LocalDateTime.now()
            )
        );
    }

    @Nested
    @DisplayName("Happy Path")
    class HappyPath {

        @Test
        @DisplayName("should return paginated clientes with default parameters")
        void shouldReturnPaginatedClientesWithDefaultParameters() {
            // given
            ListClientesInput input = new ListClientesInput(null, null, null, 0, 20, "nombreOrazonSocial", "ASC");

            when(clienteRepository.findAll(any(ClienteSearchCriteria.class))).thenReturn(clientes);
            when(clienteRepository.count(any(ClienteSearchCriteria.class))).thenReturn(3L);

            // when
            PaginatedResponse<ClienteOutput> result = listClientesUseCase.execute(input);

            // then
            assertNotNull(result);
            assertEquals(3, result.totalElements());
            assertEquals(1, result.totalPages());
            assertEquals(0, result.currentPage());
            assertEquals(20, result.pageSize());
            assertEquals(3, result.content().size());
        }

        @Test
        @DisplayName("should map cliente fields correctly")
        void shouldMapClienteFieldsCorrectly() {
            // given
            ListClientesInput input = new ListClientesInput(null, null, null, 0, 20, null, null);

            when(clienteRepository.findAll(any(ClienteSearchCriteria.class))).thenReturn(List.of(clientes.get(0)));
            when(clienteRepository.count(any(ClienteSearchCriteria.class))).thenReturn(1L);

            // when
            PaginatedResponse<ClienteOutput> result = listClientesUseCase.execute(input);

            // then
            ClienteOutput first = result.content().get(0);
            assertEquals(1L, first.id());
            assertEquals("PARTICULAR", first.tipo());
            assertEquals("Juan Pérez", first.nombreOrazonSocial());
            assertEquals("111222333", first.telefono());
            assertEquals("ACTIVO", first.estado());
        }

        @Test
        @DisplayName("should handle pagination correctly")
        void shouldHandlePaginationCorrectly() {
            // given - simulate 50 elements with page size 20
            ListClientesInput input = new ListClientesInput(null, null, null, 2, 20, "nombreOrazonSocial", "ASC");

            when(clienteRepository.findAll(any(ClienteSearchCriteria.class))).thenReturn(List.of());
            when(clienteRepository.count(any(ClienteSearchCriteria.class))).thenReturn(50L);

            // when
            PaginatedResponse<ClienteOutput> result = listClientesUseCase.execute(input);

            // then
            assertEquals(3, result.totalPages());
            assertEquals(2, result.currentPage());
        }
    }

    @Nested
    @DisplayName("Empty Results")
    class EmptyResults {

        @Test
        @DisplayName("should return empty when no clientes exist")
        void shouldReturnEmptyWhenNoClientes() {
            // given
            ListClientesInput input = new ListClientesInput(null, null, null, 0, 10, null, null);

            when(clienteRepository.findAll(any(ClienteSearchCriteria.class))).thenReturn(List.of());
            when(clienteRepository.count(any(ClienteSearchCriteria.class))).thenReturn(0L);

            // when
            PaginatedResponse<ClienteOutput> result = listClientesUseCase.execute(input);

            // then
            assertNotNull(result);
            assertTrue(result.content().isEmpty());
            assertEquals(0, result.totalElements());
            assertEquals(0, result.totalPages());
            assertEquals(0, result.currentPage());
        }

        @Test
        @DisplayName("should return empty when page is beyond total pages")
        void shouldReturnEmptyWhenPageBeyondTotal() {
            // given
            ListClientesInput input = new ListClientesInput(null, null, null, 10, 20, null, null);

            when(clienteRepository.findAll(any(ClienteSearchCriteria.class))).thenReturn(List.of());
            when(clienteRepository.count(any(ClienteSearchCriteria.class))).thenReturn(5L);

            // when
            PaginatedResponse<ClienteOutput> result = listClientesUseCase.execute(input);

            // then
            assertTrue(result.content().isEmpty());
            assertEquals(1, result.totalPages());
        }
    }

    @Nested
    @DisplayName("Filtering")
    class Filtering {

        @Test
        @DisplayName("should pass search term to criteria")
        void shouldPassSearchTermToCriteria() {
            // given
            ListClientesInput input = new ListClientesInput("Juan", null, null, 0, 20, null, null);

            when(clienteRepository.findAll(any(ClienteSearchCriteria.class))).thenReturn(List.of(clientes.get(0)));
            when(clienteRepository.count(any(ClienteSearchCriteria.class))).thenReturn(1L);

            // when
            PaginatedResponse<ClienteOutput> result = listClientesUseCase.execute(input);

            // then
            assertEquals(1, result.content().size());
            verify(clienteRepository).findAll(any(ClienteSearchCriteria.class));
        }

        @Test
        @DisplayName("should pass status filter to criteria")
        void shouldPassStatusFilterToCriteria() {
            // given
            ListClientesInput input = new ListClientesInput(null, "INACTIVO", null, 0, 20, null, null);

            when(clienteRepository.findAll(any(ClienteSearchCriteria.class))).thenReturn(List.of(clientes.get(2)));
            when(clienteRepository.count(any(ClienteSearchCriteria.class))).thenReturn(1L);

            // when
            PaginatedResponse<ClienteOutput> result = listClientesUseCase.execute(input);

            // then
            assertEquals(1, result.content().size());
            assertEquals("INACTIVO", result.content().get(0).estado());
        }

        @Test
        @DisplayName("should pass tipo filter to criteria")
        void shouldPassTipoFilterToCriteria() {
            // given
            ListClientesInput input = new ListClientesInput(null, null, "EMPRESA", 0, 20, null, null);

            when(clienteRepository.findAll(any(ClienteSearchCriteria.class))).thenReturn(List.of(clientes.get(1)));
            when(clienteRepository.count(any(ClienteSearchCriteria.class))).thenReturn(1L);

            // when
            PaginatedResponse<ClienteOutput> result = listClientesUseCase.execute(input);

            // then
            assertEquals(1, result.content().size());
            assertEquals("EMPRESA", result.content().get(0).tipo());
        }
    }

    @Nested
    @DisplayName("Sorting")
    class Sorting {

        @Test
        @DisplayName("should pass sort parameters to criteria")
        void shouldPassSortParametersToCriteria() {
            // given
            ListClientesInput input = new ListClientesInput(null, null, null, 0, 20, "telefono", "DESC");

            when(clienteRepository.findAll(any(ClienteSearchCriteria.class))).thenReturn(clientes);
            when(clienteRepository.count(any(ClienteSearchCriteria.class))).thenReturn(3L);

            // when
            PaginatedResponse<ClienteOutput> result = listClientesUseCase.execute(input);

            // then
            assertEquals(3, result.content().size());
            verify(clienteRepository).findAll(any(ClienteSearchCriteria.class));
        }
    }
}
