package edu.dosw.parcial.core.services.adapters;

import edu.dosw.parcial.core.models.PagoResultado;

import java.util.Map;
import java.util.Random;

/**
 * Adaptador para pago en efectivo en caja (sin integración digital).
 * Regla de negocio:
 * - Monto >= 10.000: APROBADO.
 * - Monto < 10.000: RECHAZADO (Monto insuficiente).
 */
public class EfectivoAdapter implements InterfacePaymentMethod {
    private static final Random RANDOM = new Random();

    @Override
    public PagoResultado processPayment(String payerName, double amount, Map<String, String> metadata) {
        boolean aprobado = amount >= 10000.0;

        String paymentId = "EFE-" + (1000 + RANDOM.nextInt(9000));
        String status = aprobado ? "APROBADO" : "RECHAZADO";
        String message = aprobado ? "Pago en efectivo recibido y aprobado" : "Monto insuficiente en efectivo (mínimo $10.000)";

        return new PagoResultado(paymentId, status, message);
    }
}
