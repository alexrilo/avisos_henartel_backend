package com.serviflow.aviso.application;

import com.serviflow.aviso.application.exception.ClienteNotFoundException;
import com.serviflow.aviso.application.input.CreateAvisoInput;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.port.CorrelativoRepository;
import com.serviflow.aviso.domain.valueobject.DireccionServicio;
import com.serviflow.aviso.domain.valueobject.NumeroCorrelativo;
import com.serviflow.aviso.domain.valueobject.Prioridad;
import com.serviflow.cliente.domain.port.ClienteRepository;
import com.serviflow.cliente.domain.valueobject.ClienteId;
import jakarta.transaction.Transactional;

import java.time.Year;

/**
 * Use case for creating a new Aviso.
 * Orchestrates domain logic but does not implement business rules.
 */
public class CreateAvisoUseCase {

    private final AvisoRepository avisoRepository;
    private final ClienteRepository clienteRepository;
    private final CorrelativoRepository correlativoRepository;

    public CreateAvisoUseCase(
            AvisoRepository avisoRepository,
            ClienteRepository clienteRepository,
            CorrelativoRepository correlativoRepository) {
        this.avisoRepository = avisoRepository;
        this.clienteRepository = clienteRepository;
        this.correlativoRepository = correlativoRepository;
    }

    /**
     * Executes the use case to create a new Aviso.
     *
     * @param input the input data for creating the aviso
     * @return the created aviso as output
     * @throws ClienteNotFoundException if the cliente does not exist
     */
    @Transactional
    public AvisoOutput execute(CreateAvisoInput input) {
        // Validate cliente exists
        if (!clienteRepository.existsById(new ClienteId(input.clienteId()))) {
            throw new ClienteNotFoundException(input.clienteId());
        }

        // Generate correlative number
        int year = Year.now().getValue();
        int sequence = correlativoRepository.getNextSequence(year);
        NumeroCorrelativo correlativo = NumeroCorrelativo.generate(year, sequence);

        // Create address value object
        DireccionServicio direccion = new DireccionServicio(
            input.calle(),
            input.numero(),
            input.localidad(),
            input.provincia(),
            input.codigoPostal()
        );

        // Create the aviso using domain factory - business logic stays in entity
        Prioridad prioridad = Prioridad.valueOf(input.prioridad());
        Aviso aviso = Aviso.create(
            input.clienteId(),
            correlativo,
            input.descripcion(),
            prioridad,
            direccion,
            input.fechaProgramada(),
            input.materialesUsados()
        );

        // Note: Initial observations are added after the aviso is saved and gets an ID from the database.
        // This is a limitation of the current domain design where Observacion requires a non-null AvisoId.

        // Persist and return output
        Aviso saved = avisoRepository.save(aviso);
        return AvisoOutput.fromDomain(saved);
    }
}
