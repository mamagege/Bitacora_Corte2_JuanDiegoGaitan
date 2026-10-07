package com.dows.bitacora2.restaurante.controller;

import com.dows.bitacora2.restaurante.controller.docs.PedidoApi;
import com.dows.bitacora2.restaurante.mapper.PedidoMapper;
import com.dows.bitacora2.restaurante.model.domain.Pedido;
import com.dows.bitacora2.restaurante.model.dto.request.PedidoRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.PedidoResponseDTO;
import com.dows.bitacora2.restaurante.service.IPedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pedidos")
@RequiredArgsConstructor
public class PedidoController implements PedidoApi {

    private final IPedidoService pedidoService;
    private final PedidoMapper pedidoMapper;

    @Override
    @GetMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('MESERO', 'COCINA', 'ADMIN', 'CLIENTE')")
    public ResponseEntity<List<PedidoResponseDTO>> listar() {
        List<Pedido> pedidos = pedidoService.obtenerTodos();
        return ResponseEntity.ok(pedidoMapper.toResponseList(pedidos));
    }

    @Override
    @GetMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('MESERO', 'COCINA', 'ADMIN', 'CLIENTE')")
    public ResponseEntity<PedidoResponseDTO> obtener(@PathVariable Long id) {
        Pedido pedido = pedidoService.obtenerPorId(id);
        return ResponseEntity.ok(pedidoMapper.toResponse(pedido));
    }

    @Override
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('CLIENTE', 'MESERO')")
    public ResponseEntity<PedidoResponseDTO> crear(@Valid @RequestBody PedidoRequestDTO dto) {
        Pedido nuevo = pedidoMapper.toDomain(dto);
        Pedido creado = pedidoService.crear(nuevo);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoMapper.toResponse(creado));
    }

    @Override
    @PutMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('CLIENTE', 'MESERO')")
    public ResponseEntity<PedidoResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody PedidoRequestDTO dto) {
        Pedido modificado = pedidoMapper.toDomain(dto);
        Pedido actualizado = pedidoService.actualizar(id, modificado);
        return ResponseEntity.ok(pedidoMapper.toResponse(actualizado));
    }

    @Override
    @PatchMapping("/{id}/estado")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('COCINA', 'MESERO')")
    public ResponseEntity<PedidoResponseDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        Pedido actualizado = pedidoService.cambiarEstado(id, estado);
        return ResponseEntity.ok(pedidoMapper.toResponse(actualizado));
    }

    @Override
    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasAnyRole('MESERO', 'ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        pedidoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/resumen")
    public ResponseEntity<com.dows.bitacora2.restaurante.model.dto.response.ResumenDiaDTO> resumenDelDia() {
        return ResponseEntity.ok(pedidoService.resumenDelDia());
    }
}