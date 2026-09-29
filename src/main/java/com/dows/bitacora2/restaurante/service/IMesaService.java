package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.model.domain.Mesa;
import java.util.List;

public interface IMesaService {
    List<Mesa> obtenerTodas();
    List<Mesa> obtenerDisponibles();
    Mesa obtenerPorId(Long id);
    Mesa crear(Mesa mesa);
    Mesa actualizar(Long id, Mesa mesa);
    Mesa cambiarEstado(Long id, String estado);
    Mesa abrirCuenta(Long id);
    Mesa cerrarCuenta(Long id);
}
