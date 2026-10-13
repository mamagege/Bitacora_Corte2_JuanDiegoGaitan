package edu.dosw.parcial.core.services.adapters;

import edu.dosw.parcial.core.models.PagoResultado;

import java.util.Map;

/**
 * Contrato interno normalizado para el procesamiento de pagos mediante el patrón Adapter.
 */
public interface InterfacePaymentMethod {
    PagoResultado processPayment(String payerName, double amount, Map<String, String> metadata);
}
