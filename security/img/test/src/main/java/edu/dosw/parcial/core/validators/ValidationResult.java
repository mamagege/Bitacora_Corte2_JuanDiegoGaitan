package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.core.models.PagoResultado;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {
    private boolean valid;
    private String reason;
    private String assignedBarber;
    private PagoResultado paymentResult;

    public static ValidationResult success() {
        return ValidationResult.builder().valid(true).build();
    }

    public static ValidationResult failure(String reason) {
        return ValidationResult.builder()
                .valid(false)
                .reason(reason)
                .build();
    }
}
