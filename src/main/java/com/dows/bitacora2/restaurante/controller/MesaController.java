package com.dows.bitacora2.restaurante.controller;

import com.dows.bitacora2.restaurante.controller.docs.MesaApi;
import com.dows.bitacora2.restaurante.mapper.MesaMapper;
import com.dows.bitacora2.restaurante.model.domain.Mesa;
import com.dows.bitacora2.restaurante.model.dto.request.MesaRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.MesaResponseDTO;
import com.dows.bitacora2.restaurante.service.IMesaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mesas")
public class MesaController implements MesaApi {

    private final IMesaService mesaService;
    private final MesaMapper mesaMapper;

    public MesaController(IMesaService mesaService, MesaMapper mesaMapper) {
        this.mesaService = mesaService;
        this.mesaMapper = mesaMapper;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<MesaResponseDTO>> listar() {
        List<Mesa> mesas = mesaService.obtenerTodas();
        return ResponseEntity.ok(mesaMapper.toResponseList(mesas));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> obtener(@PathVariable Long id) {
        Mesa mesa = mesaService.obtenerPorId(id);
        return ResponseEntity.ok(mesaMapper.toResponse(mesa));
    }

    @Override
    @PostMapping
    public ResponseEntity<MesaResponseDTO> crear(@Valid @RequestBody MesaRequestDTO dto) {
        Mesa nueva = mesaMapper.toDomain(dto);
        Mesa creada = mesaService.crear(nueva);
        return ResponseEntity.status(HttpStatus.CREATED).body(mesaMapper.toResponse(creada));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<MesaResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody MesaRequestDTO dto) {
        Mesa modificada = mesaMapper.toDomain(dto);
        Mesa actualizada = mesaService.actualizar(id, modificada);
        return ResponseEntity.ok(mesaMapper.toResponse(actualizada));
    }

    @Override
    @PatchMapping("/{id}/estado")
    public ResponseEntity<MesaResponseDTO> cambiarEstado(@PathVariable Long id, @RequestParam String estado) {
        Mesa actualizada = mesaService.cambiarEstado(id, estado);
        return ResponseEntity.ok(mesaMapper.toResponse(actualizada));
    }

    @Override
    @PatchMapping("/{id}/abrir-cuenta")
    public ResponseEntity<MesaResponseDTO> abrirCuenta(@PathVariable Long id) {
        Mesa actualizada = mesaService.abrirCuenta(id);
        return ResponseEntity.ok(mesaMapper.toResponse(actualizada));
    }

    @Override
    @PatchMapping("/{id}/cerrar-cuenta")
    public ResponseEntity<MesaResponseDTO> cerrarCuenta(@PathVariable Long id) {
        Mesa actualizada = mesaService.cerrarCuenta(id);
        return ResponseEntity.ok(mesaMapper.toResponse(actualizada));
    }
}
