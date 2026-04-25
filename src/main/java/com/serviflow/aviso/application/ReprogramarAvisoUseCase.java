package com.serviflow.aviso.application;

import com.serviflow.aviso.application.input.ReprogramarInput;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.exception.AvisoNotFoundException;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.valueobject.AvisoId;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

/**
 * Use case for rescheduling an Aviso.
 * Orchestrates domain logic but does not implement business rules.
 */
public class ReprogramarAvisoUseCase {

    private final AvisoRepository avisoRepository;

    public ReprogramarAvisoUseCase(AvisoRepository avisoRepository) {
        this.avisoRepository = avisoRepository;
    }

    /**
     * Executes the use case to reschedule an aviso.
     *
     * @param input the input data for rescheduling
     * @return the updated aviso as output
     * @throws AvisoNotFoundException if the aviso does not exist
     */
    @Transactional
    public AvisoOutput execute(ReprogramarInput input) {
        AvisoId id = new AvisoId(input.avisoId());
        Aviso aviso = avisoRepository.findById(id)
                .orElseThrow(() -> new AvisoNotFoundException(input.avisoId()));

        // Domain handles validation - cannot reprogram terminal states
        LocalDateTime nuevaFecha = input.nuevaFecha() != null 
            ? input.nuevaFecha() 
            : aviso.fechaProgramada();
        Long nuevoTecnicoId = input.nuevoTecnicoId();

        aviso.reprogramar(nuevaFecha, nuevoTecnicoId, input.usuario());

        Aviso saved = avisoRepository.save(aviso);
        return AvisoOutput.fromDomain(saved);
    }
}