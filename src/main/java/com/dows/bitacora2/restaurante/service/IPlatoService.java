package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.model.domain.Plato;
import java.util.List;

public interface IPlatoService {
    List<Plato>  obtenerTodos();
    List<Plato>  obtenerDisponibles();
    List<Plato>  obtenerPorCategoria(String categoria);
    Plato        obtenerPorId(Long id);
    Plato        crear(Plato plato);
    Plato        actualizar(Long id, Plato plato);
    Plato        cambiarDisponibilidad(Long id, boolean disponible);
    void         eliminar(Long id);
}
