package com.serviflow.cliente.domain.port;

import com.serviflow.cliente.domain.valueobject.ClienteStatus;
import com.serviflow.cliente.domain.valueobject.TipoCliente;

import java.util.Optional;

/**
 * Value object representing search criteria for querying Cliente entities.
 * Supports filtering, pagination, and sorting.
 */
public final class ClienteSearchCriteria {

    private final String searchTerm;
    private final ClienteStatus estado;
    private final TipoCliente tipo;
    private final int page;
    private final int size;
    private final String sortBy;
    private final boolean sortAsc;

    private ClienteSearchCriteria(String searchTerm, ClienteStatus estado, TipoCliente tipo,
                                   int page, int size, String sortBy, boolean sortAsc) {
        this.searchTerm = searchTerm;
        this.estado = estado;
        this.tipo = tipo;
        this.page = Math.max(0, page);
        this.size = Math.min(Math.max(1, size), 100);
        this.sortBy = sortBy != null ? sortBy : "fechaCreacion";
        this.sortAsc = sortAsc;
    }

    /**
     * Creates a new Builder for ClienteSearchCriteria.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a default search criteria (no filters, defaults for pagination/sorting).
     */
    public static ClienteSearchCriteria defaultCriteria() {
        return builder().build();
    }

    // Getters

    public Optional<String> searchTerm() {
        return Optional.ofNullable(searchTerm);
    }

    public Optional<ClienteStatus> estado() {
        return Optional.ofNullable(estado);
    }

    public Optional<TipoCliente> tipo() {
        return Optional.ofNullable(tipo);
    }

    public int page() {
        return page;
    }

    public int size() {
        return size;
    }

    public String sortBy() {
        return sortBy;
    }

    public boolean sortAsc() {
        return sortAsc;
    }

    /**
     * Builder for ClienteSearchCriteria.
     */
    public static class Builder {
        private String searchTerm;
        private ClienteStatus estado;
        private TipoCliente tipo;
        private int page = 0;
        private int size = 20;
        private String sortBy = "fechaCreacion";
        private boolean sortAsc = false;

        public Builder searchTerm(String searchTerm) {
            this.searchTerm = searchTerm;
            return this;
        }

        public Builder estado(ClienteStatus estado) {
            this.estado = estado;
            return this;
        }

        public Builder tipo(TipoCliente tipo) {
            this.tipo = tipo;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder sortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }

        public Builder sortAsc(boolean sortAsc) {
            this.sortAsc = sortAsc;
            return this;
        }

        public ClienteSearchCriteria build() {
            return new ClienteSearchCriteria(searchTerm, estado, tipo, page, size, sortBy, sortAsc);
        }
    }
}
