package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.core.models.Turno;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Validación 1 de la cadena: Disponibilidad de franja horaria.
 * Criterio de rechazo: Si la franja horaria está llena (máx. 3 clientes por hora), el turno se rechaza.
 */
@Slf4j
public class TimeAvailableHandler extends ValidationHandler {

    public static final int MAX_CLIENTES_POR_FRANJA = 3;
    private final Map<String, Integer> ocupacionLocal = new ConcurrentHashMap<>();
    private final Function<String, Long> externalOccupancyProvider;

    public TimeAvailableHandler() {
        this.externalOccupancyProvider = null;
    }

    public TimeAvailableHandler(Function<String, Long> externalOccupancyProvider) {
        this.externalOccupancyProvider = externalOccupancyProvider;
    }

    @Override
    public ValidationResult handle(Turno turno) {
        String franja = turno.getTimeSlot();
        if (franja == null || franja.isBlank()) {
            log.warn("[RECHAZADO] Franja horaria no especificada");
            return ValidationResult.failure("Franja horaria no especificada");
        }

        long count = 0;
        if (externalOccupancyProvider != null) {
            count = externalOccupancyProvider.apply(franja);
        } else {
            count = ocupacionLocal.getOrDefault(franja, 0);
        }

        if (count >= MAX_CLIENTES_POR_FRANJA) {
            log.warn("[RECHAZADO] Franja horaria llena: {} (máximo {} clientes)", franja, MAX_CLIENTES_POR_FRANJA);
            return ValidationResult.failure("Franja horaria llena (máx. 3 clientes por hora)");
        }

        ocupacionLocal.put(franja, (int) count + 1);
        log.info("[OK] Franja disponible: {}", franja);
        return proceed(turno);
    }
}
