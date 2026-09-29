package com.dows.bitacora2.restaurante.controller.docs;

import com.dows.bitacora2.restaurante.model.dto.request.MesaRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.MesaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Tag(name = "Mesas", description = "Administración de mesas del restaurante")
public interface MesaApi {
    @Operation(summary = "Listar todas las mesas")
    @ApiResponse(responseCode = "200", description = "Lista de mesas")
    ResponseEntity<List<MesaResponseDTO>> listar();

    @Operation(summary = "Obtener mesa por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mesa encontrada"),
        @ApiResponse(responseCode = "404", description = "Mesa no existe")
    })
    ResponseEntity<MesaResponseDTO> obtener(Long id);

    @Operation(summary = "Crear una nueva mesa")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mesa creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Número de mesa duplicado")
    })
    ResponseEntity<MesaResponseDTO> crear(MesaRequestDTO dto);

    @Operation(summary = "Actualizar mesa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mesa actualizada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Mesa no existe"),
        @ApiResponse(responseCode = "409", description = "Número duplicado")
    })
    ResponseEntity<MesaResponseDTO> actualizar(Long id, MesaRequestDTO dto);

    @Operation(summary = "Cambiar estado de una mesa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "400", description = "Estado inválido"),
        @ApiResponse(responseCode = "404", description = "Mesa no existe")
    })
    ResponseEntity<MesaResponseDTO> cambiarEstado(Long id, String estado);

    @Operation(summary = "Abrir cuenta de una mesa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cuenta abierta"),
        @ApiResponse(responseCode = "404", description = "Mesa no existe"),
        @ApiResponse(responseCode = "409", description = "Mesa ya tiene cuenta abierta")
    })
    ResponseEntity<MesaResponseDTO> abrirCuenta(Long id);

    @Operation(summary = "Cerrar cuenta de una mesa")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cuenta cerrada"),
        @ApiResponse(responseCode = "404", description = "Mesa no existe"),
        @ApiResponse(responseCode = "409", description = "Mesa no tiene cuenta abierta")
    })
    ResponseEntity<MesaResponseDTO> cerrarCuenta(Long id);
}
