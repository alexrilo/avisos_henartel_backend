package com.serviflow.aviso.application;

import com.serviflow.aviso.application.input.UpdateAvisoInput;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.exception.AvisoNotFoundException;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAvisoUseCaseTest {
    @Mock private AvisoRepository avisoRepository;
    @InjectMocks private UpdateAvisoUseCase useCase;
    private Aviso existingAviso;

    @BeforeEach
    void setUp() {
        DireccionServicio dir = new DireccionServicio("Calle", "123", "Localidad", "Provincia", "12345");
        existingAviso = Aviso.reconstitute(
            new AvisoId(1L), 1L, NumeroCorrelativo.generate(2024, 1), "Desc",
            Prioridad.MEDIA, EstadoAviso.NUEVO, dir, LocalDateTime.now(), null, null, null, null, java.util.List.of()
        );
    }

    @Test
    void shouldUpdateAvisoWhenValidInput() {
        UpdateAvisoInput input = new UpdateAvisoInput(1L, "Updated Desc", "ALTA", "New St", "456", "New City", "New Prov", "54321", null, "user");
        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(existingAviso));
        when(avisoRepository.save(any(Aviso.class))).thenAnswer(inv -> inv.getArgument(0));

        AvisoOutput result = useCase.execute(input);

        assertThat(result.descripcion()).isEqualTo("Updated Desc");
        assertThat(result.prioridad()).isEqualTo("ALTA");
        verify(avisoRepository).save(any(Aviso.class));
    }

    @Test
    void shouldThrowWhenAvisoNotFound() {
        UpdateAvisoInput input = new UpdateAvisoInput(999L, "Desc", "BAJA", "C", "1", "L", "P", "12345", null, "user");
        when(avisoRepository.findById(new AvisoId(999L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(input))
            .isInstanceOf(AvisoNotFoundException.class);
    }

    @Test
    void shouldThrowWhenUpdatingCompletedAviso() {
        DireccionServicio dir = new DireccionServicio("C", "1", "L", "P", "12345");
        Aviso completed = Aviso.reconstitute(
            new AvisoId(1L), 1L, NumeroCorrelativo.generate(2024, 1), "Desc",
            Prioridad.MEDIA, EstadoAviso.COMPLETADO, dir, LocalDateTime.now(), null, null, null, null, java.util.List.of()
        );
        UpdateAvisoInput input = new UpdateAvisoInput(1L, "Updated", "ALTA", "C", "1", "L", "P", "12345", null, "user");
        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(completed));

        assertThatThrownBy(() -> useCase.execute(input))
            .isInstanceOf(Exception.class);
    }
}