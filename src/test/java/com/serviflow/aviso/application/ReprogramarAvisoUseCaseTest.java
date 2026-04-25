package com.serviflow.aviso.application;

import com.serviflow.aviso.application.input.ReprogramarInput;
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
 * Unit tests for ReprogramarAvisoUseCase.
 */
@ExtendWith(MockitoExtension.class)
class ReprogramarAvisoUseCaseTest {

    @Mock
    private AvisoRepository avisoRepository;

    @InjectMocks
    private ReprogramarAvisoUseCase useCase;

    private Aviso asignadoAviso;

    @BeforeEach
    void setUp() {
        DireccionServicio dir = new DireccionServicio("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        asignadoAviso = Aviso.reconstitute(
            new AvisoId(1L),
            1L,
            NumeroCorrelativo.generate(2026, 1),
            "Test description",
            Prioridad.MEDIA,
            EstadoAviso.ASIGNADO,
            dir,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            1L,
            null,
            null,
            java.util.List.of()
        );
    }

    @Test
    void execute_shouldReprogramarAviso() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.now().plusDays(3);
        ReprogramarInput input = new ReprogramarInput(1L, newDate, null, "test-user");

        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(asignadoAviso));
        when(avisoRepository.save(any(Aviso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AvisoOutput result = useCase.execute(input);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void execute_shouldReprogramarWithNewTecnico() {
        // Arrange
        LocalDateTime newDate = LocalDateTime.now().plusDays(3);
        ReprogramarInput input = new ReprogramarInput(1L, newDate, 5L, "test-user");

        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(asignadoAviso));
        when(avisoRepository.save(any(Aviso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AvisoOutput result = useCase.execute(input);

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void execute_shouldRejectReprogramarOnCompleted() {
        // Arrange
        DireccionServicio dir = new DireccionServicio("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        Aviso completedAviso = Aviso.reconstitute(
            new AvisoId(1L),
            1L,
            NumeroCorrelativo.generate(2026, 1),
            "Test description",
            Prioridad.MEDIA,
            EstadoAviso.COMPLETADO,
            dir,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            java.util.List.of()
        );

        ReprogramarInput input = new ReprogramarInput(1L, LocalDateTime.now().plusDays(1), null, "test-user");

        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(completedAviso));

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(input))
            .isInstanceOf(Exception.class);
    }

    @Test
    void execute_shouldRejectReprogramarOnCancelled() {
        // Arrange
        DireccionServicio dir = new DireccionServicio("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        Aviso cancelledAviso = Aviso.reconstitute(
            new AvisoId(1L),
            1L,
            NumeroCorrelativo.generate(2026, 1),
            "Test description",
            Prioridad.MEDIA,
            EstadoAviso.CANCELADO,
            dir,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            1L,
            null,
            null,
            java.util.List.of()
        );

        ReprogramarInput input = new ReprogramarInput(1L, LocalDateTime.now().plusDays(1), null, "test-user");

        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(cancelledAviso));

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(input))
            .isInstanceOf(Exception.class);
    }

    @Test
    void execute_shouldThrowWhenAvisoNotFound() {
        // Arrange
        ReprogramarInput input = new ReprogramarInput(999L, LocalDateTime.now().plusDays(1), null, "test-user");

        when(avisoRepository.findById(new AvisoId(999L))).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(input))
            .isInstanceOf(com.serviflow.aviso.domain.exception.AvisoNotFoundException.class);
    }
}
