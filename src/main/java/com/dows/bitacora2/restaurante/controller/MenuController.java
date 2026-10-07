package com.dows.bitacora2.restaurante.controller;

import com.dows.bitacora2.restaurante.controller.docs.MenuApi;
import com.dows.bitacora2.restaurante.exception.RecursoNoEncontradoException;
import com.dows.bitacora2.restaurante.mapper.PlatoMapper;
import com.dows.bitacora2.restaurante.model.domain.Plato;
import com.dows.bitacora2.restaurante.model.dto.response.PlatoResponseDTO;
import com.dows.bitacora2.restaurante.service.IPlatoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/menu")
@lombok.RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class MenuController implements MenuApi {

    private final IPlatoService platoService;
    private final PlatoMapper   platoMapper;

    @Override
    @GetMapping
    public ResponseEntity<List<PlatoResponseDTO>> verCarta() {
        log.info("GET /api/v1/menu");
        return ResponseEntity.ok(
            platoService.obtenerDisponibles().stream()
                .map(platoMapper::toResponse)
                .toList()
        );
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PlatoResponseDTO> verDetalle(@PathVariable Long id) {
        log.info("GET /api/v1/menu/{}", id);
        Plato plato = platoService.obtenerPorId(id);
        if (!plato.estaDisponible()) {
            throw new RecursoNoEncontradoException("Plato disponible", id);
        }
        return ResponseEntity.ok(platoMapper.toResponse(plato));
    }

    @Override
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<PlatoResponseDTO>> porCategoria(@PathVariable String categoria) {
        log.info("GET /api/v1/menu/categoria/{}", categoria);
        return ResponseEntity.ok(
            platoService.obtenerPorCategoria(categoria).stream()
                .filter(Plato::estaDisponible)
                .map(platoMapper::toResponse)
                .toList()
        );
    }
}
