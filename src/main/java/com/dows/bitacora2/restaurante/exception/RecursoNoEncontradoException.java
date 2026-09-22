package com.dows.bitacora2.restaurante.exception;

public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String recurso, Long id) {
        super("No existe " + recurso + " con id=" + id);
    }
}
