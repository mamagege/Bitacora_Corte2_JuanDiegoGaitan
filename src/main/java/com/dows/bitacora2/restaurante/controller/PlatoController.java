package com.dows.bitacora2.restaurante.controller;

import com.dows.bitacora2.restaurante.controller.docs.PlatoApi;
import com.dows.bitacora2.restaurante.mapper.PlatoMapper;
import com.dows.bitacora2.restaurante.model.domain.Plato;
import com.dows.bitacora2.restaurante.model.dto.request.PlatoRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.PlatoResponseDTO;
import com.dows.bitacora2.restaurante.service.IPlatoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/platos")
public class PlatoController implements PlatoApi {

    public PlatoController(IPlatoService platoService, PlatoMapper platoMapper) { this.platoService = platoService; this.platoMapper = platoMapper; }

    private final IPlatoService platoService;
    private final PlatoMapper   platoMapper;

    @Override @GetMapping
    public ResponseEntity<List<PlatoResponseDTO>> listar() {
        return ResponseEntity.ok(
            platoService.obtenerTodos().stream()
                .map(platoMapper::toResponse).toList());
    }

    @Override @GetMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(platoMapper.toResponse(platoService.obtenerPorId(id)));
    }

    @Override @PostMapping
    public ResponseEntity<PlatoResponseDTO> crear(@RequestBody @Valid PlatoRequestDTO dto) {
        Plato creado = platoService.crear(platoMapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(platoMapper.toResponse(creado));
    }

    @Override @PutMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> actualizar(@PathVariable Long id, @RequestBody @Valid PlatoRequestDTO dto) {
        Plato actualizado = platoService.actualizar(id, platoMapper.toDomain(dto));
        return ResponseEntity.ok(platoMapper.toResponse(actualizado));
    }

    @Override @PatchMapping("/{id}/disponible")
    public ResponseEntity<PlatoResponseDTO> cambiarDisponibilidad(@PathVariable Long id, @RequestParam boolean disponible) {
        return ResponseEntity.ok(platoMapper.toResponse(platoService.cambiarDisponibilidad(id, disponible)));
    }

    @Override @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        platoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
