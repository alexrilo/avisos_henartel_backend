package com.serviflow.cliente.application;

import com.serviflow.cliente.application.input.ListClientesInput;
import com.serviflow.cliente.application.output.ClienteOutput;
import com.serviflow.cliente.application.output.PaginatedResponse;
import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.port.ClienteRepository;
import com.serviflow.cliente.domain.port.ClienteSearchCriteria;
import com.serviflow.cliente.domain.valueobject.ClienteStatus;
import com.serviflow.cliente.domain.valueobject.TipoCliente;

import java.util.List;

/**
 * Use case for listing clientes with filtering, pagination and sorting.
 */
public class ListClientesUseCase {

    private final ClienteRepository clienteRepository;

    public ListClientesUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Executes the listClientes use case.
     * 
     * @param input the list input containing search criteria
     * @return paginated list of clientes
     */
    public PaginatedResponse<ClienteOutput> execute(ListClientesInput input) {
        ClienteSearchCriteria criteria = ClienteSearchCriteria.builder()
            .searchTerm(input.searchTerm())
            .estado(input.estado() != null ? ClienteStatus.from(input.estado()) : null)
            .tipo(input.tipo() != null ? TipoCliente.from(input.tipo()) : null)
            .page(input.page())
            .size(input.size())
            .sortBy(input.sortBy())
            .sortAsc("ASC".equalsIgnoreCase(input.sortDir()))
            .build();

        List<Cliente> content = clienteRepository.findAll(criteria);
        long totalElements = clienteRepository.count(criteria);
        int totalPages = (int) Math.ceil((double) totalElements / input.size());

        List<ClienteOutput> output = content.stream()
            .map(ClienteOutput::fromDomain)
            .toList();

        return new PaginatedResponse<>(output, totalElements, totalPages, input.page(), input.size());
    }
}
