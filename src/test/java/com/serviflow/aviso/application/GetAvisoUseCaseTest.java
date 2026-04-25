package com.serviflow.aviso.application;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAvisoUseCaseTest {
    @Mock private AvisoRepository avisoRepository;
    @Mock private com.serviflow.cliente.domain.port.ClienteRepository clienteRepository;
    private GetAvisoUseCase useCase;
    private Aviso aviso;

    @BeforeEach
    void setUp() {
        useCase = new GetAvisoUseCase(avisoRepository, clienteRepository);
        DireccionServicio dir = new DireccionServicio("C", "1", "L", "P", "12345");
        aviso = Aviso.reconstitute(
            new AvisoId(1L), 1L, NumeroCorrelativo.generate(2024, 1), "Desc",
            Prioridad.MEDIA, EstadoAviso.NUEVO, dir, LocalDateTime.now(), null, null, null, null, java.util.List.of()
        );
    }

    @Test
    void shouldReturnAvisoWhenFound() {
        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(aviso));
        AvisoOutput result = useCase.execute(1L, null);
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenNotFound() {
        when(avisoRepository.findById(new AvisoId(999L))).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.execute(999L, null))
            .isInstanceOf(AvisoNotFoundException.class);
    }

    @Test
    void shouldThrowWhenTecnicoDoesNotOwnAviso() {
        when(avisoRepository.findById(new AvisoId(1L))).thenReturn(Optional.of(aviso));
        assertThatThrownBy(() -> useCase.execute(1L, 999L))
            .isInstanceOf(Exception.class);
    }
}