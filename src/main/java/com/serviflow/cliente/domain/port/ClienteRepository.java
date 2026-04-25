package com.serviflow.cliente.domain.port;

import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.valueobject.ClienteId;

import java.util.List;
import java.util.Optional;

/**
 * Output port (repository interface) for Cliente persistence operations.
 * This is a pure Java interface with no framework dependencies.
 */
public interface ClienteRepository {

    /**
     * Saves a Cliente entity.
     * 
     * @param cliente the Cliente to save
     * @return the saved Cliente (with assigned ID)
     */
    Cliente save(Cliente cliente);

    /**
     * Finds a Cliente by its ID.
     * 
     * @param id the ClienteId to search for
     * @return an Optional containing the Cliente if found, empty otherwise
     */
    Optional<Cliente> findById(ClienteId id);

    /**
     * Finds all Cliente entities matching the given search criteria.
     * 
     * @param criteria the search criteria
     * @return a list of matching Cliente entities
     */
    List<Cliente> findAll(ClienteSearchCriteria criteria);

    /**
     * Counts the total number of Cliente entities matching the given search criteria.
     * 
     * @param criteria the search criteria
     * @return the total count of matching entities
     */
    long count(ClienteSearchCriteria criteria);

    /**
     * Checks if a Cliente exists with the given phone number.
     * 
     * @param telefono the phone number to check
     * @return true if a Cliente exists with this phone number, false otherwise
     */
    boolean existsByTelefono(String telefono);

    /**
     * Checks if a Cliente exists with the given phone number, excluding the specified ID.
     * Used for update operations to allow the same phone for the same entity.
     * 
     * @param telefono the phone number to check
     * @param excludeId the ClienteId to exclude from the search
     * @return true if a Cliente exists with this phone number and a different ID
     */
    boolean existsByTelefonoAndIdNot(String telefono, ClienteId excludeId);

    /**
     * Checks if a Cliente exists with the given ID.
     * 
     * @param id the ClienteId to check
     * @return true if a Cliente exists with this ID
     */
    boolean existsById(ClienteId id);
}
