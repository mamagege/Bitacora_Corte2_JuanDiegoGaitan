package edu.dosw.parcial.core.services.adapters;

import edu.dosw.parcial.core.models.PagoResultado;

import java.util.Map;
import java.util.Random;

/**
 * Adaptador para pasarela de pagos Nequi.
 * Regla de negocio: si el teléfono termina en "65", el pago es exitoso (APROBADO);
 * en cualquier otro caso se rechaza.
 */
public class NequiAdapter implements InterfacePaymentMethod {
    private static final Random RANDOM = new Random();

    @Override
    public PagoResultado processPayment(String payerName, double amount, Map<String, String> metadata) {
        String phone = metadata != null ? metadata.getOrDefault("phone", "") : "";
        boolean successful = phone.endsWith("65");

        String paymentId = "NQ-" + (1000 + RANDOM.nextInt(9000));
        String status = successful ? "APROBADO" : "RECHAZADO";
        String message = successful ? "Pago Nequi exitoso" : "Fallo en validación Nequi (número no termina en 65)";

        return new PagoResultado(paymentId, status, message);
    }
}
