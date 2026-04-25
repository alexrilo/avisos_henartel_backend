package com.serviflow.aviso.application;

import com.serviflow.aviso.application.input.AssignTecnicoInput;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.exception.AvisoNotFoundException;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.valueobject.AvisoId;
import com.serviflow.aviso.domain.valueobject.DireccionServicio;
import com.serviflow.aviso.domain.valueobject.EstadoAviso;
import com.serviflow.aviso.domain.valueobject.NumeroCorrelativo;
import com.serviflow.aviso.domain.valueobject.Prioridad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AssignTecnicoUseCase.
 */
@ExtendWith(MockitoExtension.class)
class AssignTecnicoUseCaseTest {

    @Mock
    private AvisoRepository avisoRepository;

    private AssignTecnicoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AssignTecnicoUseCase(avisoRepository);
    }

    @Test
    void execute_shouldAssignTecnico_whenAvisoExistsAndIsNuevo() {
        // Arrange
        Long avisoId = 1L;
        Long tecnicoId = 100L;

        NumeroCorrelativo correlativo = NumeroCorrelativo.generate(2026, 1);
        DireccionServicio direccion = new DireccionServicio(
            "Calle Falsa", "123", "Madrid", "Madrid", "28001"
        );

        // Create an aviso in NUEVO state
        Aviso aviso = Aviso.reconstitute(
            new AvisoId(avisoId),
            1L,
            correlativo,
            "Test description",
            Prioridad.ALTA,
            EstadoAviso.NUEVO, // Must be NUEVO to transition to ASIGNADO
            direccion,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            null, // No tecnico assigned yet
            null,
            null,
            List.of()
        );

        when(avisoRepository.findById(new AvisoId(avisoId))).thenReturn(Optional.of(aviso));
        
        // Create another aviso after state change (ASIGNADO)
        Aviso updatedAviso = Aviso.reconstitute(
            new AvisoId(avisoId),
            1L,
            correlativo,
            "Test description",
            Prioridad.ALTA,
            EstadoAviso.ASIGNADO,
            direccion,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            tecnicoId, // Tecnico now assigned
            null,
            null,
            List.of()
        );

        when(avisoRepository.save(any(Aviso.class))).thenReturn(updatedAviso);

        AssignTecnicoInput input = new AssignTecnicoInput(avisoId, tecnicoId, "test-user");

        // Act
        AvisoOutput result = useCase.execute(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(avisoId);
        assertThat(result.tecnicoId()).isEqualTo(tecnicoId);
        assertThat(result.estado()).isEqualTo("ASIGNADO");

        verify(avisoRepository).findById(new AvisoId(avisoId));
        verify(avisoRepository).save(any(Aviso.class));
    }

    @Test
    void execute_shouldThrowAvisoNotFoundException_whenAvisoDoesNotExist() {
        // Arrange
        Long avisoId = 999L;
        Long tecnicoId = 100L;

        when(avisoRepository.findById(new AvisoId(avisoId))).thenReturn(Optional.empty());

        AssignTecnicoInput input = new AssignTecnicoInput(avisoId, tecnicoId, "test-user");

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(input))
            .isInstanceOf(AvisoNotFoundException.class)
            .hasMessageContaining("Aviso not found with id: " + avisoId);

        verify(avisoRepository).findById(new AvisoId(avisoId));
        verify(avisoRepository, org.mockito.Mockito.never()).save(any(Aviso.class));
    }

    @Test
    void execute_shouldThrowException_whenAvisoIsAlreadyAsignado() {
        // Arrange
        Long avisoId = 1L;
        Long tecnicoId = 100L;

        NumeroCorrelativo correlativo = NumeroCorrelativo.generate(2026, 1);
        DireccionServicio direccion = new DireccionServicio(
            "Calle Falsa", "123", "Madrid", "Madrid", "28001"
        );

        // Create an aviso already in ASIGNADO state
        Aviso aviso = Aviso.reconstitute(
            new AvisoId(avisoId),
            1L,
            correlativo,
            "Test description",
            Prioridad.ALTA,
            EstadoAviso.ASIGNADO, // Already assigned - cannot transition
            direccion,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            tecnicoId, // Already has tecnico
            null,
            null,
            List.of()
        );

        when(avisoRepository.findById(new AvisoId(avisoId))).thenReturn(Optional.of(aviso));

        AssignTecnicoInput input = new AssignTecnicoInput(avisoId, tecnicoId, "test-user");

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(input))
            .hasMessageContaining("Cannot assign tecnico from state");

        verify(avisoRepository).findById(new AvisoId(avisoId));
        verify(avisoRepository, org.mockito.Mockito.never()).save(any(Aviso.class));
    }
}