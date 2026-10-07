package com.dows.bitacora2.restaurante.controller.docs;

import com.dows.bitacora2.restaurante.model.dto.request.PedidoRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.PedidoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import java.util.List;

@Tag(name = "Pedidos", description = "Administración de pedidos del restaurante")
public interface PedidoApi {
    @Operation(summary = "Listar todos los pedidos")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de pedidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Motivo: Rol insuficiente.")
    })
    ResponseEntity<List<PedidoResponseDTO>> listar();

    @Operation(summary = "Obtener pedido por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Motivo: Rol insuficiente."),
        @ApiResponse(responseCode = "404", description = "Pedido no existe")
    })
    ResponseEntity<PedidoResponseDTO> obtener(Long id);

    @Operation(summary = "Crear un nuevo pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Pedido creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o reglas de negocio no cumplidas"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Motivo: Rol insuficiente.")
    })
    ResponseEntity<PedidoResponseDTO> crear(PedidoRequestDTO dto);

    @Operation(summary = "Actualizar pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pedido actualizado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Motivo: Rol insuficiente."),
        @ApiResponse(responseCode = "404", description = "Pedido no existe"),
        @ApiResponse(responseCode = "409", description = "Pedido no está en estado RECIBIDO")
    })
    ResponseEntity<PedidoResponseDTO> actualizar(Long id, PedidoRequestDTO dto);

    @Operation(summary = "Cambiar estado de un pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado actualizado"),
        @ApiResponse(responseCode = "400", description = "Transición inválida o estado inexistente"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Motivo: Rol insuficiente."),
        @ApiResponse(responseCode = "404", description = "Pedido no existe")
    })
    ResponseEntity<PedidoResponseDTO> cambiarEstado(Long id, String estado);

    @Operation(summary = "Cancelar pedido")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Pedido cancelado"),
        @ApiResponse(responseCode = "400", description = "No se puede cancelar en este estado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado. Motivo: Rol insuficiente."),
        @ApiResponse(responseCode = "404", description = "Pedido no existe")
    })
    ResponseEntity<Void> eliminar(Long id);
}
