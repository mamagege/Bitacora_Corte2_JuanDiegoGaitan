package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.core.models.Servicio;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.core.services.ServiceCatalog;
import lombok.extern.slf4j.Slf4j;

/**
 * Validación 4 de la cadena: Servicio permitido.
 * Criterio de rechazo: Sólo los servicios del catálogo activo pueden reservarse.
 */
@Slf4j
public class CatalogValidationHandler extends ValidationHandler {

    private final ServiceCatalog serviceCatalog;

    public CatalogValidationHandler(ServiceCatalog serviceCatalog) {
        this.serviceCatalog = serviceCatalog;
    }

    @Override
    public ValidationResult handle(Turno turno) {
        String serviceCode = turno.getServiceCode();

        if (serviceCode == null || !serviceCatalog.isValid(serviceCode)) {
            log.warn("[RECHAZADO] Servicio no existe en el catálogo activo: {}", serviceCode);
            return ValidationResult.failure("Servicio no existe en el catálogo activo");
        }

        Servicio servicio = serviceCatalog.get(serviceCode);
        turno.setServiceName(servicio.getName());
        if (turno.getAmount() <= 0) {
            turno.setAmount(servicio.getPrice());
        }

        log.info("[OK] Servicio en catálogo: {} ({})", servicio.getName(), servicio.getCode());
        return proceed(turno);
    }
}
