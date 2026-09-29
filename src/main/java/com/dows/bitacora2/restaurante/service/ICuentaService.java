package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.model.domain.Cuenta;
import java.util.List;

public interface ICuentaService {
    List<Cuenta> obtenerTodas();
    Cuenta obtenerPorId(Long id);
    Cuenta crear(Cuenta cuenta);
    Cuenta actualizarTotal(Long id);
    Cuenta pagar(Long id);
    Cuenta asociarPedido(Long idCuenta, Long idPedido);
}
