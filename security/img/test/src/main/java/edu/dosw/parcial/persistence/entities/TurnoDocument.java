package edu.dosw.parcial.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entidad de persistencia documental para colección MongoDB 'turnos'.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "turnos")
public class TurnoDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String turnoId;

    private String clientName;
    private String clientPhone;
    private String clientEmail;

    private String serviceCode;
    private String serviceName;

    @Indexed
    private String timeSlot;

    private String assignedBarber;

    private String paymentMethod;
    private Map<String, String> paymentMetadata;
    private double amount;

    private String paymentId;
    private String paymentStatus;
    private String paymentMessage;

    @Indexed
    private String status; // CONFIRMADO, PENDIENTE, RECHAZADO

    private String rejectionReason;

    private LocalDateTime createdAt;
}
