package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.core.models.Turno;

/**
 * Manejador abstracto base para el patrón de comportamiento Chain of Responsibility.
 */
public abstract class ValidationHandler {

    protected ValidationHandler next;

    public ValidationHandler setNext(ValidationHandler next) {
        this.next = next;
        return next;
    }

    protected ValidationResult proceed(Turno turno) {
        if (next == null) {
            return ValidationResult.success();
        }
        return next.handle(turno);
    }

    public abstract ValidationResult handle(Turno turno);
}
