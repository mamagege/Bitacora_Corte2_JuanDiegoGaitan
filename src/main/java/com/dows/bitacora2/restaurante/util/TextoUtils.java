package com.dows.bitacora2.restaurante.util;

public final class TextoUtils {

    private TextoUtils() {}

    public static String normalizarNombre(String nombre) {
        if (nombre == null) return null;
        String limpio = nombre.strip().replaceAll("\\s+", " ");
        if (limpio.isEmpty()) return "";
        return limpio.substring(0, 1).toUpperCase() + limpio.substring(1).toLowerCase();
    }

    public static boolean sonIgualesNormalizados(String a, String b) {
        if (a == null || b == null) return false;
        return a.strip().equalsIgnoreCase(b.strip());
    }
}
