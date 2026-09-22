package com.dows.bitacora2.restaurante.exception;

public class MesaNoDisponibleException extends ReglaDeNegocioException {
    public MesaNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
