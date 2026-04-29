package com.serviflow.aviso.application;

import com.serviflow.aviso.application.input.ChangeEstadoInput;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.valueobject.AvisoId;
import com.serviflow.aviso.domain.valueobject.DireccionServicio;
import com.serviflow.aviso.domain.valueobject.EstadoAviso;
import com.serviflow.aviso.domain.valueobject.NumeroCorrelativo;
import com.serviflow.aviso.domain.valueobject.Prioridad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Unit tests for ChangeEstadoUseCase.
 */
@ExtendWith(MockitoExtension.class)
class ChangeEstadoUseCaseTest {

    @Mock
    private AvisoRepository avisoRepository;

    @InjectMocks
    private ChangeEstadoUseCase useCase;

    private Aviso nuevoAviso;

    @BeforeEach
    void setUp() {
        DireccionServicio dir = new DireccionServicio("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        nuevoAviso = Aviso.reconstitute(
            new AvisoId(1L),
            1L,
            NumeroCorrelativo.generate(2026, 1),
            "Test description",
            Prioridad.MEDIA,
            EstadoAviso.NUEVO,
            dir,
            LocalDateTime.now(),
            null,
            null,
            null,
            null,
            java.util.List.of(),
            null
        );
    }

    @Test
    void execute_shouldTransitionNuevoToAsignado() {
        // Arrange
        ChangeEstadoInput input = new ChangeEstadoInput(1L, "ASIGNADO", 1L, "test-user", null, null);

        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(nuevoAviso));
        when(avisoRepository.save(any(Aviso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AvisoOutput result = useCase.execute(input);

        // Assert
        assertThat(result.estado()).isEqualTo("ASIGNADO");
    }

    @Test
    void execute_shouldTransitionAsignadoToEnCurso() {
        // Arrange
        DireccionServicio dir = new DireccionServicio("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        Aviso asignadoAviso = Aviso.reconstitute(
            new AvisoId(1L),
            1L,
            NumeroCorrelativo.generate(2026, 1),
            "Test description",
            Prioridad.MEDIA,
            EstadoAviso.ASIGNADO,
            dir,
            LocalDateTime.now(),
            null,
            1L,
            null,
            null,
            java.util.List.of(),
            null
        );

        ChangeEstadoInput input = new ChangeEstadoInput(1L, "EN_CURSO", null, "test-user", null, null);

        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(asignadoAviso));
        when(avisoRepository.save(any(Aviso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AvisoOutput result = useCase.execute(input);

        // Assert
        assertThat(result.estado()).isEqualTo("EN_CURSO");
    }

    @Test
    void execute_shouldTransitionEnCursoToCompletado() {
        // Arrange
        DireccionServicio dir = new DireccionServicio("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        Aviso enCursoAviso = Aviso.reconstitute(
            new AvisoId(1L),
            1L,
            NumeroCorrelativo.generate(2026, 1),
            "Test description",
            Prioridad.MEDIA,
            EstadoAviso.EN_CURSO,
            dir,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L,
            LocalDateTime.now(),
            null,
            java.util.List.of(),
            null
        );

        ChangeEstadoInput input = new ChangeEstadoInput(1L, "COMPLETADO", null, "test-user", null, "Herramientas y repuestos varios");

        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(enCursoAviso));
        when(avisoRepository.save(any(Aviso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AvisoOutput result = useCase.execute(input);

        // Assert
        assertThat(result.estado()).isEqualTo("COMPLETADO");
        assertThat(result.materialesUsados()).isEqualTo("Herramientas y repuestos varios");
    }

    @Test
    void execute_shouldIgnoreMaterialesUsadosWhenNotCompleting() {
        // Arrange
        ChangeEstadoInput input = new ChangeEstadoInput(1L, "ASIGNADO", 1L, "test-user", null, "No debe guardarse");

        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(nuevoAviso));
        when(avisoRepository.save(any(Aviso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AvisoOutput result = useCase.execute(input);

        // Assert
        assertThat(result.estado()).isEqualTo("ASIGNADO");
        assertThat(result.materialesUsados()).isNull();
    }

    @Test
    void execute_shouldRejectInvalidTransition() {
        // Arrange - Cannot go directly from NUEVO to COMPLETADO
        ChangeEstadoInput input = new ChangeEstadoInput(1L, "COMPLETADO", null, "test-user", null, null);

        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(nuevoAviso));

        // Act & Assert - Domain validation should throw DomainException
        assertThatThrownBy(() -> useCase.execute(input))
            .isInstanceOf(com.serviflow.shared.domain.exception.DomainException.class);
    }

    @Test
    void execute_shouldTransitionToPendienteSeguimiento() {
        // Arrange
        DireccionServicio dir = new DireccionServicio("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        Aviso enCursoAviso = Aviso.reconstitute(
            new AvisoId(1L),
            1L,
            NumeroCorrelativo.generate(2026, 1),
            "Test description",
            Prioridad.MEDIA,
            EstadoAviso.EN_CURSO,
            dir,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L,
            LocalDateTime.now(),
            null,
            java.util.List.of(),
            null
        );

        ChangeEstadoInput input = new ChangeEstadoInput(1L, "PENDIENTE_SEGUIMIENTO", null, "test-user", null, null);

        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(enCursoAviso));
        when(avisoRepository.save(any(Aviso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AvisoOutput result = useCase.execute(input);

        // Assert
        assertThat(result.estado()).isEqualTo("PENDIENTE_SEGUIMIENTO");
    }

    @Test
    void execute_shouldCancelAviso() {
        // Arrange
        ChangeEstadoInput input = new ChangeEstadoInput(1L, "CANCELADO", null, "test-user", null, null);

        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(nuevoAviso));
        when(avisoRepository.save(any(Aviso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AvisoOutput result = useCase.execute(input);

        // Assert
        assertThat(result.estado()).isEqualTo("CANCELADO");
    }

    @Test
    void execute_shouldThrowWhenAvisoNotFound() {
        // Arrange
        ChangeEstadoInput input = new ChangeEstadoInput(999L, "ASIGNADO", 1L, "test-user", null, null);

        when(avisoRepository.findById(new AvisoId(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(input))
            .isInstanceOf(com.serviflow.aviso.domain.exception.AvisoNotFoundException.class);
    }
}
