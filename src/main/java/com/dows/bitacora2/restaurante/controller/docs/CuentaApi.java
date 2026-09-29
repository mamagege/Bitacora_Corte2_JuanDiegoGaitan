package com.dows.bitacora2.restaurante.controller.docs;

import com.dows.bitacora2.restaurante.model.dto.request.CuentaRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.CuentaResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Tag(name = "Cuentas", description = "Administración de cuentas y pagos del restaurante")
public interface CuentaApi {
    @Operation(summary = "Listar todas las cuentas")
    @ApiResponse(responseCode = "200", description = "Lista de cuentas")
    ResponseEntity<List<CuentaResponseDTO>> listar();

    @Operation(summary = "Obtener cuenta por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
        @ApiResponse(responseCode = "404", description = "Cuenta no existe")
    })
    ResponseEntity<CuentaResponseDTO> obtener(Long id);

    @Operation(summary = "Crear/abrir una nueva cuenta")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Cuenta creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "409", description = "Mesa ya tiene cuenta abierta")
    })
    ResponseEntity<CuentaResponseDTO> crear(CuentaRequestDTO dto);

    @Operation(summary = "Actualizar total de la cuenta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Total actualizado"),
        @ApiResponse(responseCode = "400", description = "La cuenta ya está pagada"),
        @ApiResponse(responseCode = "404", description = "Cuenta no existe")
    })
    ResponseEntity<CuentaResponseDTO> actualizarTotal(Long id);

    @Operation(summary = "Pagar cuenta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cuenta pagada"),
        @ApiResponse(responseCode = "400", description = "La cuenta ya estaba pagada"),
        @ApiResponse(responseCode = "404", description = "Cuenta no existe")
    })
    ResponseEntity<CuentaResponseDTO> pagar(Long id);
}
