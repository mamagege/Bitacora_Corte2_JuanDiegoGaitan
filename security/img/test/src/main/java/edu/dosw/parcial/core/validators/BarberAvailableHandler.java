package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.models.Servicio;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.core.services.BarberRegistry;
import edu.dosw.parcial.core.services.ServiceCatalog;
import edu.dosw.parcial.core.utils.TimeSlotUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Validación 2 de la cadena: Barbero disponible.
 * Criterio de rechazo: Si ningún barbero con la especialidad requerida está libre en esa franja, se rechaza.
 * Si está libre, se asigna al turno.
 */
@Slf4j
public class BarberAvailableHandler extends ValidationHandler {

    private final BarberRegistry barberRegistry;
    private final ServiceCatalog serviceCatalog;

    public BarberAvailableHandler(BarberRegistry barberRegistry, ServiceCatalog serviceCatalog) {
        this.barberRegistry = barberRegistry;
        this.serviceCatalog = serviceCatalog;
    }

    @Override
    public ValidationResult handle(Turno turno) {
        String serviceCode = turno.getServiceCode();
        Servicio servicio = serviceCatalog.get(serviceCode);

        if (servicio == null) {
            log.warn("[RECHAZADO] Servicio no encontrado en catálogo para verificar barbero: {}", serviceCode);
            return ValidationResult.failure("Servicio no existe en el catálogo activo");
        }

        String day = TimeSlotUtil.extractDay(turno.getTimeSlot());
        int hour = TimeSlotUtil.extractHour24(turno.getTimeSlot());

        if (hour < 0 || day.isEmpty()) {
            log.warn("[RECHAZADO] Franja horaria con formato no procesable: {}", turno.getTimeSlot());
            return ValidationResult.failure("Formato de franja horaria inválido");
        }

        Barbero asignado = barberRegistry.findAvailable(servicio.getSpecialty(), day, hour);
        if (asignado == null) {
            log.warn("[RECHAZADO] Ningún barbero disponible para especialidad {} en {} a las {}",
                    servicio.getSpecialty(), day, hour);
            return ValidationResult.failure("Ningún barbero disponible para la especialidad " +
                    servicio.getSpecialty() + " en la franja " + turno.getTimeSlot());
        }

        turno.setAssignedBarber(asignado.getName());
        log.info("[OK] Barbero asignado: {}", asignado.getName());
        return proceed(turno);
    }
}
