package com.serviflow.aviso.application;

import com.serviflow.aviso.application.input.AssignTecnicoInput;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.exception.AvisoNotFoundException;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.valueobject.AvisoId;
import jakarta.transaction.Transactional;

/**
 * Use case for assigning a technician to an Aviso.
 * Orchestrates domain logic but does not implement business rules.
 */
public class AssignTecnicoUseCase {

    private final AvisoRepository avisoRepository;

    public AssignTecnicoUseCase(AvisoRepository avisoRepository) {
        this.avisoRepository = avisoRepository;
    }

    /**
     * Executes the use case to assign a technician.
     *
     * @param input the input data for assigning the technician
     * @return the updated aviso as output
     * @throws AvisoNotFoundException if the aviso does not exist
     */
    @Transactional
    public AvisoOutput execute(AssignTecnicoInput input) {
        AvisoId id = new AvisoId(input.avisoId());
        Aviso aviso = avisoRepository.findById(id)
                .orElseThrow(() -> new AvisoNotFoundException(input.avisoId()));

        // Domain handles state transition validation
        aviso.assignTecnico(input.tecnicoId(), input.usuario());

        Aviso saved = avisoRepository.save(aviso);
        return AvisoOutput.fromDomain(saved);
    }
}