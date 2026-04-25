package com.serviflow.cliente.application;

import com.serviflow.cliente.application.exception.DuplicateClienteException;
import com.serviflow.cliente.application.input.UpdateClienteInput;
import com.serviflow.cliente.application.output.ClienteOutput;
import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.exception.ClienteNotFoundException;
import com.serviflow.cliente.domain.port.ClienteRepository;
import com.serviflow.cliente.domain.valueobject.ClienteId;
import jakarta.transaction.Transactional;

/**
 * Use case for updating an existing Cliente.
 * Orchestrates the update flow by validating existence, uniqueness and delegating to domain.
 */
public class UpdateClienteUseCase {

    private final ClienteRepository clienteRepository;

    public UpdateClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Executes the updateCliente use case.
     * 
     * @param input the update input containing cliente data
     * @return the updated Cliente as output
     * @throws ClienteNotFoundException if cliente doesn't exist
     * @throws DuplicateClienteException if phone number is already used by another cliente
     */
    @Transactional
    public ClienteOutput execute(UpdateClienteInput input) {
        ClienteId id = new ClienteId(input.id());
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ClienteNotFoundException(input.id()));

        if (clienteRepository.existsByTelefonoAndIdNot(input.telefono(), id)) {
            throw new DuplicateClienteException("teléfono", input.telefono());
        }

        Cliente updated = cliente.actualizarDatos(
            input.nombreOrazonSocial(),
            input.telefono(),
            input.personaContacto(),
            input.observaciones()
        );

        Cliente saved = clienteRepository.save(updated);
        return ClienteOutput.fromDomain(saved);
    }
}
