package com.dows.bitacora2.restaurante.controller.docs;

import com.dows.bitacora2.restaurante.model.dto.request.PlatoRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.PlatoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Tag(name = "Platos", description = "Administración de la carta del restaurante")
public interface PlatoApi {
    @Operation(summary = "Listar todos los platos")
    @ApiResponse(responseCode = "200", description = "Lista de platos")
    ResponseEntity<List<PlatoResponseDTO>> listar();

    @Operation(summary = "Obtener plato por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Plato encontrado"),
        @ApiResponse(responseCode = "404", description = "Plato no existe")
    })
    ResponseEntity<PlatoResponseDTO> obtener(Long id);

    @Operation(summary = "Crear un nuevo plato")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Plato creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    ResponseEntity<PlatoResponseDTO> crear(PlatoRequestDTO dto);

    @Operation(summary = "Actualizar plato completo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Plato actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Plato no existe"),
        @ApiResponse(responseCode = "409", description = "Nombre duplicado")
    })
    ResponseEntity<PlatoResponseDTO> actualizar(Long id, PlatoRequestDTO dto);

    @Operation(summary = "Activar o desactivar un plato")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Disponibilidad actualizada"),
        @ApiResponse(responseCode = "404", description = "Plato no existe")
    })
    ResponseEntity<PlatoResponseDTO> cambiarDisponibilidad(Long id, boolean disponible);

    @Operation(summary = "Eliminar un plato")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Plato eliminado"),
        @ApiResponse(responseCode = "404", description = "Plato no existe")
    })
    ResponseEntity<Void> eliminar(Long id);
}
