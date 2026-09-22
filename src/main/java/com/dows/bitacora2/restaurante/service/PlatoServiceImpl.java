package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.exception.RecursoNoEncontradoException;
import com.dows.bitacora2.restaurante.model.domain.Plato;
import com.dows.bitacora2.restaurante.validator.IPlatoValidator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PlatoServiceImpl implements IPlatoService {
    private static final Logger log = LoggerFactory.getLogger(PlatoServiceImpl.class);

    public PlatoServiceImpl(IPlatoValidator validator) { this.validator = validator; }

    private final Map<Long, Plato> platos  = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(1);
    private final IPlatoValidator validator;

    @Override
    public List<Plato> obtenerTodos() {
        log.info("Obteniendo todos los platos. Total: {}", platos.size());
        return platos.values().stream().toList();
    }

    @Override
    public List<Plato> obtenerDisponibles() {
        return platos.values().stream().filter(Plato::estaDisponible).toList();
    }

    @Override
    public List<Plato> obtenerPorCategoria(String categoria) {
        return platos.values().stream()
                .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
                .toList();
    }

    @Override
    public Plato obtenerPorId(Long id) {
        return platos.values().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Plato no encontrado: id={}", id);
                    return new RecursoNoEncontradoException("Plato", id);
                });
    }

    @Override
    public Plato crear(Plato plato) {
        validator.validarNombreUnico(plato.getNombre(), platos.values());
        plato.setId(contador.getAndIncrement());
        platos.put(plato.getId(), plato);
        log.info("Plato creado: id={}, nombre={}", plato.getId(), plato.getNombre());
        return plato;
    }

    @Override
    public Plato actualizar(Long id, Plato nuevosDatos) {
        Plato existente = obtenerPorId(id);
        validator.validarNombreUnico(nuevosDatos.getNombre(),
            platos.values().stream()
                    .filter(p -> !p.getId().equals(id))
                    .toList());
        existente.setNombre(nuevosDatos.getNombre());
        existente.setPrecio(nuevosDatos.getPrecio());
        existente.setCategoria(nuevosDatos.getCategoria());
        existente.setDescripcion(nuevosDatos.getDescripcion());
        log.info("Plato actualizado: id={}", id);
        return existente;
    }

    @Override
    public Plato cambiarDisponibilidad(Long id, boolean disponible) {
        Plato plato = obtenerPorId(id);
        if (disponible) plato.activar(); else plato.desactivar();
        log.info("Plato id={} → disponible={}", id, disponible);
        return plato;
    }

    @Override
    public void eliminar(Long id) {
        obtenerPorId(id);
        platos.remove(id);
        log.info("Plato eliminado: id={}", id);
    }
}
