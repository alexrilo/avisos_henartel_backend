package com.serviflow.cliente.application;

import com.serviflow.cliente.application.exception.DuplicateClienteException;
import com.serviflow.cliente.application.input.CreateClienteInput;
import com.serviflow.cliente.application.output.ClienteOutput;
import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.port.ClienteRepository;
import com.serviflow.cliente.domain.valueobject.TipoCliente;
import jakarta.transaction.Transactional;

/**
 * Use case for creating a new Cliente.
 * Orchestrates the creation flow by validating uniqueness and delegating to domain.
 */
public class CreateClienteUseCase {

    private final ClienteRepository clienteRepository;

    public CreateClienteUseCase(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Executes the createCliente use case.
     * 
     * @param input the create input containing cliente data
     * @return the created Cliente as output
     * @throws DuplicateClienteException if phone number already exists
     */
    @Transactional
    public ClienteOutput execute(CreateClienteInput input) {
        if (clienteRepository.existsByTelefono(input.telefono())) {
            throw new DuplicateClienteException("teléfono", input.telefono());
        }

        TipoCliente tipo = TipoCliente.from(input.tipo());
        
        Cliente cliente = Cliente.create(
            tipo,
            input.nombreOrazonSocial(),
            input.telefono(),
            input.personaContacto(),
            input.observaciones()
        );

        Cliente saved = clienteRepository.save(cliente);
        return ClienteOutput.fromDomain(saved);
    }
}
