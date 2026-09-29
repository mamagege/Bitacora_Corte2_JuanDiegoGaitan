package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.model.domain.Pedido;
import java.util.List;

public interface IPedidoService {
    List<Pedido> obtenerTodos();
    Pedido obtenerPorId(Long id);
    Pedido crear(Pedido pedido);
    Pedido actualizar(Long id, Pedido pedido);
    Pedido cambiarEstado(Long id, String estado);
    void eliminar(Long id);
}
