package com.serviflow.aviso.domain.entity;

import com.serviflow.aviso.domain.valueobject.AvisoId;
import com.serviflow.aviso.domain.valueobject.DireccionServicio;
import com.serviflow.aviso.domain.valueobject.EstadoAviso;
import com.serviflow.aviso.domain.valueobject.NumeroCorrelativo;
import com.serviflow.aviso.domain.valueobject.Prioridad;
import com.serviflow.shared.domain.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Aviso domain entity.
 */
class AvisoTest {

    private static final NumeroCorrelativo CORRELATIVO = NumeroCorrelativo.generate(2026, 1);
    private static final DireccionServicio DIRECCION = new DireccionServicio(
        "Calle Principal", "123", "Madrid", "Madrid", "28001"
    );

    @Nested
    @DisplayName("Factory Methods")
    class FactoryMethods {

        @Test
        @DisplayName("create should create aviso with estado NUEVO")
        void create_shouldCreateAvisoWithEstadoNuevo() {
            LocalDateTime fechaProgramada = LocalDateTime.now().plusDays(1);
            Aviso aviso = Aviso.create(1L, CORRELATIVO, "Descripción del servicio", 
                                       Prioridad.MEDIA, DIRECCION, fechaProgramada);

            assertEquals(EstadoAviso.NUEVO, aviso.estado());
            assertEquals(1L, aviso.clienteId());
            assertEquals("Descripción del servicio", aviso.descripcion());
            assertEquals(Prioridad.MEDIA, aviso.prioridad());
            assertEquals(DIRECCION, aviso.direccionServicio());
            assertNotNull(aviso.fechaCreacion());
            assertFalse(aviso.isAssigned());
            assertFalse(aviso.isTerminal());
        }

        @Test
        @DisplayName("reconstitute should create aviso with all fields from persistence")
        void reconstitute_shouldCreateAvisoFromPersistence() {
            LocalDateTime now = LocalDateTime.now();
            AvisoId id = new AvisoId(1L);

            Aviso aviso = Aviso.reconstitute(
                id,
                1L,
                CORRELATIVO,
                "Descripción",
                Prioridad.ALTA,
                EstadoAviso.EN_CURSO,
                DIRECCION,
                now,
                now.plusDays(1),
                2L,
                now,
                null,
                List.of()
            );

            assertEquals(id, aviso.id());
            assertEquals(EstadoAviso.EN_CURSO, aviso.estado());
            assertEquals(2L, aviso.tecnicoId());
            assertNotNull(aviso.fechaInicio());
            assertNull(aviso.fechaFin());
        }
    }

    @Nested
    @DisplayName("Validation")
    class Validation {

        @Test
        @DisplayName("should reject null clienteId")
        void shouldRejectNullClienteId() {
            assertThrows(NullPointerException.class, () ->
                Aviso.create(null, CORRELATIVO, "Desc", Prioridad.BAJA, DIRECCION, null)
            );
        }

        @Test
        @DisplayName("should reject null correlativo")
        void shouldRejectNullCorrelativo() {
            assertThrows(NullPointerException.class, () ->
                Aviso.create(1L, null, "Desc", Prioridad.BAJA, DIRECCION, null)
            );
        }

        @Test
        @DisplayName("should reject null descripcion")
        void shouldRejectNullDescripcion() {
            assertThrows(NullPointerException.class, () ->
                Aviso.create(1L, CORRELATIVO, null, Prioridad.BAJA, DIRECCION, null)
            );
        }

        @Test
        @DisplayName("should reject blank descripcion")
        void shouldRejectBlankDescripcion() {
            assertThrows(DomainException.class, () ->
                Aviso.create(1L, CORRELATIVO, "   ", Prioridad.BAJA, DIRECCION, null)
            );
        }

        @Test
        @DisplayName("should reject descripcion exceeding 2000 characters")
        void shouldRejectDescripcionExceeding2000Characters() {
            String longDesc = "A".repeat(2001);
            assertThrows(DomainException.class, () ->
                Aviso.create(1L, CORRELATIVO, longDesc, Prioridad.BAJA, DIRECCION, null)
            );
        }

        @Test
        @DisplayName("should reject null prioridad")
        void shouldRejectNullPrioridad() {
            assertThrows(NullPointerException.class, () ->
                Aviso.create(1L, CORRELATIVO, "Desc", null, DIRECCION, null)
            );
        }

        @Test
        @DisplayName("should reject null direccion")
        void shouldRejectNullDireccion() {
            assertThrows(NullPointerException.class, () ->
                Aviso.create(1L, CORRELATIVO, "Desc", Prioridad.BAJA, null, null)
            );
        }

        @Test
        @DisplayName("should trim descripcion")
        void shouldTrimDescripcion() {
            Aviso aviso = Aviso.create(1L, CORRELATIVO, "  Descripción  ", Prioridad.BAJA, DIRECCION, null);

            assertEquals("Descripción", aviso.descripcion());
        }
    }

    @Nested
    @DisplayName("State Transitions")
    class StateTransitions {

        @Test
        @DisplayName("assignTecnico should transition from NUEVO to ASIGNADO")
        void assignTecnico_shouldTransitionNuevoToAsignado() {
            Aviso aviso = createBasicAviso();
            aviso.assignTecnico(2L, "admin");

            assertEquals(EstadoAviso.ASIGNADO, aviso.estado());
            assertEquals(2L, aviso.tecnicoId());
            assertTrue(aviso.isAssigned());
            assertEquals(1, aviso.observaciones().size());
            assertTrue(aviso.observaciones().get(0).contenido().contains("ASIGNADO"));
        }

        @Test
        @DisplayName("assignTecnico should throw from invalid state")
        void assignTecnico_shouldThrowFromInvalidState() {
            Aviso aviso = createBasicAviso();
            aviso.assignTecnico(2L, "admin");

            assertThrows(DomainException.class, () ->
                aviso.assignTecnico(3L, "admin")
            );
        }

        @Test
        @DisplayName("startWork should transition from ASIGNADO to EN_CURSO")
        void startWork_shouldTransitionAsignadoToEnCurso() {
            Aviso aviso = createBasicAviso();
            aviso.assignTecnico(2L, "admin");
            aviso.startWork("tecnico");

            assertEquals(EstadoAviso.EN_CURSO, aviso.estado());
            assertNotNull(aviso.fechaInicio());
            assertEquals(2, aviso.observaciones().size());
        }

        @Test
        @DisplayName("startWork should throw from invalid state")
        void startWork_shouldThrowFromInvalidState() {
            Aviso aviso = createBasicAviso();

            assertThrows(DomainException.class, () ->
                aviso.startWork("tecnico")
            );
        }

        @Test
        @DisplayName("completeWork should transition from EN_CURSO to COMPLETADO")
        void completeWork_shouldTransitionEnCursoToCompletado() {
            Aviso aviso = createBasicAviso();
            aviso.assignTecnico(2L, "admin");
            aviso.startWork("tecnico");
            aviso.completeWork("tecnico");

            assertEquals(EstadoAviso.COMPLETADO, aviso.estado());
            assertNotNull(aviso.fechaFin());
            assertTrue(aviso.isTerminal());
        }

        @Test
        @DisplayName("completeWork should throw from invalid state")
        void completeWork_shouldThrowFromInvalidState() {
            Aviso aviso = createBasicAviso();

            assertThrows(DomainException.class, () ->
                aviso.completeWork("tecnico")
            );
        }

        @Test
        @DisplayName("pendingFollowUp should transition from EN_CURSO to PENDIENTE_SEGUIMIENTO")
        void pendingFollowUp_shouldTransitionEnCursoToPendienteSeguimiento() {
            Aviso aviso = createBasicAviso();
            aviso.assignTecnico(2L, "admin");
            aviso.startWork("tecnico");
            aviso.pendingFollowUp("tecnico");

            assertEquals(EstadoAviso.PENDIENTE_SEGUIMIENTO, aviso.estado());
            assertFalse(aviso.isTerminal());
        }

        @Test
        @DisplayName("cancel should transition from NUEVO to CANCELADO")
        void cancel_shouldTransitionNuevoToCancelado() {
            Aviso aviso = createBasicAviso();
            aviso.cancel("admin");

            assertEquals(EstadoAviso.CANCELADO, aviso.estado());
            assertTrue(aviso.isTerminal());
        }

        @Test
        @DisplayName("cancel should throw from COMPLETADO state")
        void cancel_shouldThrowFromCompletado() {
            Aviso aviso = createBasicAviso();
            aviso.assignTecnico(2L, "admin");
            aviso.startWork("tecnico");
            aviso.completeWork("tecnico");

            assertThrows(DomainException.class, () ->
                aviso.cancel("admin")
            );
        }

        @Test
        @DisplayName("cancel should throw if already cancelled")
        void cancel_shouldThrowIfAlreadyCancelled() {
            Aviso aviso = createBasicAviso();
            aviso.cancel("admin");

            assertThrows(DomainException.class, () ->
                aviso.cancel("admin")
            );
        }

        @Test
        @DisplayName("full transition NUEVO -> ASIGNADO -> EN_CURSO -> COMPLETADO")
        void fullTransition_shouldWork() {
            Aviso aviso = createBasicAviso();

            assertEquals(EstadoAviso.NUEVO, aviso.estado());

            aviso.assignTecnico(2L, "admin");
            assertEquals(EstadoAviso.ASIGNADO, aviso.estado());

            aviso.startWork("tecnico");
            assertEquals(EstadoAviso.EN_CURSO, aviso.estado());

            aviso.completeWork("tecnico");
            assertEquals(EstadoAviso.COMPLETADO, aviso.estado());
            assertTrue(aviso.isTerminal());
        }

        @Test
        @DisplayName("loop EN_CURSO -> PENDIENTE_SEGUIMIENTO -> ASIGNADO -> EN_CURSO")
        void loopTransition_shouldWork() {
            Aviso aviso = createBasicAviso();
            aviso.assignTecnico(2L, "admin");
            aviso.startWork("tecnico");

            assertEquals(EstadoAviso.EN_CURSO, aviso.estado());

            aviso.pendingFollowUp("tecnico");
            assertEquals(EstadoAviso.PENDIENTE_SEGUIMIENTO, aviso.estado());

            aviso.assignTecnico(3L, "admin");
            assertEquals(EstadoAviso.ASIGNADO, aviso.estado());

            aviso.startWork("tecnico");
            assertEquals(EstadoAviso.EN_CURSO, aviso.estado());
        }
    }

    @Nested
    @DisplayName("Reprogramar")
    class Reprogramar {

        @Test
        @DisplayName("reprogramar should update fechaProgramada")
        void reprogramar_shouldUpdateFechaProgramada() {
            Aviso aviso = createBasicAviso();
            LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(5);

            aviso.reprogramar(nuevaFecha, null, "admin");

            assertEquals(nuevaFecha, aviso.fechaProgramada());
            assertEquals(1, aviso.observaciones().size());
        }

        @Test
        @DisplayName("reprogramar should update tecnicoId when different")
        void reprogramar_shouldUpdateTecnicoId() {
            Aviso aviso = createBasicAviso();
            aviso.assignTecnico(2L, "admin");
            LocalDateTime nuevaFecha = LocalDateTime.now().plusDays(5);

            aviso.reprogramar(nuevaFecha, 3L, "admin");

            assertEquals(3L, aviso.tecnicoId());
            // Second observation (index 1) is the reprogramacion, first one (index 0) is the assignment
            assertTrue(aviso.observaciones().get(1).contenido().contains("técnico="));
        }

        @Test
        @DisplayName("reprogramar should throw on terminal state")
        void reprogramar_shouldThrowOnTerminalState() {
            Aviso aviso = createBasicAviso();
            aviso.assignTecnico(2L, "admin");
            aviso.startWork("tecnico");
            aviso.completeWork("tecnico");

            assertThrows(DomainException.class, () ->
                aviso.reprogramar(LocalDateTime.now().plusDays(5), null, "admin")
            );
        }
    }

    @Nested
    @DisplayName("Observaciones")
    class Observaciones {

        @Test
        @DisplayName("addObservacion should add observation")
        void addObservacion_shouldAddObservation() {
            Aviso aviso = createBasicAviso();

            aviso.addObservacion("Test observation", "OBSERVACION", "admin");

            assertEquals(1, aviso.observaciones().size());
            assertEquals("Test observation", aviso.observaciones().get(0).contenido());
        }

        @Test
        @DisplayName("should reject null contenido")
        void shouldRejectNullContenido() {
            Aviso aviso = createBasicAviso();

            assertThrows(NullPointerException.class, () ->
                aviso.addObservacion(null, "OBSERVACION", "admin")
            );
        }

        @Test
        @DisplayName("should reject null tipo")
        void shouldRejectNullTipo() {
            Aviso aviso = createBasicAviso();

            assertThrows(NullPointerException.class, () ->
                aviso.addObservacion("content", null, "admin")
            );
        }

        @Test
        @DisplayName("should reject null usuario")
        void shouldRejectNullUsuario() {
            Aviso aviso = createBasicAviso();

            assertThrows(NullPointerException.class, () ->
                aviso.addObservacion("content", "OBSERVACION", null)
            );
        }

        @Test
        @DisplayName("observaciones should be immutable")
        void observaciones_shouldBeImmutable() {
            Aviso aviso = createBasicAviso();
            aviso.addObservacion("Obs1", "OBSERVACION", "admin");

            // Try to add to the unmodifiable list - should throw
            assertThrows(UnsupportedOperationException.class, () ->
                aviso.observaciones().add(Observacion.reconstitute(1L, new AvisoId(1L), "test", "OBSERVACION", "user", LocalDateTime.now()))
            );
        }
    }

    @Nested
    @DisplayName("Query Methods")
    class QueryMethods {

        @Test
        @DisplayName("isAssigned should return true when tecnicoId is set")
        void isAssigned_shouldReturnTrueWhenTecnicoSet() {
            Aviso aviso = createBasicAviso();
            assertFalse(aviso.isAssigned());

            aviso.assignTecnico(2L, "admin");
            assertTrue(aviso.isAssigned());
        }

        @Test
        @DisplayName("isTerminal should return true for COMPLETADO")
        void isTerminal_shouldReturnTrueForCompletado() {
            Aviso aviso = createBasicAviso();
            assertFalse(aviso.isTerminal());

            aviso.assignTecnico(2L, "admin");
            aviso.startWork("tecnico");
            aviso.completeWork("tecnico");

            assertTrue(aviso.isTerminal());
        }

        @Test
        @DisplayName("isTerminal should return true for CANCELADO")
        void isTerminal_shouldReturnTrueForCancelado() {
            Aviso aviso = createBasicAviso();
            aviso.cancel("admin");

            assertTrue(aviso.isTerminal());
        }
    }

    private Aviso createBasicAviso() {
        // Use reconstitute with a valid ID so that observations can be added
        return Aviso.reconstitute(
            new AvisoId(1L),
            1L,
            CORRELATIVO,
            "Descripción de prueba",
            Prioridad.MEDIA,
            EstadoAviso.NUEVO,
            DIRECCION,
            LocalDateTime.now(),
            LocalDateTime.now().plusDays(1),
            null,
            null,
            null,
            new java.util.ArrayList<>()
        );
    }
}
