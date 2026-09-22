package com.dows.bitacora2.restaurante.validator;

import com.dows.bitacora2.restaurante.model.domain.Plato;
import java.util.Collection;

public interface IPlatoValidator {
    void validarNombreUnico(String nombre, Collection<Plato> platosExistentes);
}
