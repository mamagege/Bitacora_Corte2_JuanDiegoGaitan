package com.dows.bitacora2.restaurante.model.domain;

public enum EstadoPedido {
    RECIBIDO, EN_PREPARACION, LISTO, ENTREGADO, CANCELADO;

    public boolean puedeTransicionarA(EstadoPedido siguiente) {
        return switch (this) {
            case RECIBIDO       -> siguiente == EN_PREPARACION || siguiente == CANCELADO;
            case EN_PREPARACION -> siguiente == LISTO;
            case LISTO          -> siguiente == ENTREGADO;
            default             -> false;
        };
    }

    public boolean esCancelable() {
        return this == RECIBIDO;
    }
}
