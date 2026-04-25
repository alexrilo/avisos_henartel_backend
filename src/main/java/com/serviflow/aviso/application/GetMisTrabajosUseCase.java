package com.serviflow.aviso.application;

import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.port.ClienteRepository;
import com.serviflow.cliente.domain.valueobject.ClienteId;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Use case for retrieving all avisos assigned to a specific technician.
 * Used for the "My Work" feature in the technician's dashboard.
 */
public class GetMisTrabajosUseCase {

    private final AvisoRepository avisoRepository;
    private final ClienteRepository clienteRepository;

    public GetMisTrabajosUseCase(AvisoRepository avisoRepository, ClienteRepository clienteRepository) {
        this.avisoRepository = avisoRepository;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Executes the use case to retrieve all avisos for a technician.
     *
     * @param tecnicoId the ID of the technician
     * @return list of avisos assigned to the technician
     */
    public List<AvisoOutput> execute(Long tecnicoId) {
        List<Aviso> avisos = avisoRepository.findByTecnicoId(tecnicoId, null);

        // Collect unique cliente IDs for batch lookup
        List<Long> clienteIds = avisos.stream()
            .map(Aviso::clienteId)
            .distinct()
            .toList();

        Map<Long, Cliente> clientesMap = clienteIds.stream()
            .map(id -> clienteRepository.findById(new ClienteId(id)))
            .filter(java.util.Optional::isPresent)
            .map(java.util.Optional::get)
            .collect(Collectors.toMap(c -> c.id().value(), c -> c));

        return avisos.stream().map(aviso -> {
            Cliente cliente = clientesMap.get(aviso.clienteId());
            String clienteNombre = cliente != null ? cliente.nombreOrazonSocial() : null;
            String clienteTelefono = cliente != null ? cliente.telefono() : null;
            return AvisoOutput.fromDomain(aviso, clienteNombre, clienteTelefono);
        }).toList();
    }
}
