package edu.dosw.parcial.core.services.adapters;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Fábrica y registro extensible de adaptadores de pasarela de pago (OCP / DIP).
 * Permite incorporar nuevos métodos de pago sin alterar el código del validador.
 */
@Component
public class PaymentAdapterFactory {

    private final Map<String, Supplier<InterfacePaymentMethod>> registry = new ConcurrentHashMap<>();

    public PaymentAdapterFactory() {
        registerAdapter("NEQUI", NequiAdapter::new);
        registerAdapter("PSE", PseAdapter::new);
        registerAdapter("STRIPE", StripeAdapter::new);
        registerAdapter("EFECTIVO", EfectivoAdapter::new);
    }

    public void registerAdapter(String method, Supplier<InterfacePaymentMethod> supplier) {
        registry.put(method.toUpperCase(), supplier);
    }

    public boolean isSupported(String method) {
        if (method == null) {
            return false;
        }
        return registry.containsKey(method.toUpperCase());
    }

    public InterfacePaymentMethod getAdapter(String method) {
        if (method == null) {
            return null;
        }
        Supplier<InterfacePaymentMethod> supplier = registry.get(method.toUpperCase());
        return supplier != null ? supplier.get() : null;
    }

    public Set<String> getSupportedMethods() {
        return Collections.unmodifiableSet(registry.keySet());
    }
}
