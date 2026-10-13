package edu.dosw.parcial.core.services.adapters;

import edu.dosw.parcial.core.models.PagoResultado;

import java.util.Map;
import java.util.Random;

/**
 * Adaptador para pasarela de pagos Stripe (Tarjeta de crédito / débito).
 * Regla de negocio:
 * - CVV == "555": Cargo exitoso (APROBADO).
 * - En cualquier otro caso: RECHAZADO.
 */
public class StripeAdapter implements InterfacePaymentMethod {
    private static final Random RANDOM = new Random();

    @Override
    public PagoResultado processPayment(String payerName, double amount, Map<String, String> metadata) {
        String cvv = metadata != null ? metadata.getOrDefault("cvv", "") : "";
        boolean paid = "555".equals(cvv);

        String paymentId = "STR-" + (1000 + RANDOM.nextInt(9000));
        String status = paid ? "APROBADO" : "RECHAZADO";
        String message = paid ? "Cargo Stripe exitoso" : "Cargo Stripe rechazado (CVV inválido)";

        return new PagoResultado(paymentId, status, message);
    }
}
