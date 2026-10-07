package com.dows.bitacora2.restaurante.util;

public final class CalculoUtils {
    
    private CalculoUtils() {}

    public static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    public static double aplicarDescuento(double total, double porcentaje) {
        return redondear(total * (1 - porcentaje / 100));
    }
}
