package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.exception.EstadoInvalidoException;
import com.dows.bitacora2.restaurante.exception.RecursoNoEncontradoException;
import com.dows.bitacora2.restaurante.exception.ReglaDeNegocioException;
import com.dows.bitacora2.restaurante.mapper.MesaEntityMapper;
import com.dows.bitacora2.restaurante.model.domain.EstadoMesa;
import com.dows.bitacora2.restaurante.model.domain.Mesa;
import com.dows.bitacora2.restaurante.persistence.entity.MesaEntity;
import com.dows.bitacora2.restaurante.repository.MesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class MesaServiceImpl implements IMesaService {
    private static final Logger log = LoggerFactory.getLogger(MesaServiceImpl.class);

    private final MesaRepository mesaRepository;
    private final MesaEntityMapper entityMapper;

    public MesaServiceImpl(MesaRepository mesaRepository, MesaEntityMapper entityMapper) {
        this.mesaRepository = mesaRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public List<Mesa> obtenerTodas() {
        log.info("Obteniendo todas las mesas");
        return entityMapper.toDomainList(mesaRepository.findAll());
    }

    @Override
    public List<Mesa> obtenerDisponibles() {
        return mesaRepository.findAll().stream()
                .map(entityMapper::toDomain)
                .filter(Mesa::estaDisponible)
                .toList();
    }

    @Override
    public Mesa obtenerPorId(Long id) {
        return mesaRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> {
                    log.warn("Mesa no encontrada: id={}", id);
                    return new RecursoNoEncontradoException("Mesa", id);
                });
    }

    @Override
    @Transactional
    public Mesa crear(Mesa mesa) {
        boolean existeNumero = mesaRepository.findAll().stream()
                .anyMatch(m -> m.getNumero().equals(mesa.getNumero()));
        if (existeNumero) {
            throw new ReglaDeNegocioException("Ya existe una mesa con el número " + mesa.getNumero());
        }
        mesa.setEstado(EstadoMesa.DISPONIBLE);
        mesa.setCuentaAbierta(false);
        MesaEntity nueva = mesaRepository.save(entityMapper.toEntity(mesa));
        log.info("Mesa creada: id={}, numero={}", nueva.getId(), nueva.getNumero());
        return entityMapper.toDomain(nueva);
    }

    @Override
    @Transactional
    public Mesa actualizar(Long id, Mesa mesaModificada) {
        MesaEntity existente = mesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mesa", id));
        
        boolean existeOtroNumero = mesaRepository.findAll().stream()
                .anyMatch(m -> !m.getId().equals(id) && m.getNumero().equals(mesaModificada.getNumero()));
        if (existeOtroNumero) {
            throw new ReglaDeNegocioException("Ya existe otra mesa con el número " + mesaModificada.getNumero());
        }

        existente.setNumero(mesaModificada.getNumero());
        existente.setCapacidad(mesaModificada.getCapacidad());
        log.info("Mesa actualizada: id={}", id);
        return entityMapper.toDomain(mesaRepository.save(existente));
    }

    @Override
    @Transactional
    public Mesa cambiarEstado(Long id, String estadoStr) {
        MesaEntity mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mesa", id));
        try {
            EstadoMesa nuevoEstado = EstadoMesa.valueOf(estadoStr.toUpperCase());
            mesa.setEstado(nuevoEstado);
            log.info("Estado de mesa id={} cambiado a {}", id, nuevoEstado);
            return entityMapper.toDomain(mesaRepository.save(mesa));
        } catch (IllegalArgumentException e) {
            throw new EstadoInvalidoException("Estado de mesa inválido: " + estadoStr);
        }
    }

    @Override
    @Transactional
    public Mesa abrirCuenta(Long id) {
        MesaEntity mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mesa", id));
        if (Boolean.TRUE.equals(mesa.getCuentaAbierta())) {
            throw new ReglaDeNegocioException("RN-06: La mesa ya tiene una cuenta abierta. Solo puede haber una cuenta abierta a la vez.");
        }
        mesa.setCuentaAbierta(true);
        mesa.setEstado(EstadoMesa.OCUPADA);
        log.info("Cuenta abierta para mesa id={}", id);
        return entityMapper.toDomain(mesaRepository.save(mesa));
    }

    @Override
    @Transactional
    public Mesa cerrarCuenta(Long id) {
        MesaEntity mesa = mesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Mesa", id));
        if (!Boolean.TRUE.equals(mesa.getCuentaAbierta())) {
            throw new ReglaDeNegocioException("La mesa no tiene una cuenta abierta para cerrar.");
        }
        mesa.setCuentaAbierta(false);
        mesa.setEstado(EstadoMesa.DISPONIBLE);
        log.info("Cuenta cerrada para mesa id={}", id);
        return entityMapper.toDomain(mesaRepository.save(mesa));
    }
}
