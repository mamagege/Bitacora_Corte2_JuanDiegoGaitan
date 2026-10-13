package edu.dosw.parcial.core.services.adapters;

import edu.dosw.parcial.core.models.PagoResultado;

import java.util.Map;
import java.util.Random;

/**
 * Adaptador para pasarela de pagos PSE (SDK bancario legado).
 * Reglas de negocio:
 * - Banco BANCOLOMBIA: PENDING (código 10, exclusivo Bancolombia).
 * - Primer dígito de la cuenta en {1, 2, 3}: APROBADO (código 0).
 * - Primer dígito en 4 o cualquier otro: RECHAZADO (código 1).
 */
public class PseAdapter implements InterfacePaymentMethod {
    private static final Random RANDOM = new Random();

    @Override
    public PagoResultado processPayment(String payerName, double amount, Map<String, String> metadata) {
        String bank = metadata != null ? metadata.getOrDefault("bank", "").toUpperCase() : "";
        String accountNumber = metadata != null ? metadata.getOrDefault("accountNumber", "0") : "0";
        int firstDigit = accountNumber.isEmpty() ? 0 : Character.getNumericValue(accountNumber.charAt(0));

        String status;
        String message;

        if ("BANCOLOMBIA".equals(bank)) {
            status = "PENDING";
            message = "Transacción PSE en proceso (Bancolombia - código 10)";
        } else if (firstDigit >= 1 && firstDigit <= 3) {
            status = "APROBADO";
            message = "Transacción PSE aprobada (código 0)";
        } else {
            status = "RECHAZADO";
            message = "Transacción PSE rechazada (código 1)";
        }

        String paymentId = "PSE-" + (1000 + RANDOM.nextInt(9000));
        return new PagoResultado(paymentId, status, message);
    }
}
