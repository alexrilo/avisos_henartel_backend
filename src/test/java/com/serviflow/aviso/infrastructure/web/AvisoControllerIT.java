package com.serviflow.aviso.infrastructure.web;

import com.serviflow.aviso.application.CreateAvisoUseCase;
import com.serviflow.aviso.application.ListAvisosUseCase;
import com.serviflow.aviso.application.output.AvisoOutput;
import com.serviflow.aviso.application.output.DireccionOutput;
import com.serviflow.aviso.application.output.PaginatedResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for AvisoController using @SpringBootTest.
 * Tests the full HTTP layer including security context.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AvisoControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateAvisoUseCase createAvisoUseCase;

    @MockBean
    private ListAvisosUseCase listAvisosUseCase;

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldCreateAvisoAndReturn201() throws Exception {
        // Arrange
        DireccionOutput direccion = new DireccionOutput("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        AvisoOutput output = new AvisoOutput(
            1L, "AVI-2026-0001", 1L, null, null, null, "Test description",
            "ALTA", "NUEVO", direccion, LocalDateTime.now(), null, null, null, null, List.of()
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
                        "prioridad": "ALTA",
                        "calle": "Calle Falsa",
                        "numero": "123",
                        "localidad": "Madrid",
                        "provincia": "Madrid",
                        "codigoPostal": "28001"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.numeroCorrelativo").value("AVI-2026-0001"))
            .andExpect(jsonPath("$.descripcion").value("Test description"))
            .andExpect(jsonPath("$.prioridad").value("ALTA"))
            .andExpect(jsonPath("$.estado").value("NUEVO"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldCreateAvisoWithOptionalFechaProgramada() throws Exception {
        // Arrange
        DireccionOutput direccion = new DireccionOutput("Calle Test", "456", "Barcelona", "Barcelona", "08001");
        LocalDateTime fechaProgramada = LocalDateTime.of(2026, 4, 20, 10, 0);
        AvisoOutput output = new AvisoOutput(
            2L, "AVI-2026-0002", 2L, null, null, null, "Test with date",
            "MEDIA", "NUEVO", direccion, LocalDateTime.now(), fechaProgramada, null, null, null, List.of()
        );
        when(createAvisoUseCase.execute(any())).thenReturn(output);

        // Act & Assert
        mockMvc.perform(post("/api/avisos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "clienteId": 2,
                        "descripcion": "Test with date",
                        "prioridad": "MEDIA",
                        "calle": "Calle Test",
                        "numero": "456",
                        "localidad": "Barcelona",
                        "provincia": "Barcelona",
                        "codigoPostal": "08001",
                        "fechaProgramada": "2026-04-20T10:00:00"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.numeroCorrelativo").value("AVI-2026-0002"));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldListAvisos() throws Exception {
        // Arrange
        DireccionOutput direccion = new DireccionOutput("Calle Falsa", "123", "Madrid", "Madrid", "28001");
        AvisoOutput aviso = new AvisoOutput(
            1L, "AVI-2026-0001", 1L, null, null, null, "Test description",
            "ALTA", "NUEVO", direccion, LocalDateTime.now(), null, null, null, null, List.of()
        );
        PaginatedResponse<AvisoOutput> response = new PaginatedResponse<>(
            List.of(aviso), 1L, 1, 0, 20
        );
        when(listAvisosUseCase.execute(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/avisos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalElements").value(1))
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.currentPage").value(0))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldListAvisosWithPagination() throws Exception {
        // Arrange
        PaginatedResponse<AvisoOutput> response = new PaginatedResponse<>(
            List.of(), 0L, 0, 0, 20
        );
        when(listAvisosUseCase.execute(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/avisos")
                .param("page", "1")
                .param("size", "10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.currentPage").value(1))
            .andExpect(jsonPath("$.pageSize").value(10));
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldListAvisosWithFilters() throws Exception {
        // Arrange
        PaginatedResponse<AvisoOutput> response = new PaginatedResponse<>(
            List.of(), 0L, 0, 0, 20
        );
        when(listAvisosUseCase.execute(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/avisos")
                .param("estado", "NUEVO")
                .param("prioridad", "ALTA"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
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
    @WithMockUser(roles = "TECNICO")
    void shouldAllowTecnicoToList() throws Exception {
        // Arrange
        PaginatedResponse<AvisoOutput> response = new PaginatedResponse<>(
            List.of(), 0L, 0, 0, 20
        );
        when(listAvisosUseCase.execute(any())).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/avisos"))
            .andExpect(status().isOk());
    }

    @Test
    void shouldDenyAccessWhenNotAuthenticated() throws Exception {
        // When not authenticated, access should be denied
        mockMvc.perform(get("/api/avisos"))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldRejectInvalidCreateRequest() throws Exception {
        // Act & Assert - missing required fields
        mockMvc.perform(post("/api/avisos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMINISTRADOR")
    void shouldRejectInvalidPrioridad() throws Exception {
        // Act & Assert - invalid prioridad value
        mockMvc.perform(post("/api/avisos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "clienteId": 1,
                        "descripcion": "Test",
                        "prioridad": "INVALID",
                        "calle": "C",
                        "numero": "1",
                        "localidad": "L",
                        "provincia": "P",
                        "codigoPostal": "12345"
                    }
                    """))
            .andExpect(status().isBadRequest());
    }
}