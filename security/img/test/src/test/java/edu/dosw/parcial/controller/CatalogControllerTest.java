package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.response.BarberResponse;
import edu.dosw.parcial.controller.dtos.response.ServiceResponse;
import edu.dosw.parcial.controller.mappers.CatalogDtoMapper;
import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.models.Servicio;
import edu.dosw.parcial.core.services.CatalogService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogControllerTest {

    @Mock
    private CatalogService catalogService;

    private CatalogDtoMapper catalogDtoMapper;
    private CatalogController catalogController;

    @BeforeEach
    void setUp() {
        catalogDtoMapper = new CatalogDtoMapper();
        catalogController = new CatalogController(catalogService, catalogDtoMapper);
    }

    @Test
    @DisplayName("getServices: debe retornar lista de servicios disponibles")
    void testGetServices() {
        Servicio s1 = new Servicio("SER-001", "Corte clásico", 25000, "CORTE");
        when(catalogService.getServices()).thenReturn(List.of(s1));

        ResponseEntity<List<ServiceResponse>> response = catalogController.getServices();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("SER-001", response.getBody().get(0).getCode());
    }

    @Test
    @DisplayName("getBarbers: debe retornar lista de barberos disponibles")
    void testGetBarbers() {
        Barbero b1 = new Barbero("Miguel", List.of("CORTE"), List.of("Lunes"), 10, 17);
        when(catalogService.getBarbers()).thenReturn(List.of(b1));

        ResponseEntity<List<BarberResponse>> response = catalogController.getBarbers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("Miguel", response.getBody().get(0).getName());
    }
}
