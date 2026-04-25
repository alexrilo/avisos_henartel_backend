package com.serviflow.aviso.application;

import com.serviflow.aviso.application.input.ChangeEstadoInput;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.exception.AvisoNotFoundException;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.valueobject.AvisoId;
import com.serviflow.aviso.domain.valueobject.EstadoAviso;
import jakarta.transaction.Transactional;

/**
 * Use case for changing the estado of an Aviso.
 * Orchestrates domain logic but does not implement business rules.
 */
public class ChangeEstadoUseCase {

    private final AvisoRepository avisoRepository;

    public ChangeEstadoUseCase(AvisoRepository avisoRepository) {
        this.avisoRepository = avisoRepository;
    }

    /**
     * Executes the use case to change the aviso estado.
     *
     * @param input the input data for changing the estado
     * @return the updated aviso as output
     * @throws AvisoNotFoundException if the aviso does not exist
     * @throws IllegalArgumentException if the state transition is invalid
     */
    @Transactional
    public AvisoOutput execute(ChangeEstadoInput input) {
        AvisoId id = new AvisoId(input.avisoId());
        Aviso aviso = avisoRepository.findById(id)
                .orElseThrow(() -> new AvisoNotFoundException(input.avisoId()));

        EstadoAviso target = EstadoAviso.valueOf(input.estado());
        
        // Domain handles state transition validation based on current state
        switch (target) {
            case ASIGNADO -> {
                if (input.tecnicoId() != null) {
                    aviso.assignTecnico(input.tecnicoId(), input.usuario());
                }
            }
            case EN_CURSO -> aviso.startWork(input.usuario());
            case COMPLETADO -> aviso.completeWork(input.usuario());
            case PENDIENTE_SEGUIMIENTO -> aviso.pendingFollowUp(input.usuario());
            case CANCELADO -> aviso.cancel(input.usuario());
            default -> throw new IllegalArgumentException("Cannot transition to " + target + " directly");
        }

        // Add manual observation if provided and not blank
        if (input.observacion() != null && !input.observacion().isBlank()) {
            aviso.addObservacion(input.observacion(), "MANUAL", input.usuario());
        }

        Aviso saved = avisoRepository.save(aviso);
        return AvisoOutput.fromDomain(saved);
    }
}