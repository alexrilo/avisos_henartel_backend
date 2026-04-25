package com.serviflow.aviso.application;

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
 * Unit tests for CancelarAvisoUseCase.
 */
@ExtendWith(MockitoExtension.class)
class CancelarAvisoUseCaseTest {

    @Mock
    private AvisoRepository avisoRepository;

    private CancelarAvisoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CancelarAvisoUseCase(avisoRepository);
    }

    @Test
    void execute_shouldCancelAviso_whenAvisoIsNuevo() {
        // Arrange
        Long avisoId = 1L;
        String usuario = "test-user";

        NumeroCorrelativo correlativo = NumeroCorrelativo.generate(2026, 1);
        DireccionServicio direccion = new DireccionServicio(
            "Calle Falsa", "123", "Madrid", "Madrid", "28001"
        );

        // Create an aviso in NUEVO state - can be cancelled
        Aviso aviso = Aviso.reconstitute(
            new AvisoId(avisoId),
            1L,
            correlativo,
            "Test description",
            Prioridad.ALTA,
            EstadoAviso.NUEVO,
            direccion,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            null,
            null,
            null,
            List.of()
        );

        when(avisoRepository.findById(new AvisoId(avisoId))).thenReturn(Optional.of(aviso));
        
        // After cancellation, the estado changes to CANCELADO
        // We need to capture the saved entity
        when(avisoRepository.save(any(Aviso.class))).thenAnswer(invocation -> {
            Aviso saved = invocation.getArgument(0);
            // Return a cancelled version
            return Aviso.reconstitute(
                saved.id(),
                saved.clienteId(),
                saved.numeroCorrelativo(),
                saved.descripcion(),
                saved.prioridad(),
                EstadoAviso.CANCELADO,
                saved.direccionServicio(),
                saved.fechaCreacion(),
                saved.fechaProgramada(),
                saved.tecnicoId(),
                saved.fechaInicio(),
                saved.fechaFin(),
                saved.observaciones()
            );
        });

        // Act
        com.serviflow.aviso.application.output.AvisoOutput result = useCase.execute(avisoId, usuario);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(avisoId);
        assertThat(result.estado()).isEqualTo("CANCELADO");

        verify(avisoRepository).findById(new AvisoId(avisoId));
        verify(avisoRepository).save(any(Aviso.class));
    }

    @Test
    void execute_shouldThrowAvisoNotFoundException_whenAvisoDoesNotExist() {
        // Arrange
        Long avisoId = 999L;
        String usuario = "test-user";

        when(avisoRepository.findById(new AvisoId(avisoId))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(avisoId, usuario))
            .isInstanceOf(AvisoNotFoundException.class)
            .hasMessageContaining("Aviso not found with id: " + avisoId);

        verify(avisoRepository).findById(new AvisoId(avisoId));
        verify(avisoRepository, org.mockito.Mockito.never()).save(any(Aviso.class));
    }

    @Test
    void execute_shouldThrowException_whenAvisoIsAlreadyCompleted() {
        // Arrange
        Long avisoId = 1L;
        String usuario = "test-user";

        NumeroCorrelativo correlativo = NumeroCorrelativo.generate(2026, 1);
        DireccionServicio direccion = new DireccionServicio(
            "Calle Falsa", "123", "Madrid", "Madrid", "28001"
        );

        // Create an aviso in COMPLETADO state - cannot be cancelled
        Aviso aviso = Aviso.reconstitute(
            new AvisoId(avisoId),
            1L,
            correlativo,
            "Test description",
            Prioridad.ALTA,
            EstadoAviso.COMPLETADO,
            direccion,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            100L,
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now(),
            List.of()
        );

        when(avisoRepository.findById(new AvisoId(avisoId))).thenReturn(Optional.of(aviso));

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(avisoId, usuario))
            .hasMessageContaining("Cannot cancel a completed aviso");

        verify(avisoRepository).findById(new AvisoId(avisoId));
        verify(avisoRepository, org.mockito.Mockito.never()).save(any(Aviso.class));
    }

    @Test
    void execute_shouldThrowException_whenAvisoIsAlreadyCancelled() {
        // Arrange
        Long avisoId = 1L;
        String usuario = "test-user";

        NumeroCorrelativo correlativo = NumeroCorrelativo.generate(2026, 1);
        DireccionServicio direccion = new DireccionServicio(
            "Calle Falsa", "123", "Madrid", "Madrid", "28001"
        );

        // Create an aviso already in CANCELADO state
        Aviso aviso = Aviso.reconstitute(
            new AvisoId(avisoId),
            1L,
            correlativo,
            "Test description",
            Prioridad.ALTA,
            EstadoAviso.CANCELADO,
            direccion,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            null,
            null,
            null,
            List.of()
        );

        when(avisoRepository.findById(new AvisoId(avisoId))).thenReturn(Optional.of(aviso));

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(avisoId, usuario))
            .hasMessageContaining("Aviso is already cancelled");

        verify(avisoRepository).findById(new AvisoId(avisoId));
        verify(avisoRepository, org.mockito.Mockito.never()).save(any(Aviso.class));
    }
}