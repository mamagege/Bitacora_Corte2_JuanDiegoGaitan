package com.dows.bitacora2.restaurante.controller;

import com.dows.bitacora2.restaurante.controller.docs.CuentaApi;
import com.dows.bitacora2.restaurante.mapper.CuentaMapper;
import com.dows.bitacora2.restaurante.model.domain.Cuenta;
import com.dows.bitacora2.restaurante.model.dto.request.CuentaRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.CuentaResponseDTO;
import com.dows.bitacora2.restaurante.service.ICuentaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cuentas")
public class CuentaController implements CuentaApi {

    private final ICuentaService cuentaService;
    private final CuentaMapper cuentaMapper;

    public CuentaController(ICuentaService cuentaService, CuentaMapper cuentaMapper) {
        this.cuentaService = cuentaService;
        this.cuentaMapper = cuentaMapper;
    }

    @Override
    @GetMapping
    public ResponseEntity<List<CuentaResponseDTO>> listar() {
        List<Cuenta> cuentas = cuentaService.obtenerTodas();
        return ResponseEntity.ok(cuentaMapper.toResponseList(cuentas));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CuentaResponseDTO> obtener(@PathVariable Long id) {
        Cuenta cuenta = cuentaService.obtenerPorId(id);
        return ResponseEntity.ok(cuentaMapper.toResponse(cuenta));
    }

    @Override
    @PostMapping
    public ResponseEntity<CuentaResponseDTO> crear(@Valid @RequestBody CuentaRequestDTO dto) {
        Cuenta nueva = cuentaMapper.toDomain(dto);
        Cuenta creada = cuentaService.crear(nueva);
        return ResponseEntity.status(HttpStatus.CREATED).body(cuentaMapper.toResponse(creada));
    }

    @Override
    @PatchMapping("/{id}/actualizar-total")
    public ResponseEntity<CuentaResponseDTO> actualizarTotal(@PathVariable Long id) {
        Cuenta actualizada = cuentaService.actualizarTotal(id);
        return ResponseEntity.ok(cuentaMapper.toResponse(actualizada));
    }

    @Override
    @PatchMapping("/{id}/pagar")
    public ResponseEntity<CuentaResponseDTO> pagar(@PathVariable Long id) {
        Cuenta pagada = cuentaService.pagar(id);
        return ResponseEntity.ok(cuentaMapper.toResponse(pagada));
    }
}
