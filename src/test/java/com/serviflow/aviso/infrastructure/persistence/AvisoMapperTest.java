package com.serviflow.aviso.infrastructure.persistence;

import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.valueobject.AvisoId;
import com.serviflow.aviso.domain.valueobject.DireccionServicio;
import com.serviflow.aviso.domain.valueobject.EstadoAviso;
import com.serviflow.aviso.domain.valueobject.NumeroCorrelativo;
import com.serviflow.aviso.domain.valueobject.Prioridad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AvisoMapper.
 */
class AvisoMapperTest {

    private AvisoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new AvisoMapper();
    }

    @Test
    void shouldMapJpaToDomain() {
        // Arrange
        JpaAvisoEntity entity = new JpaAvisoEntity();
        entity.setId(1L);
        entity.setClienteId(10L);
        entity.setNumeroCorrelativo("AVI-2026-0001");
        entity.setDescripcion("Test description");
        entity.setPrioridad("ALTA");
        entity.setEstado("NUEVO");
        entity.setCalle("Calle Falsa");
        entity.setNumero("123");
        entity.setLocalidad("Madrid");
        entity.setProvincia("Madrid");
        entity.setCodigoPostal("28001");
        entity.setFechaCreacion(LocalDateTime.now());

        // Act
        Aviso result = mapper.toDomain(entity);

        // Assert
        assertThat(result.id()).isEqualTo(new AvisoId(1L));
        assertThat(result.clienteId()).isEqualTo(10L);
        assertThat(result.numeroCorrelativo().value()).isEqualTo("AVI-2026-0001");
        assertThat(result.descripcion()).isEqualTo("Test description");
        assertThat(result.prioridad()).isEqualTo(Prioridad.ALTA);
        assertThat(result.estado()).isEqualTo(EstadoAviso.NUEVO);
        assertThat(result.direccionServicio().calle()).isEqualTo("Calle Falsa");
        assertThat(result.direccionServicio().numero()).isEqualTo("123");
        assertThat(result.direccionServicio().localidad()).isEqualTo("Madrid");
    }

    @Test
    void shouldMapDomainToJpa() {
        // Arrange
        DireccionServicio dir = new DireccionServicio("Nueva Calle", "456", "Barcelona", "Barcelona", "08001");
        Aviso aviso = Aviso.reconstitute(
            new AvisoId(5L),
            2L,
            NumeroCorrelativo.generate(2026, 5),
            "Test description",
            Prioridad.URGENTE,
            EstadoAviso.ASIGNADO,
            dir,
            LocalDateTime.now(),
            null,
            1L,
            null,
            null,
            new ArrayList<>()
        );

        // Act
        JpaAvisoEntity result = mapper.toJpa(aviso);

        // Assert
        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getClienteId()).isEqualTo(2L);
        assertThat(result.getNumeroCorrelativo()).isEqualTo("AVI-2026-0005");
        assertThat(result.getDescripcion()).isEqualTo("Test description");
        assertThat(result.getPrioridad()).isEqualTo("URGENTE");
        assertThat(result.getEstado()).isEqualTo("ASIGNADO");
        assertThat(result.getCalle()).isEqualTo("Nueva Calle");
        assertThat(result.getNumero()).isEqualTo("456");
        assertThat(result.getLocalidad()).isEqualTo("Barcelona");
        assertThat(result.getTecnicoId()).isEqualTo(1L);
    }

    @Test
    void shouldRoundtripCorrectly() {
        // Arrange - create original domain object
        DireccionServicio dir = new DireccionServicio("Calle Original", "100", "Sevilla", "Sevilla", "41001");
        LocalDateTime fechaCreacion = LocalDateTime.of(2026, 3, 15, 10, 30);
        LocalDateTime fechaProgramada = LocalDateTime.of(2026, 3, 20, 9, 0);
        
        Aviso original = Aviso.reconstitute(
            new AvisoId(10L),
            3L,
            NumeroCorrelativo.generate(2026, 10),
            "Roundtrip test",
            Prioridad.MEDIA,
            EstadoAviso.EN_CURSO,
            dir,
            fechaCreacion,
            fechaProgramada,
            2L,
            LocalDateTime.of(2026, 3, 18, 8, 0),
            null,
            new ArrayList<>()
        );

        // Act - domain to JPA and back
        JpaAvisoEntity entity = mapper.toJpa(original);
        Aviso restored = mapper.toDomain(entity);

        // Assert
        assertThat(restored.id()).isEqualTo(original.id());
        assertThat(restored.clienteId()).isEqualTo(original.clienteId());
        assertThat(restored.numeroCorrelativo()).isEqualTo(original.numeroCorrelativo());
        assertThat(restored.descripcion()).isEqualTo(original.descripcion());
        assertThat(restored.prioridad()).isEqualTo(original.prioridad());
        assertThat(restored.estado()).isEqualTo(original.estado());
        assertThat(restored.tecnicoId()).isEqualTo(original.tecnicoId());
        assertThat(restored.direccionServicio().calle()).isEqualTo(original.direccionServicio().calle());
        assertThat(restored.direccionServicio().localidad()).isEqualTo(original.direccionServicio().localidad());
    }

    @Test
    void shouldHandleNullObservaciones() {
        // Arrange
        JpaAvisoEntity entity = new JpaAvisoEntity();
        entity.setId(1L);
        entity.setClienteId(1L);
        entity.setNumeroCorrelativo("AVI-2026-0001");
        entity.setDescripcion("Test");
        entity.setPrioridad("MEDIA");
        entity.setEstado("NUEVO");
        entity.setCalle("C");
        entity.setNumero("1");
        entity.setLocalidad("L");
        entity.setProvincia("P");
        entity.setCodigoPostal("12345");
        entity.setFechaCreacion(LocalDateTime.now());
        entity.setObservaciones(null);

        // Act
        Aviso result = mapper.toDomain(entity);

        // Assert
        assertThat(result.observaciones()).isEmpty();
    }

    @Test
    void shouldPreserveIdWhenNull() {
        // Arrange
        JpaAvisoEntity entity = new JpaAvisoEntity();
        entity.setId(null);
        entity.setClienteId(1L);
        entity.setNumeroCorrelativo("AVI-2026-0001");
        entity.setDescripcion("Test");
        entity.setPrioridad("MEDIA");
        entity.setEstado("NUEVO");
        entity.setCalle("C");
        entity.setNumero("1");
        entity.setLocalidad("L");
        entity.setProvincia("P");
        entity.setCodigoPostal("12345");
        entity.setFechaCreacion(LocalDateTime.now());

        // Act
        Aviso result = mapper.toDomain(entity);

        // Assert
        assertThat(result.id()).isNull();
    }
}
