package com.dows.bitacora2.restaurante.exception;

public class TransicionEstadoInvalidaException extends ReglaDeNegocioException {
    public TransicionEstadoInvalidaException(String mensaje) {
        super(mensaje);
    }
}
