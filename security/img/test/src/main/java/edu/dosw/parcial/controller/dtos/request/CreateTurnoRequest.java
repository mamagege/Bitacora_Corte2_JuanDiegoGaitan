package edu.dosw.parcial.controller.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO de solicitud para la creación y procesamiento de un turno.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTurnoRequest {

    @Schema(description = "Identificador opcional del turno", example = "TURNO #001")
    private String turnoId;

    @NotBlank(message = "El nombre del cliente no puede ser nulo ni vacío")
    @Schema(description = "Nombre completo del cliente", example = "Carlos Ruiz")
    private String clientName;

    @NotBlank(message = "El teléfono del cliente es obligatorio")
    @Pattern(regexp = "^\\d{10}$", message = "El teléfono debe tener exactamente 10 dígitos")
    @Schema(description = "Teléfono móvil a 10 dígitos", example = "3208872165")
    private String clientPhone;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato de correo no es válido")
    @Schema(description = "Correo del cliente (dominio @gmail.com requerido por regla de negocio)", example = "carlos@gmail.com")
    private String clientEmail;

    @NotBlank(message = "El código de servicio es obligatorio")
    @Schema(description = "Código del servicio del catálogo activo (SER-001 a SER-005)", example = "SER-002")
    private String serviceCode;

    @NotBlank(message = "La franja horaria es obligatoria")
    @Schema(description = "Franja horaria con día y hora", example = "Martes 10:00 AM")
    private String timeSlot;

    @NotBlank(message = "El método de pago es obligatorio")
    @Schema(description = "Pasarela de pago: NEQUI, PSE, STRIPE, EFECTIVO", example = "NEQUI")
    private String paymentMethod;

    @Schema(description = "Metadatos complementarios de pago (phone para Nequi; bank y accountNumber para PSE; cvv para Stripe)")
    private Map<String, String> paymentMetadata;

    @Schema(description = "Monto de la transacción (opcional, por defecto toma el precio del catálogo)", example = "38000")
    private double amount;
}
