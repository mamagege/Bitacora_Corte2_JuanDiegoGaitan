package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Turno {
    private String id;
    private String turnoId;
    private Cliente client;
    private String serviceCode;
    private String serviceName;
    private String timeSlot;
    private String assignedBarber;
    private String paymentMethod;
    private Map<String, String> paymentMetadata;
    private double amount;
    private PagoResultado paymentResult;
    private String status; // CONFIRMADO, PENDIENTE, RECHAZADO
    private String rejectionReason;
    private LocalDateTime createdAt;
}
