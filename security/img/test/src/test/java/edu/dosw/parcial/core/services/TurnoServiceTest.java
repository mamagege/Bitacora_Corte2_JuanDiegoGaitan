package edu.dosw.parcial.core.services;

import edu.dosw.parcial.core.models.Cliente;
import edu.dosw.parcial.core.models.PagoResultado;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.core.models.TurnoValidationException;
import edu.dosw.parcial.core.validators.ValidationChainFactory;
import edu.dosw.parcial.core.validators.ValidationHandler;
import edu.dosw.parcial.core.validators.ValidationResult;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.mappers.TurnoEntityMapper;
import edu.dosw.parcial.persistence.repositories.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private TurnoEntityMapper turnoEntityMapper;

    @Mock
    private ValidationChainFactory validationChainFactory;

    @Mock
    private ValidationHandler validationChain;

    private TurnoService turnoService;

    @BeforeEach
    void setUp() {
        turnoService = new TurnoService(turnoRepository, turnoEntityMapper, validationChainFactory);
    }

    @Test
    @DisplayName("createTurno: debe confirmar y persistir un turno cuando la cadena es exitosa")
    void testCreateTurnoExitoso() {
        Turno inputTurno = Turno.builder()
                .client(new Cliente("Carlos Ruiz", "3208872165", "carlos@gmail.com"))
                .serviceCode("SER-002")
                .timeSlot("Martes 10:00 AM")
                .paymentMethod("NEQUI")
                .build();

        ValidationResult successResult = ValidationResult.builder()
                .valid(true)
                .assignedBarber("Miguel")
                .paymentResult(new PagoResultado("NQ-1001", "APROBADO", "Pago exitoso"))
                .build();

        when(validationChainFactory.buildChain(any())).thenReturn(validationChain);
        when(validationChain.handle(any(Turno.class))).thenReturn(successResult);

        TurnoDocument mockDoc = TurnoDocument.builder().id("mongo-id-123").status("CONFIRMADO").build();
        when(turnoEntityMapper.toDocument(any(Turno.class))).thenReturn(mockDoc);
        when(turnoRepository.save(any(TurnoDocument.class))).thenReturn(mockDoc);

        Turno mockDomain = Turno.builder().id("mongo-id-123").turnoId("TURNO #001").status("CONFIRMADO").assignedBarber("Miguel").amount(38000).build();
        when(turnoEntityMapper.toDomain(mockDoc)).thenReturn(mockDomain);

        Turno result = turnoService.createTurno(inputTurno);

        assertNotNull(result);
        assertEquals("CONFIRMADO", result.getStatus());
        assertEquals("Miguel", result.getAssignedBarber());
        verify(turnoRepository, times(1)).save(mockDoc);
    }

    @Test
    @DisplayName("createTurno: debe lanzar TurnoValidationException cuando la cadena rechaza la solicitud")
    void testCreateTurnoRechazado() {
        Turno inputTurno = Turno.builder()
                .client(new Cliente("Ana Lopez", "312345", "ana@gmail.com"))
                .serviceCode("SER-005")
                .timeSlot("Lunes 10:00 AM")
                .paymentMethod("PSE")
                .build();

        ValidationResult failureResult = ValidationResult.failure("Error: teléfono debe tener 10 dígitos");

        when(validationChainFactory.buildChain(any())).thenReturn(validationChain);
        when(validationChain.handle(any(Turno.class))).thenReturn(failureResult);

        assertThrows(TurnoValidationException.class, () -> turnoService.createTurno(inputTurno));
    }

    @Test
    @DisplayName("getAllTurnos: debe mapear y retornar lista de turnos")
    void testGetAllTurnos() {
        TurnoDocument doc1 = TurnoDocument.builder().id("1").turnoId("TURNO #001").build();
        when(turnoRepository.findAll()).thenReturn(List.of(doc1));
        when(turnoEntityMapper.toDomain(doc1)).thenReturn(Turno.builder().id("1").turnoId("TURNO #001").build());

        List<Turno> result = turnoService.getAllTurnos();

        assertEquals(1, result.size());
        assertEquals("TURNO #001", result.get(0).getTurnoId());
    }

    @Test
    @DisplayName("getTurnoById: debe retornar el turno solicitado")
    void testGetTurnoById() {
        TurnoDocument doc = TurnoDocument.builder().id("123").turnoId("TURNO #001").build();
        when(turnoRepository.findById("123")).thenReturn(Optional.of(doc));
        when(turnoEntityMapper.toDomain(doc)).thenReturn(Turno.builder().id("123").turnoId("TURNO #001").build());

        Turno result = turnoService.getTurnoById("123");

        assertNotNull(result);
        assertEquals("123", result.getId());
    }
}
