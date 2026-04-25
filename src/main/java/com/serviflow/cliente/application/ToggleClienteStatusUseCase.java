package com.serviflow.cliente.application;

import com.serviflow.cliente.application.output.ClienteOutput;
import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.exception.ClienteNotFoundException;
import com.serviflow.cliente.domain.port.ClienteRepository;
import com.serviflow.cliente.domain.valueobject.ClienteId;
import jakarta.transaction.Transactional;

/**
 * Use case for toggling a Cliente's status between ACTIVO and INACTIVO.
 */
public class ToggleClienteStatusUseCase {

    private final ClienteRepository clienteRepository;

    public ToggleClienteStatusUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Executes the toggleClienteStatus use case.
     * 
     * @param clienteId the cliente ID
     * @return the Cliente with toggled status
     * @throws ClienteNotFoundException if cliente doesn't exist
     */
    @Transactional
    public ClienteOutput execute(Long clienteId) {
        ClienteId id = new ClienteId(clienteId);
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ClienteNotFoundException(clienteId));

        Cliente toggled = cliente.toggleStatus();
        Cliente saved = clienteRepository.save(toggled);

        return ClienteOutput.fromDomain(saved);
    }
}
