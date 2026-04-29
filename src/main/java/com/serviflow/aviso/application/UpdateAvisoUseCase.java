package com.serviflow.aviso.application;

import com.serviflow.aviso.application.input.UpdateAvisoInput;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.exception.AvisoNotFoundException;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.valueobject.AvisoId;
import com.serviflow.aviso.domain.valueobject.DireccionServicio;
import com.serviflow.aviso.domain.valueobject.Prioridad;

import java.time.LocalDateTime;
import jakarta.transaction.Transactional;

/**
 * Use case for updating an existing Aviso.
 * Orchestrates domain logic but does not implement business rules.
 * 
 * Note: Most fields are immutable once created. Only description, priority,
 * address, and scheduled date can be updated when in NUEVO or ASIGNADO state.
 */
public class UpdateAvisoUseCase {

    private final AvisoRepository avisoRepository;

    public UpdateAvisoUseCase(AvisoRepository avisoRepository) {
        this.avisoRepository = avisoRepository;
    }

    /**
     * Executes the use case to update an aviso.
     *
     * @param input the input data for updating the aviso
     * @return the updated aviso as output
     * @throws AvisoNotFoundException if the aviso does not exist
     */
    @Transactional
    public AvisoOutput execute(UpdateAvisoInput input) {
        AvisoId id = new AvisoId(input.avisoId());
        Aviso aviso = avisoRepository.findById(id)
                .orElseThrow(() -> new AvisoNotFoundException(input.avisoId()));

        // Parse prioridad if provided
        Prioridad prioridad = aviso.prioridad();
        if (input.prioridad() != null && !input.prioridad().isBlank()) {
            prioridad = Prioridad.valueOf(input.prioridad());
        }

        // Build DireccionServicio from input if address fields provided
        DireccionServicio direccion = aviso.direccionServicio();
        if (input.calle() != null || input.numero() != null || input.localidad() != null 
                || input.provincia() != null || input.codigoPostal() != null) {
            direccion = new DireccionServicio(
                input.calle() != null ? input.calle() : direccion.calle(),
                input.numero() != null ? input.numero() : direccion.numero(),
                input.localidad() != null ? input.localidad() : direccion.localidad(),
                input.provincia() != null ? input.provincia() : direccion.provincia(),
                input.codigoPostal() != null ? input.codigoPostal() : direccion.codigoPostal()
            );
        }

        // Use domain entity's updateInfo method (validates state)
        String descripcion = input.descripcion() != null ? input.descripcion() : aviso.descripcion();
        LocalDateTime fechaProgramada = input.fechaProgramada() != null ? input.fechaProgramada() : aviso.fechaProgramada();
        
        String materialesUsados = input.materialesUsados() != null ? input.materialesUsados() : aviso.materialesUsados();
        Aviso updated = aviso.updateInfo(descripcion, prioridad, direccion, fechaProgramada, materialesUsados);
        
        // Add observation for audit trail
        updated.addObservacion("Aviso actualizado: información modificada", "ACTUALIZACION", input.usuario());

        // Persist changes
        Aviso saved = avisoRepository.save(updated);
        return AvisoOutput.fromDomain(saved);
    }
}
