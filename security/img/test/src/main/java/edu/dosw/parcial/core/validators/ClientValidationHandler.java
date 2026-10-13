package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.core.models.Cliente;
import edu.dosw.parcial.core.models.Turno;
import lombok.extern.slf4j.Slf4j;

/**
 * Validación 3 de la cadena: Datos del cliente.
 * Criterio de rechazo: Nombre (no nulo ni blanco), teléfono (exactamente 10 dígitos)
 * y correo válido (@gmail.com) son obligatorios.
 */
@Slf4j
public class ClientValidationHandler extends ValidationHandler {

    @Override
    public ValidationResult handle(Turno turno) {
        Cliente client = turno.getClient();

        if (client == null) {
            log.warn("[RECHAZADO] Datos del cliente ausentes");
            return ValidationResult.failure("Datos del cliente son obligatorios");
        }

        if (client.getName() == null || client.getName().isBlank()) {
            log.warn("[RECHAZADO] Nombre del cliente inválido o nulo");
            return ValidationResult.failure("Nombre del cliente no puede ser nulo ni vacío");
        }

        if (client.getPhone() == null || !client.getPhone().matches("^\\d{10}$")) {
            log.warn("[RECHAZADO] Teléfono inválido: {}", client.getPhone());
            return ValidationResult.failure("Error: teléfono debe tener 10 dígitos");
        }

        if (client.getEmail() == null || !client.getEmail().toLowerCase().endsWith("@gmail.com")) {
            log.warn("[RECHAZADO] Correo inválido: {}", client.getEmail());
            return ValidationResult.failure("Correo inválido: debe ser una cuenta @gmail.com");
        }

        log.info("[OK] Datos del cliente válidos");
        return proceed(turno);
    }
}
