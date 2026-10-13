package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.core.models.PagoResultado;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.core.services.adapters.InterfacePaymentMethod;
import edu.dosw.parcial.core.services.adapters.PaymentAdapterFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * Validación 5 de la cadena: Pasarela de pago.
 * Criterio de rechazo: La pasarela seleccionada debe estar activa y disponible,
 * y la transacción procesada no debe resultar RECHAZADA.
 */
@Slf4j
public class PaymentValidationHandler extends ValidationHandler {

    private final PaymentAdapterFactory paymentAdapterFactory;

    public PaymentValidationHandler(PaymentAdapterFactory paymentAdapterFactory) {
        this.paymentAdapterFactory = paymentAdapterFactory;
    }

    @Override
    public ValidationResult handle(Turno turno) {
        String method = turno.getPaymentMethod();

        if (method == null || !paymentAdapterFactory.isSupported(method)) {
            log.warn("[RECHAZADO] Pasarela de pago inactiva o no soportada: {}", method);
            return ValidationResult.failure("Pasarela de pago inactiva o no soportada: " + method);
        }

        InterfacePaymentMethod adapter = paymentAdapterFactory.getAdapter(method);
        String payerName = turno.getClient() != null ? turno.getClient().getName() : "Cliente";

        log.info("[OK] Pasarela {} activa. Procesando pago de ${}...", method, turno.getAmount());
        PagoResultado pagoResultado = adapter.processPayment(payerName, turno.getAmount(), turno.getPaymentMetadata());
        turno.setPaymentResult(pagoResultado);

        log.info("PAGO: {} - {}", pagoResultado.getPaymentId(), pagoResultado.getStatus());

        if ("RECHAZADO".equalsIgnoreCase(pagoResultado.getStatus())) {
            log.warn("[RECHAZADO] Pago no aprobado: {}", pagoResultado.getMessage());
            ValidationResult res = ValidationResult.failure("Pago rechazado: " + pagoResultado.getMessage());
            res.setPaymentResult(pagoResultado);
            return res;
        }

        ValidationResult res = proceed(turno);
        if (res.isValid()) {
            res.setPaymentResult(pagoResultado);
            res.setAssignedBarber(turno.getAssignedBarber());
        }
        return res;
    }
}
