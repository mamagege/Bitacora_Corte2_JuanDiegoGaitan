package com.dows.bitacora2.restaurante.validator;

public interface IPlatoValidator {
    void validarNombreUnico(String nombre);
    void validarNombreUnicoExcluyendo(String nombre, Long id);
}
