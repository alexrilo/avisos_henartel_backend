package com.serviflow.aviso.domain.port;

/**
 * Port interface for generating correlative numbers.
 * Ensures atomic sequence generation for concurrency safety.
 */
public interface CorrelativoRepository {

    /**
     * Gets the next sequence number for the given year.
     * This operation should be atomic to prevent duplicates.
     */
    int getNextSequence(int year);
}
