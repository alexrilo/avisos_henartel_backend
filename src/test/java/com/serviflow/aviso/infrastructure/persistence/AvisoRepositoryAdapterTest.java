package com.serviflow.aviso.infrastructure.persistence;

import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.port.AvisoSearchCriteria;
import com.serviflow.aviso.domain.valueobject.AvisoId;
import com.serviflow.aviso.domain.valueobject.DireccionServicio;
import com.serviflow.aviso.domain.valueobject.NumeroCorrelativo;
import com.serviflow.aviso.domain.valueobject.Prioridad;
import com.serviflow.cliente.domain.port.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for AvisoRepositoryAdapter using @DataJpaTest.
 * Tests the repository layer with an in-memory H2 database.
 */
@DataJpaTest
@Import({AvisoRepositoryAdapter.class, AvisoMapper.class, ObservacionMapper.class})
class AvisoRepositoryAdapterTest {

    @Autowired
    private AvisoRepository avisoRepository;

    @Autowired
    private JpaAvisoRepository jpaRepository;

    @MockBean
    private ClienteRepository clienteRepository;

    @Test
    void shouldSaveAndFindAviso() {
        // Arrange
        DireccionServicio dir = new DireccionServicio("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        Aviso aviso = Aviso.create(
            1L,
            NumeroCorrelativo.generate(2026, 1),
            "Test description",
            Prioridad.ALTA,
            dir,
            null
        );

        // Act
        Aviso saved = avisoRepository.save(aviso);
        Optional<Aviso> found = avisoRepository.findById(saved.id());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().descripcion()).isEqualTo("Test description");
        assertThat(found.get().prioridad()).isEqualTo(Prioridad.ALTA);
        assertThat(found.get().estado()).isNotNull();
        assertThat(found.get().numeroCorrelativo().value()).startsWith("AVI-2026");
    }

    @Test
    void shouldFindAllWithPagination() {
        // Arrange
        DireccionServicio dir = new DireccionServicio("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        Aviso a1 = Aviso.create(
            1L,
            NumeroCorrelativo.generate(2026, 1),
            "Aviso A",
            Prioridad.ALTA,
            dir,
            null
        );
        Aviso a2 = Aviso.create(
            2L,
            NumeroCorrelativo.generate(2026, 2),
            "Aviso B",
            Prioridad.BAJA,
            dir,
            null
        );
        avisoRepository.save(a1);
        avisoRepository.save(a2);

        // Act
        AvisoSearchCriteria criteria = new AvisoSearchCriteria(null, null, null, null, null, 0, 10);
        List<Aviso> result = avisoRepository.findAll(criteria);

        // Assert
        assertThat(result).hasSize(2);
    }

    @Test
    void shouldFindByKeyword() {
        // Arrange
        DireccionServicio dir = new DireccionServicio("Calle Test", "456", "Barcelona", "Barcelona", "08001");
        Aviso aviso = Aviso.create(
            1L,
            NumeroCorrelativo.generate(2026, 3),
            "Test keyword search",
            Prioridad.MEDIA,
            dir,
            null
        );
        avisoRepository.save(aviso);

        // Act
        AvisoSearchCriteria criteria = new AvisoSearchCriteria(null, null, null, null, "keyword", 0, 10);
        List<Aviso> result = avisoRepository.findAll(criteria);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).descripcion()).contains("keyword");
    }

    @Test
    void shouldFindByEstado() {
        // Arrange
        DireccionServicio dir = new DireccionServicio("Calle Test", "456", "Barcelona", "Barcelona", "08001");
        Aviso aviso = Aviso.create(
            1L,
            NumeroCorrelativo.generate(2026, 4),
            "Test estado",
            Prioridad.MEDIA,
            dir,
            null
        );
        avisoRepository.save(aviso);

        // Act - filter by estado is done through criteria
        AvisoSearchCriteria criteria = new AvisoSearchCriteria(
            com.serviflow.aviso.domain.valueobject.EstadoAviso.NUEVO, null, null, null, null, 0, 10);
        List<Aviso> result = avisoRepository.findAll(criteria);

        // Assert
        assertThat(result).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldFindByPrioridad() {
        // Arrange
        DireccionServicio dir = new DireccionServicio("Calle Test", "456", "Barcelona", "Barcelona", "08001");
        Aviso aviso = Aviso.create(
            1L,
            NumeroCorrelativo.generate(2026, 5),
            "Test prioridad",
            Prioridad.URGENTE,
            dir,
            null
        );
        avisoRepository.save(aviso);

        // Act
        AvisoSearchCriteria criteria = new AvisoSearchCriteria(null, Prioridad.URGENTE, null, null, null, 0, 10);
        List<Aviso> result = avisoRepository.findAll(criteria);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).prioridad()).isEqualTo(Prioridad.URGENTE);
    }

    @Test
    void shouldFindByClienteId() {
        // Arrange
        Long clienteId = 100L;
        DireccionServicio dir = new DireccionServicio("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        Aviso aviso = Aviso.create(
            clienteId,
            NumeroCorrelativo.generate(2026, 6),
            "Test cliente",
            Prioridad.MEDIA,
            dir,
            null
        );
        avisoRepository.save(aviso);

        // Act
        AvisoSearchCriteria criteria = new AvisoSearchCriteria(null, null, null, clienteId, null, 0, 10);
        List<Aviso> result = avisoRepository.findAll(criteria);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).clienteId()).isEqualTo(clienteId);
    }

    @Test
    void shouldReturnEmptyWhenNotFound() {
        // Act
        Optional<Aviso> found = avisoRepository.findById(new AvisoId(99999L));

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void shouldSaveWithAllFields() {
        // Arrange
        DireccionServicio dir = new DireccionServicio("Nueva Calle", "999", "Sevilla", "Sevilla", "41001");
        LocalDateTime fechaProgramada = LocalDateTime.of(2026, 4, 15, 10, 0);
        
        Aviso aviso = Aviso.create(
            5L,
            NumeroCorrelativo.generate(2026, 7),
            "Full test description",
            Prioridad.ALTA,
            dir,
            fechaProgramada
        );

        // Act
        Aviso saved = avisoRepository.save(aviso);
        Optional<Aviso> found = avisoRepository.findById(saved.id());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().clienteId()).isEqualTo(5L);
        assertThat(found.get().descripcion()).isEqualTo("Full test description");
        assertThat(found.get().prioridad()).isEqualTo(Prioridad.ALTA);
        assertThat(found.get().direccionServicio().calle()).isEqualTo("Nueva Calle");
        assertThat(found.get().direccionServicio().numero()).isEqualTo("999");
        assertThat(found.get().direccionServicio().localidad()).isEqualTo("Sevilla");
    }
}