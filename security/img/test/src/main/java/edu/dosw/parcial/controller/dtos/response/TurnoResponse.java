package edu.dosw.parcial.controller.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnoResponse {

    @Schema(description = "Identificador único en base de datos", example = "6524fa10b9876543210fedcb")
    private String id;

    @Schema(description = "Código de turno", example = "TURNO #001")
    private String turnoId;

    @Schema(description = "Nombre del cliente", example = "Carlos Ruiz")
    private String clientName;

    @Schema(description = "Teléfono del cliente", example = "3208872165")
    private String clientPhone;

    @Schema(description = "Correo del cliente", example = "carlos@gmail.com")
    private String clientEmail;

    @Schema(description = "Código del servicio reservado", example = "SER-002")
    private String serviceCode;

    @Schema(description = "Nombre descriptivo del servicio", example = "Corte + barba")
    private String serviceName;

    @Schema(description = "Franja horaria asignada", example = "Martes 10:00 AM")
    private String timeSlot;

    @Schema(description = "Barbero asignado automáticamente según especialidad y horario", example = "Miguel")
    private String assignedBarber;

    @Schema(description = "Pasarela de pago utilizada", example = "NEQUI")
    private String paymentMethod;

    @Schema(description = "Monto total del servicio", example = "38000")
    private double amount;

    @Schema(description = "Código de transacción normalizado generado por el Adapter", example = "NQ-1001")
    private String paymentId;

    @Schema(description = "Estado normalizado del pago: APROBADO, RECHAZADO, PENDING", example = "APROBADO")
    private String paymentStatus;

    @Schema(description = "Mensaje informativo devuelto por la pasarela adaptada", example = "Pago Nequi exitoso")
    private String paymentMessage;

    @Schema(description = "Estado general del turno: CONFIRMADO, PENDIENTE, RECHAZADO", example = "CONFIRMADO")
    private String status;

    @Schema(description = "Motivo de rechazo en caso de no superar alguna validación de la cadena")
    private String rejectionReason;

    @Schema(description = "Fecha y hora de creación")
    private LocalDateTime createdAt;
}
