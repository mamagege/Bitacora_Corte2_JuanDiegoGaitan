package edu.dosw.parcial.core.models;

import lombok.Getter;

@Getter
public class TurnoValidationException extends RuntimeException {
    private final String reason;
    private final PagoResultado paymentResult;

    public TurnoValidationException(String reason) {
        super(reason);
        this.reason = reason;
        this.paymentResult = null;
    }

    public TurnoValidationException(String reason, PagoResultado paymentResult) {
        super(reason);
        this.reason = reason;
        this.paymentResult = paymentResult;
    }
}
