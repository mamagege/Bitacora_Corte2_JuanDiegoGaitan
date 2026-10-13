package edu.dosw.parcial.core.services.adapters;

import edu.dosw.parcial.core.models.PagoResultado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PaymentAdaptersTest {

    @Test
    @DisplayName("NequiAdapter: debe aprobar si el teléfono termina en 65 y rechazar en caso contrario")
    void testNequiAdapter() {
        NequiAdapter adapter = new NequiAdapter();

        PagoResultado aprobado = adapter.processPayment("Carlos", 38000, Map.of("phone", "3208872165"));
        assertNotNull(aprobado);
        assertEquals("APROBADO", aprobado.getStatus());
        assertTrue(aprobado.getPaymentId().startsWith("NQ-"));

        PagoResultado rechazado = adapter.processPayment("Carlos", 38000, Map.of("phone", "3208872199"));
        assertNotNull(rechazado);
        assertEquals("RECHAZADO", rechazado.getStatus());
    }

    @Test
    @DisplayName("PseAdapter: debe retornar PENDING para Bancolombia, APROBADO para 1-3 y RECHAZADO para otros")
    void testPseAdapter() {
        PseAdapter adapter = new PseAdapter();

        // Caso Bancolombia -> PENDING
        PagoResultado bancolombia = adapter.processPayment("Julián", 45000,
                Map.of("bank", "BANCOLOMBIA", "accountNumber", "123456789"));
        assertEquals("PENDING", bancolombia.getStatus());
        assertTrue(bancolombia.getPaymentId().startsWith("PSE-"));

        // Caso Banco Bogotá cuenta iniciando en 2 -> APROBADO
        PagoResultado aprobado = adapter.processPayment("Julián", 45000,
                Map.of("bank", "DAVIVIENDA", "accountNumber", "234567890"));
        assertEquals("APROBADO", aprobado.getStatus());

        // Caso Banco Bogotá cuenta iniciando en 4 -> RECHAZADO
        PagoResultado rechazado = adapter.processPayment("Julián", 45000,
                Map.of("bank", "DAVIVIENDA", "accountNumber", "434567890"));
        assertEquals("RECHAZADO", rechazado.getStatus());
    }

    @Test
    @DisplayName("StripeAdapter: debe aprobar si CVV es 555 y rechazar para cualquier otro CVV")
    void testStripeAdapter() {
        StripeAdapter adapter = new StripeAdapter();

        PagoResultado aprobado = adapter.processPayment("Laura", 25000, Map.of("cvv", "555"));
        assertEquals("APROBADO", aprobado.getStatus());
        assertTrue(aprobado.getPaymentId().startsWith("STR-"));

        PagoResultado rechazado = adapter.processPayment("Laura", 25000, Map.of("cvv", "123"));
        assertEquals("RECHAZADO", rechazado.getStatus());
    }

    @Test
    @DisplayName("EfectivoAdapter: debe aprobar si monto >= 10000 y rechazar si monto < 10000")
    void testEfectivoAdapter() {
        EfectivoAdapter adapter = new EfectivoAdapter();

        PagoResultado aprobado = adapter.processPayment("Pedro", 15000, Map.of());
        assertEquals("APROBADO", aprobado.getStatus());
        assertTrue(aprobado.getPaymentId().startsWith("EFE-"));

        PagoResultado rechazado = adapter.processPayment("Pedro", 5000, Map.of());
        assertEquals("RECHAZADO", rechazado.getStatus());
    }

    @Test
    @DisplayName("PaymentAdapterFactory: debe registrar y resolver todos los adaptadores soportados")
    void testPaymentAdapterFactory() {
        PaymentAdapterFactory factory = new PaymentAdapterFactory();

        assertTrue(factory.isSupported("NEQUI"));
        assertTrue(factory.isSupported("PSE"));
        assertTrue(factory.isSupported("STRIPE"));
        assertTrue(factory.isSupported("EFECTIVO"));
        assertFalse(factory.isSupported("BITCOIN"));

        assertNotNull(factory.getAdapter("NEQUI"));
        assertNotNull(factory.getAdapter("PSE"));
        assertNull(factory.getAdapter("UNKNOWN"));
    }
}
