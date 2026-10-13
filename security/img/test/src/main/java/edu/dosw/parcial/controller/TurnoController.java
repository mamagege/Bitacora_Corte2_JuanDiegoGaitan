package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.CreateTurnoRequest;
import edu.dosw.parcial.controller.dtos.response.TurnoResponse;
import edu.dosw.parcial.controller.mappers.TurnoDtoMapper;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.core.services.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión y procesamiento de turnos en Bob's Barbery.
 * Ruta protegida: requiere token JWT Bearer.
 */
@RestController
@RequestMapping("/api/v1/turnos")
@RequiredArgsConstructor
@Tag(name = "Turnos", description = "Endpoints para creación y consulta de turnos con validaciones y pagos")
@SecurityRequirement(name = "BearerAuth")
public class TurnoController {

    private final TurnoService turnoService;
    private final TurnoDtoMapper turnoDtoMapper;

    @Operation(summary = "Crear y procesar un nuevo turno",
               description = "Valida el turno a través de la cadena de responsabilidad (franja, barbero, cliente, catálogo, pago) y lo persiste en MongoDB.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Turno creado y confirmado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "422", description = "Turno rechazado por regla de negocio o pago declinado"),
            @ApiResponse(responseCode = "401", description = "No autenticado (Token JWT ausente o inválido)")
    })
    @PostMapping
    public ResponseEntity<TurnoResponse> createTurno(@Valid @RequestBody CreateTurnoRequest request) {
        Turno domain = turnoDtoMapper.toDomain(request);
        Turno created = turnoService.createTurno(domain);
        TurnoResponse response = turnoDtoMapper.toResponse(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Listar todos los turnos registrados")
    @GetMapping
    public ResponseEntity<List<TurnoResponse>> getAllTurnos() {
        List<TurnoResponse> list = turnoService.getAllTurnos().stream()
                .map(turnoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Consultar turno por ID de base de datos o identificador de turno")
    @GetMapping("/{id}")
    public ResponseEntity<TurnoResponse> getTurnoById(@PathVariable String id) {
        Turno turno;
        if (id.startsWith("TURNO")) {
            turno = turnoService.getTurnoByTurnoId(id);
        } else {
            turno = turnoService.getTurnoById(id);
        }
        return ResponseEntity.ok(turnoDtoMapper.toResponse(turno));
    }
}
