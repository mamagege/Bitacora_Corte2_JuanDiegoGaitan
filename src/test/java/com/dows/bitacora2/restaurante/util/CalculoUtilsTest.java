package com.dows.bitacora2.restaurante.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculoUtilsTest {

    @Test
    @DisplayName("redondear - valor con muchos decimales queda en 2")
    void redondear_valorConDecimaes_quedaEnDos() {
        assertEquals(12.35, CalculoUtils.redondear(12.3456789));
    }

    @Test
    @DisplayName("aplicarDescuento - 10% de descuento sobre 10000 = 9000")
    void aplicarDescuento_10Porciento_retornaValorCorrecto() {
        assertEquals(9000.0, CalculoUtils.aplicarDescuento(10000.0, 10.0));
    }
}
