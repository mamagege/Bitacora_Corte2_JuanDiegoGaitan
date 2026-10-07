package com.dows.bitacora2.restaurante.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TextoUtilsTest {

    @Test
    @DisplayName("normalizarNombre - quita espacios extra y pone Title Case")
    void normalizarNombre_quitaEspacios_poneTitleCase() {
        assertEquals("Bandeja paisa", TextoUtils.normalizarNombre("   bAndejA    PAISA   "));
    }

    @Test
    @DisplayName("normalizarNombre - null retorna null")
    void normalizarNombre_null_retornaNull() {
        assertNull(TextoUtils.normalizarNombre(null));
    }

    @Test
    @DisplayName("sonIgualesNormalizados - compara ignorando case y espacios")
    void sonIgualesNormalizados_diferenteCase_retornaTrue() {
        assertTrue(TextoUtils.sonIgualesNormalizados("  Ajiaco  ", "aJiaCO"));
    }
}
