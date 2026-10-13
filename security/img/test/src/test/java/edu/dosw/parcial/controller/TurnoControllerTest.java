package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.CreateTurnoRequest;
import edu.dosw.parcial.controller.dtos.response.TurnoResponse;
import edu.dosw.parcial.controller.mappers.TurnoDtoMapper;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.core.services.TurnoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TurnoControllerTest {

    @Mock
    private TurnoService turnoService;

    private TurnoDtoMapper turnoDtoMapper;
    private TurnoController turnoController;

    @BeforeEach
    void setUp() {
        turnoDtoMapper = new TurnoDtoMapper();
        turnoController = new TurnoController(turnoService, turnoDtoMapper);
    }

    @Test
    @DisplayName("createTurno: debe retornar 201 Created con el TurnoResponse generado")
    void testCreateTurnoEndpoint() {
        CreateTurnoRequest request = CreateTurnoRequest.builder()
                .clientName("Carlos Ruiz")
                .clientPhone("3208872165")
                .clientEmail("carlos@gmail.com")
                .serviceCode("SER-002")
                .timeSlot("Martes 10:00 AM")
                .paymentMethod("NEQUI")
                .amount(38000)
                .build();

        Turno mockCreatedTurno = Turno.builder()
                .id("mongo-1")
                .turnoId("TURNO #001")
                .serviceCode("SER-002")
                .serviceName("Corte + barba")
                .timeSlot("Martes 10:00 AM")
                .assignedBarber("Miguel")
                .amount(38000)
                .status("CONFIRMADO")
                .build();

        when(turnoService.createTurno(any(Turno.class))).thenReturn(mockCreatedTurno);

        ResponseEntity<TurnoResponse> response = turnoController.createTurno(request);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("TURNO #001", response.getBody().getTurnoId());
        assertEquals("CONFIRMADO", response.getBody().getStatus());
        assertEquals("Miguel", response.getBody().getAssignedBarber());
    }

    @Test
    @DisplayName("getAllTurnos: debe retornar 200 OK con la lista de turnos")
    void testGetAllTurnosEndpoint() {
        Turno mockTurno = Turno.builder().id("1").turnoId("TURNO #001").status("CONFIRMADO").build();
        when(turnoService.getAllTurnos()).thenReturn(List.of(mockTurno));

        ResponseEntity<List<TurnoResponse>> response = turnoController.getAllTurnos();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
