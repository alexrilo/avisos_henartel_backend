package com.serviflow.aviso.domain.port;

import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.valueobject.ClienteId;

/**
 * Port interface for Cliente repository.
 * Used to validate cliente existence when creating avisos.
 */
public interface ClienteRepository {

    /**
     * Finds a cliente by its ID.
     */
    Cliente findById(ClienteId id);

    /**
     * Checks if a cliente exists.
     */
    boolean existsById(ClienteId id);
}
