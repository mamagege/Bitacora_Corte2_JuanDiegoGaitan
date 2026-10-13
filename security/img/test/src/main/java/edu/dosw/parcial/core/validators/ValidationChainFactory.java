package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.core.services.BarberRegistry;
import edu.dosw.parcial.core.services.ServiceCatalog;
import edu.dosw.parcial.core.services.adapters.PaymentAdapterFactory;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Fábrica para ensamblar la cadena secuencial de validación del turno (Chain of Responsibility).
 * Orden estricto según especificación:
 * 1. Disponibilidad de franja
 * 2. Barbero disponible
 * 3. Datos del cliente
 * 4. Servicio permitido
 * 5. Pasarela de pago
 */
@Component
public class ValidationChainFactory {

    private final BarberRegistry barberRegistry;
    private final ServiceCatalog serviceCatalog;
    private final PaymentAdapterFactory paymentAdapterFactory;

    public ValidationChainFactory(BarberRegistry barberRegistry,
                                  ServiceCatalog serviceCatalog,
                                  PaymentAdapterFactory paymentAdapterFactory) {
        this.barberRegistry = barberRegistry;
        this.serviceCatalog = serviceCatalog;
        this.paymentAdapterFactory = paymentAdapterFactory;
    }

    public ValidationHandler buildChain(Function<String, Long> occupancyProvider) {
        ValidationHandler timeHandler = new TimeAvailableHandler(occupancyProvider);
        ValidationHandler barberHandler = new BarberAvailableHandler(barberRegistry, serviceCatalog);
        ValidationHandler clientHandler = new ClientValidationHandler();
        ValidationHandler catalogHandler = new CatalogValidationHandler(serviceCatalog);
        ValidationHandler paymentHandler = new PaymentValidationHandler(paymentAdapterFactory);

        timeHandler.setNext(barberHandler)
                .setNext(clientHandler)
                .setNext(catalogHandler)
                .setNext(paymentHandler);

        return timeHandler;
    }
}
