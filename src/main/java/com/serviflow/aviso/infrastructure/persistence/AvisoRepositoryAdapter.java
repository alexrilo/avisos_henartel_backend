package com.serviflow.aviso.infrastructure.persistence;

import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.port.AvisoSearchCriteria;
import com.serviflow.aviso.domain.port.CorrelativoRepository;
import com.serviflow.aviso.domain.valueobject.AvisoId;
import com.serviflow.aviso.domain.valueobject.EstadoAviso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter that implements the domain AvisoRepository and CorrelativoRepository ports using Spring Data JPA.
 * This is the Infrastructure layer implementation of the Domain ports.
 */
@Repository
public class AvisoRepositoryAdapter implements AvisoRepository, CorrelativoRepository {

    private final JpaAvisoRepository jpaRepository;
    private final JpaCorrelativoRepository correlativoRepository;
    private final AvisoMapper mapper;

    public AvisoRepositoryAdapter(JpaAvisoRepository jpaRepository, 
                                   JpaCorrelativoRepository correlativoRepository,
                                   AvisoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.correlativoRepository = correlativoRepository;
        this.mapper = mapper;
    }

    @Override
    public Aviso save(Aviso aviso) {
        JpaAvisoEntity entity = mapper.toJpa(aviso);
        JpaAvisoEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Aviso> findById(AvisoId id) {
        return jpaRepository.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Aviso> findAll(AvisoSearchCriteria criteria) {
        Specification<JpaAvisoEntity> spec = buildSpecification(criteria);
        // Use default sort if not provided
        Sort sort = Sort.by(Sort.Direction.DESC, "fechaCreacion");
        PageRequest pageRequest = PageRequest.of(criteria.page(), criteria.size(), sort);
        Page<JpaAvisoEntity> page = jpaRepository.findAll(spec, pageRequest);
        return page.getContent().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public long count(AvisoSearchCriteria criteria) {
        Specification<JpaAvisoEntity> spec = buildSpecification(criteria);
        return jpaRepository.count(spec);
    }

    @Override
    public Optional<Aviso> findByNumeroCorrelativo(String numeroCorrelativo) {
        return jpaRepository.findByNumeroCorrelativo(numeroCorrelativo).map(mapper::toDomain);
    }

    @Override
    public List<Aviso> findByTecnicoId(Long tecnicoId, EstadoAviso estado) {
        Specification<JpaAvisoEntity> spec = Specification.where(
            (root, query, cb) -> cb.equal(root.get("tecnicoId"), tecnicoId)
        );
        if (estado != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("estado"), estado.name()));
        }
        return jpaRepository.findAll(spec).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int getNextSequence(int year) {
        // Try to find existing correlativo for the year
        JpaCorrelativoEntity entity = correlativoRepository.findById(year)
            .orElseGet(() -> {
                // Create new if doesn't exist
                JpaCorrelativoEntity newEntity = new JpaCorrelativoEntity(year, 0);
                return correlativoRepository.save(newEntity);
            });
        
        // Increment the sequence
        entity.setLastSequence(entity.getLastSequence() + 1);
        correlativoRepository.save(entity);
        
        return entity.getLastSequence();
    }

    /**
     * Builds a JPA Specification from the domain search criteria.
     */
    private Specification<JpaAvisoEntity> buildSpecification(AvisoSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            // Search term filter (maps from domain's "keyword")
            if (criteria.keyword() != null && !criteria.keyword().isBlank()) {
                String search = "%" + criteria.keyword().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("descripcion")), search),
                    cb.like(cb.lower(root.get("numeroCorrelativo")), search)
                ));
            }

            // Estado filter
            if (criteria.estado() != null) {
                predicates.add(cb.equal(root.get("estado"), criteria.estado().name()));
            }

            // Prioridad filter
            if (criteria.prioridad() != null) {
                predicates.add(cb.equal(root.get("prioridad"), criteria.prioridad().name()));
            }

            // Cliente ID filter
            if (criteria.clienteId() != null) {
                predicates.add(cb.equal(root.get("clienteId"), criteria.clienteId()));
            }

            // Tecnico ID filter
            if (criteria.tecnicoId() != null) {
                predicates.add(cb.equal(root.get("tecnicoId"), criteria.tecnicoId()));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}