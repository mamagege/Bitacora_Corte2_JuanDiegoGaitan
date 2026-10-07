package com.dows.bitacora2.restaurante.validator;

import com.dows.bitacora2.restaurante.exception.ConflictoException;
import com.dows.bitacora2.restaurante.repository.PlatoRepository;
import org.springframework.stereotype.Component;

@Component
@lombok.RequiredArgsConstructor
public class PlatoValidator implements IPlatoValidator {

    private final PlatoRepository platoRepository;

    @Override
    public void validarNombreUnico(String nombre) {
        if (platoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictoException("Ya existe un plato con el nombre '" + nombre + "'");
        }
    }

    @Override
    public void validarNombreUnicoExcluyendo(String nombre, Long id) {
        if (platoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictoException("Ya existe otro plato con el nombre '" + nombre + "'");
        }
    }
}
