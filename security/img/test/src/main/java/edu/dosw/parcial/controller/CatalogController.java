package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.response.BarberResponse;
import edu.dosw.parcial.controller.dtos.response.ServiceResponse;
import edu.dosw.parcial.controller.mappers.CatalogDtoMapper;
import edu.dosw.parcial.core.services.CatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para consultar el catálogo de servicios activos y los barberos de la barbería.
 */
@RestController
@RequestMapping("/api/v1/catalog")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Endpoints para consulta de servicios y disponibilidad de barberos")
public class CatalogController {

    private final CatalogService catalogService;
    private final CatalogDtoMapper catalogDtoMapper;

    @Operation(summary = "Listar los servicios disponibles en el catálogo activo")
    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponse>> getServices() {
        List<ServiceResponse> list = catalogService.getServices().stream()
                .map(catalogDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Listar los barberos del equipo y sus especialidades/horarios")
    @GetMapping("/barbers")
    public ResponseEntity<List<BarberResponse>> getBarbers() {
        List<BarberResponse> list = catalogService.getBarbers().stream()
                .map(catalogDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
