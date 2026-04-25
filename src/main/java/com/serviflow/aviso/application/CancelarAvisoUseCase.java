package com.serviflow.aviso.application;

import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.exception.AvisoNotFoundException;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.valueobject.AvisoId;
import jakarta.transaction.Transactional;

/**
 * Use case for cancelling an Aviso.
 * Orchestrates domain logic but does not implement business rules.
 */
public class CancelarAvisoUseCase {

    private final AvisoRepository avisoRepository;

    public CancelarAvisoUseCase(AvisoRepository avisoRepository) {
        this.avisoRepository = avisoRepository;
    }

    /**
     * Executes the use case to cancel an aviso.
     *
     * @param avisoId the ID of the aviso to cancel
     * @param usuario the user performing the cancellation
     * @return the cancelled aviso as output
     * @throws AvisoNotFoundException if the aviso does not exist
     * @throws DomainException if the aviso cannot be cancelled (e.g., already completed)
     */
    @Transactional
    public AvisoOutput execute(Long avisoId, String usuario) {
        AvisoId id = new AvisoId(avisoId);
        Aviso aviso = avisoRepository.findById(id)
                .orElseThrow(() -> new AvisoNotFoundException(avisoId));

        // Domain handles validation - cannot cancel completed avisos
        aviso.cancel(usuario);

        Aviso saved = avisoRepository.save(aviso);
        return AvisoOutput.fromDomain(saved);
    }
}