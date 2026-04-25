package com.serviflow.aviso.application;

import com.serviflow.aviso.application.input.ListAvisosInput;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.application.output.PaginatedResponse;
import com.serviflow.aviso.domain.entity.Aviso;
import com.serviflow.aviso.domain.port.AvisoRepository;
import com.serviflow.aviso.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListAvisosUseCaseTest {
    @Mock private AvisoRepository avisoRepository;
    @InjectMocks private ListAvisosUseCase useCase;
    private List<Aviso> avisos;

    @BeforeEach
    void setUp() {
        DireccionServicio dir = new DireccionServicio("C", "1", "L", "P", "12345");
        avisos = List.of(
            Aviso.reconstitute(new AvisoId(1L), 1L, NumeroCorrelativo.generate(2024, 1), "Desc1", Prioridad.ALTA, EstadoAviso.NUEVO, dir, LocalDateTime.now(), null, null, null, null, java.util.List.of()),
            Aviso.reconstitute(new AvisoId(2L), 2L, NumeroCorrelativo.generate(2024, 2), "Desc2", Prioridad.BAJA, EstadoAviso.ASIGNADO, dir, LocalDateTime.now(), null, 1L, null, null, java.util.List.of())
        );
    }

    @Test
    void shouldReturnPaginatedAvisos() {
        // Order: clienteId, tecnicoId, estado, prioridad, search, page, size, sortBy, sortDir
        ListAvisosInput input = new ListAvisosInput(null, null, null, null, null, 0, 10, "fechaCreacion", "DESC");
        when(avisoRepository.findAll(any())).thenReturn(avisos);
        when(avisoRepository.count(any())).thenReturn(2L);

        PaginatedResponse<AvisoOutput> result = useCase.execute(input);

        assertThat(result.content()).hasSize(2);
        assertThat(result.totalElements()).isEqualTo(2);
    }

    @Test
    void shouldReturnEmptyWhenNoAvisos() {
        ListAvisosInput input = new ListAvisosInput(null, null, null, null, null, 0, 10, null, null);
        when(avisoRepository.findAll(any())).thenReturn(List.of());
        when(avisoRepository.count(any())).thenReturn(0L);

        PaginatedResponse<AvisoOutput> result = useCase.execute(input);
        assertThat(result.content()).isEmpty();
    }
}