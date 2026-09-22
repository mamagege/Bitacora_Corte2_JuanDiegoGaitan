package com.dows.bitacora2.restaurante.validator;

import com.dows.bitacora2.restaurante.exception.ConflictoException;
import com.dows.bitacora2.restaurante.model.domain.Plato;
import org.springframework.stereotype.Component;
import java.util.Collection;

@Component
public class PlatoValidator implements IPlatoValidator {
    @Override
    public void validarNombreUnico(String nombre, Collection<Plato> platosExistentes) {
        boolean existe = platosExistentes.stream()
                .anyMatch(p -> p.getNombre().equalsIgnoreCase(nombre.strip()));
        if (existe) {
            throw new ConflictoException("Ya existe un plato con el nombre: " + nombre);
        }
    }
}
