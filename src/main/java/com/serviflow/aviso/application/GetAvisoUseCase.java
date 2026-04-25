package com.serviflow.aviso.application;

import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.exception.AvisoNotFoundException;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.valueobject.AvisoId;
import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.port.ClienteRepository;
import com.serviflow.cliente.domain.valueobject.ClienteId;
import com.serviflow.shared.domain.exception.DomainException;

/**
 * Use case for retrieving a single Aviso by ID.
 * Orchestrates domain logic but does not implement business rules.
 */
public class GetAvisoUseCase {

    private final AvisoRepository avisoRepository;
    private final ClienteRepository clienteRepository;

    public GetAvisoUseCase(AvisoRepository avisoRepository, ClienteRepository clienteRepository) {
        this.avisoRepository = avisoRepository;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Executes the use case to retrieve an aviso by ID.
     *
     * @param avisoId the ID of the aviso to retrieve
     * @param tecnicoId optional technician ID for authorization check
     * @return the aviso as output
     * @throws AvisoNotFoundException if the aviso does not exist
     * @throws DomainException if technician does not have permission
     */
    public AvisoOutput execute(Long avisoId, Long tecnicoId) {
        AvisoId id = new AvisoId(avisoId);
        Aviso aviso = avisoRepository.findById(id)
                .orElseThrow(() -> new AvisoNotFoundException(avisoId));

        // Validate technician access if tecnicoId provided
        if (tecnicoId != null && !tecnicoId.equals(aviso.tecnicoId())) {
            throw new DomainException("You do not have permission to view this aviso");
        }

        String clienteNombre = null;
        String clienteTelefono = null;

        if (aviso.clienteId() != null) {
            var clienteOpt = clienteRepository.findById(new ClienteId(aviso.clienteId()));
            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                clienteNombre = cliente.nombreOrazonSocial();
                clienteTelefono = cliente.telefono();
            }
        }

        return AvisoOutput.fromDomain(aviso, clienteNombre, clienteTelefono);
    }
}