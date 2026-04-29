package com.serviflow.aviso.infrastructure.web;

import com.serviflow.aviso.application.*;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.application.output.DireccionOutput;
import com.serviflow.aviso.application.output.PaginatedResponse;
import com.serviflow.auth.domain.port.TokenProvider;
import com.serviflow.test.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AvisoController.
 */
@WebMvcTest(AvisoController.class)
@Import(TestSecurityConfig.class)
class AvisoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateAvisoUseCase createAvisoUseCase;

    @MockBean
    private UpdateAvisoUseCase updateAvisoUseCase;

    @MockBean
    private GetAvisoUseCase getAvisoUseCase;

    @MockBean
    private ListAvisosUseCase listAvisosUseCase;

    @MockBean
    private AssignTecnicoUseCase assignTecnicoUseCase;

    @MockBean
    private ChangeEstadoUseCase changeEstadoUseCase;

    @MockBean
    private ReprogramarAvisoUseCase reprogramarAvisoUseCase;

    @MockBean
    private CancelarAvisoUseCase cancelarAvisoUseCase;

    @MockBean
    private GetMisTrabajosUseCase getMisTrabajosUseCase;

    @MockBean
    private TokenProvider tokenProvider;

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldCreateAviso() throws Exception {
        // Arrange
        DireccionOutput direccion = new DireccionOutput("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        AvisoOutput output = new AvisoOutput(
            1L, "AVI-2026-0001", 1L, null, null, null, "Test description",
            "MEDIA", "NUEVO", direccion, LocalDateTime.now(), null, null, null, null, null, List.of()
        );
        when(createAvisoUseCase.execute(any())).thenReturn(output);

        // Act & Assert
        mockMvc.perform(post("/api/avisos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "clienteId": 1,
                        "descripcion": "Test description",
                        "prioridad": "MEDIA",
                        "calle": "Calle Falsa",
                        "numero": "123",
                        "localidad": "Madrid",
                        "provincia": "Madrid",
                        "codigoPostal": "28001",
                        "materialesUsados": "Cinta aislante"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.numeroCorrelativo").value("AVI-2026-0001"))
            .andExpect(jsonPath("$.estado").value("NUEVO"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldListAvisos() throws Exception {
        // Arrange
        PaginatedResponse<AvisoOutput> response = new PaginatedResponse<>(
            List.of(), 0L, 0, 0, 20
        );
        when(listAvisosUseCase.execute(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/avisos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldGetAvisoById() throws Exception {
        // Arrange
        DireccionOutput direccion = new DireccionOutput("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        AvisoOutput output = new AvisoOutput(
            1L, "AVI-2026-0001", 1L, null, null, null, "Test description",
            "MEDIA", "NUEVO", direccion, LocalDateTime.now(), null, null, null, null, "Cables y conectores", List.of()
        );
        when(getAvisoUseCase.execute(any(Long.class), any())).thenReturn(output);

        // Act & Assert
        mockMvc.perform(get("/api/avisos/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.numeroCorrelativo").value("AVI-2026-0001"))
            .andExpect(jsonPath("$.materialesUsados").value("Cables y conectores"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldUpdateAviso() throws Exception {
        // Arrange
        DireccionOutput direccion = new DireccionOutput("Nueva Calle", "456", "Barcelona", "Barcelona", "08001");
        AvisoOutput output = new AvisoOutput(
            1L, "AVI-2026-0001", 1L, null, null, null, "Updated description",
            "ALTA", "NUEVO", direccion, LocalDateTime.now(), null, null, null, null, null, List.of()
        );
        when(updateAvisoUseCase.execute(any())).thenReturn(output);

        // Act & Assert
        mockMvc.perform(put("/api/avisos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "descripcion": "Updated description",
                        "prioridad": "ALTA"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.descripcion").value("Updated description"))
            .andExpect(jsonPath("$.prioridad").value("ALTA"));
    }

    @Test
    @WithMockUser(roles = "TECNICO")
    void shouldDenyCreateToTecnico() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/avisos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isForbidden());
    }

    @Test
    void shouldDenyAccessWhenNotAuthenticated() throws Exception {
        // When not authenticated, access should be denied (401 or 403 depending on security config)
        mockMvc.perform(get("/api/avisos"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldChangeEstado() throws Exception {
        // Arrange
        DireccionOutput direccion = new DireccionOutput("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        AvisoOutput output = new AvisoOutput(
            1L, "AVI-2026-0001", 1L, null, null, null, "Test description",
            "MEDIA", "ASIGNADO", direccion, LocalDateTime.now(), null, 1L, null, null, null, List.of()
        );
        when(changeEstadoUseCase.execute(any())).thenReturn(output);

        // Act & Assert
        mockMvc.perform(post("/api/avisos/1/cambiar-estado")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "estado": "ASIGNADO",
                        "tecnicoId": 1,
                        "materialesUsados": "Cables y conectores"
                    }
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("ASIGNADO"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldReprogramar() throws Exception {
        // Arrange
        DireccionOutput direccion = new DireccionOutput("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        LocalDateTime newDate = LocalDateTime.now().plusDays(1);
        AvisoOutput output = new AvisoOutput(
            1L, "AVI-2026-0001", 1L, null, null, null, "Test description",
            "MEDIA", "ASIGNADO", direccion, LocalDateTime.now(), null, 1L, null, null, null, List.of()
        );
        when(reprogramarAvisoUseCase.execute(any())).thenReturn(output);

        // Act & Assert
        mockMvc.perform(post("/api/avisos/1/reprogramar")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "nuevaFecha": "2026-04-05T10:00:00",
                        "nuevoTecnicoId": null
                    }
                    """))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldCancel() throws Exception {
        // Arrange
        DireccionOutput direccion = new DireccionOutput("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        AvisoOutput output = new AvisoOutput(
            1L, "AVI-2026-0001", 1L, null, null, null, "Test description",
            "MEDIA", "CANCELADO", direccion, LocalDateTime.now(), null, null, null, null, null, List.of()
        );
        when(cancelarAvisoUseCase.execute(any(Long.class), any())).thenReturn(output);

        // Act & Assert
        mockMvc.perform(post("/api/avisos/1/cancelar")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("CANCELADO"));
    }
}
