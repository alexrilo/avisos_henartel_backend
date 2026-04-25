package com.serviflow.cliente.infrastructure.persistence;

import com.serviflow.cliente.domain.entity.Cliente;
import com.serviflow.cliente.domain.port.ClienteRepository;
import com.serviflow.cliente.domain.port.ClienteSearchCriteria;
import com.serviflow.cliente.domain.valueobject.ClienteId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the domain ClienteRepository port using Spring Data JPA.
 * This is the Infrastructure layer implementation of the Domain port.
 */
@Repository
public class ClienteRepositoryAdapter implements ClienteRepository {

    private final JpaClienteRepository jpaRepository;
    private final ClienteMapper mapper;

    public ClienteRepositoryAdapter(JpaClienteRepository jpaRepository, ClienteMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Cliente save(Cliente cliente) {
        JpaClienteEntity entity = mapper.toJpa(cliente);
        JpaClienteEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Cliente> findById(ClienteId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Cliente> findAll(ClienteSearchCriteria criteria) {
        Specification<JpaClienteEntity> spec = buildSpecification(criteria);

        String sortDir = criteria.sortAsc() ? "ASC" : "DESC";
        PageRequest pageRequest = PageRequest.of(
            criteria.page(),
            criteria.size(),
            Sort.by(Sort.Direction.fromString(sortDir), criteria.sortBy())
        );

        Page<JpaClienteEntity> page = jpaRepository.findAll(spec, pageRequest);
        return page.getContent().stream().map(mapper::toDomain).toList();
    }

    @Override
    public long count(ClienteSearchCriteria criteria) {
        Specification<JpaClienteEntity> spec = buildSpecification(criteria);
        return jpaRepository.count(spec);
    }

    @Override
    public boolean existsByTelefono(String telefono) {
        return jpaRepository.existsByTelefono(telefono);
    }

    @Override
    public boolean existsByTelefonoAndIdNot(String telefono, ClienteId id) {
        return jpaRepository.existsByTelefonoAndIdNot(telefono, id.value());
    }

    @Override
    public boolean existsById(ClienteId id) {
        return jpaRepository.existsById(id.value());
    }

    /**
     * Builds a JPA Specification from the domain search criteria.
     */
    private Specification<JpaClienteEntity> buildSpecification(ClienteSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            // Search term filter
            criteria.searchTerm().ifPresent(search -> {
                if (!search.isBlank()) {
                    String searchPattern = "%" + search.toLowerCase() + "%";
                    predicates.add(cb.or(
                        cb.like(cb.lower(root.get("nombreOrazonSocial")), searchPattern),
                        cb.like(cb.lower(root.get("telefono")), searchPattern)
                    ));
                }
            });

            // Estado filter
            criteria.estado().ifPresent(estado ->
                predicates.add(cb.equal(root.get("estado"), estado.name()))
            );

            // Tipo filter
            criteria.tipo().ifPresent(tipo ->
                predicates.add(cb.equal(root.get("tipo"), tipo.name()))
            );

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
