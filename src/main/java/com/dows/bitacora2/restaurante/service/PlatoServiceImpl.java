package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.exception.RecursoNoEncontradoException;
import com.dows.bitacora2.restaurante.exception.ConflictoException;
import com.dows.bitacora2.restaurante.mapper.PlatoEntityMapper;
import com.dows.bitacora2.restaurante.model.domain.Plato;
import com.dows.bitacora2.restaurante.persistence.entity.PlatoEntity;
import com.dows.bitacora2.restaurante.repository.PlatoRepository;
import com.dows.bitacora2.restaurante.validator.IPlatoValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class PlatoServiceImpl implements IPlatoService {
    

    private final PlatoRepository platoRepository;
    private final PlatoEntityMapper entityMapper;
    private final IPlatoValidator validator;

    @Override
    public List<Plato> obtenerTodos() {
        List<PlatoEntity> entities = platoRepository.findAll();
        log.info("Obteniendo todos los platos. Total: {}", entities.size());
        return entityMapper.toDomainList(entities);
    }

    @Override
    public List<Plato> obtenerDisponibles() {
        return entityMapper.toDomainList(platoRepository.findByDisponibleTrue());
    }

    @Override
    public List<Plato> obtenerPorCategoria(String categoria) {
        return entityMapper.toDomainList(platoRepository.findByCategoriaIgnoreCase(categoria));
    }

    @Override
    public Plato obtenerPorId(Long id) {
        log.debug("Buscando plato con id={}", id);
        return platoRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> {
                    log.error("Plato con id={} no encontrado", id);
                    return new RecursoNoEncontradoException("Plato", id);
                });
    }

    @Override
    @Transactional
    public Plato crear(Plato plato) {
        log.info("Creando plato con nombre '{}'", plato.getNombre());
        validator.validarNombreUnico(plato.getNombre());
        PlatoEntity guardado = platoRepository.save(entityMapper.toEntity(plato));
        log.info("Plato '{}' creado con id={}", guardado.getNombre(), guardado.getId());
        return entityMapper.toDomain(guardado);
    }

    @Override
    @Transactional
    public Plato actualizar(Long id, Plato nuevosDatos) {
        PlatoEntity existente = platoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Plato", id));
        validator.validarNombreUnicoExcluyendo(nuevosDatos.getNombre(), id);
        existente.setNombre(nuevosDatos.getNombre());
        existente.setPrecio(nuevosDatos.getPrecio());
        existente.setCategoria(nuevosDatos.getCategoria());
        existente.setDescripcion(nuevosDatos.getDescripcion());
        log.info("Plato actualizado: id={}", id);
        return entityMapper.toDomain(platoRepository.save(existente));
    }

    @Override
    @Transactional
    public Plato cambiarDisponibilidad(Long id, boolean disponible) {
        PlatoEntity plato = platoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Plato", id));
        plato.setDisponible(disponible);
        log.info("Plato id={} â†’ disponible={}", id, disponible);
        return entityMapper.toDomain(platoRepository.save(plato));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!platoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Plato", id);
        }
        platoRepository.deleteById(id);
        log.info("Plato eliminado: id={}", id);
    }
}


