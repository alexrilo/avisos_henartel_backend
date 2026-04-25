package com.serviflow.cliente.application;

import com.serviflow.cliente.application.output.ClienteOutput;
import com.serviflow.cliente.domain.exception.ClienteNotFoundException;
import com.serviflow.cliente.domain.port.ClienteRepository;
import com.serviflow.cliente.domain.valueobject.ClienteId;

/**
 * Use case for retrieving a Cliente by ID.
 */
public class GetClienteUseCase {

    private final ClienteRepository clienteRepository;

    public GetClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Executes the getCliente use case.
     * 
     * @param clienteId the cliente ID
     * @return the Cliente as output
     * @throws ClienteNotFoundException if cliente doesn't exist
     */
    public ClienteOutput execute(Long clienteId) {
        ClienteId id = new ClienteId(clienteId);
        return clienteRepository.findById(id)
            .map(ClienteOutput::fromDomain)
            .orElseThrow(() -> new ClienteNotFoundException(clienteId));
    }
}
