package com.dows.bitacora2.restaurante.controller.docs;

import com.dows.bitacora2.restaurante.model.dto.response.PlatoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Tag(name = "Menu", description = "Consulta de la carta - vista del cliente")
public interface MenuApi {

    @Operation(summary = "Ver carta del restaurante")
    @ApiResponse(responseCode = "200", description = "Platos disponibles")
    ResponseEntity<List<PlatoResponseDTO>> verCarta();

    @Operation(summary = "Ver detalle de un plato")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Plato encontrado"),
        @ApiResponse(responseCode = "404", description = "Plato no existe o no disponible")
    })
    ResponseEntity<PlatoResponseDTO> verDetalle(Long id);

    @Operation(summary = "Ver carta filtrada por categoria")
    @ApiResponse(responseCode = "200", description = "Platos de la categoria")
    ResponseEntity<List<PlatoResponseDTO>> porCategoria(String categoria);
}
