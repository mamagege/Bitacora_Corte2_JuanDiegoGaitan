package edu.dosw.parcial.persistence.mappers;

import edu.dosw.parcial.core.models.Cliente;
import edu.dosw.parcial.core.models.PagoResultado;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper OUT: Transforma bidireccionalmente entre el modelo de Dominio (Turno)
 * y el Documento de MongoDB (TurnoDocument).
 * El Service no manipula entidades de base de datos directamente.
 */
@Component
public class TurnoEntityMapper {

    public TurnoDocument toDocument(Turno domain) {
        if (domain == null) {
            return null;
        }

        String paymentId = domain.getPaymentResult() != null ? domain.getPaymentResult().getPaymentId() : null;
        String paymentStatus = domain.getPaymentResult() != null ? domain.getPaymentResult().getStatus() : null;
        String paymentMsg = domain.getPaymentResult() != null ? domain.getPaymentResult().getMessage() : null;

        String clientName = domain.getClient() != null ? domain.getClient().getName() : null;
        String clientPhone = domain.getClient() != null ? domain.getClient().getPhone() : null;
        String clientEmail = domain.getClient() != null ? domain.getClient().getEmail() : null;

        return TurnoDocument.builder()
                .id(domain.getId())
                .turnoId(domain.getTurnoId())
                .clientName(clientName)
                .clientPhone(clientPhone)
                .clientEmail(clientEmail)
                .serviceCode(domain.getServiceCode())
                .serviceName(domain.getServiceName())
                .timeSlot(domain.getTimeSlot())
                .assignedBarber(domain.getAssignedBarber())
                .paymentMethod(domain.getPaymentMethod())
                .paymentMetadata(domain.getPaymentMetadata())
                .amount(domain.getAmount())
                .paymentId(paymentId)
                .paymentStatus(paymentStatus)
                .paymentMessage(paymentMsg)
                .status(domain.getStatus())
                .rejectionReason(domain.getRejectionReason())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    public Turno toDomain(TurnoDocument doc) {
        if (doc == null) {
            return null;
        }

        Cliente cliente = Cliente.builder()
                .name(doc.getClientName())
                .phone(doc.getClientPhone())
                .email(doc.getClientEmail())
                .build();

        PagoResultado pago = null;
        if (doc.getPaymentId() != null || doc.getPaymentStatus() != null) {
            pago = PagoResultado.builder()
                    .paymentId(doc.getPaymentId())
                    .status(doc.getPaymentStatus())
                    .message(doc.getPaymentMessage())
                    .build();
        }

        return Turno.builder()
                .id(doc.getId())
                .turnoId(doc.getTurnoId())
                .client(cliente)
                .serviceCode(doc.getServiceCode())
                .serviceName(doc.getServiceName())
                .timeSlot(doc.getTimeSlot())
                .assignedBarber(doc.getAssignedBarber())
                .paymentMethod(doc.getPaymentMethod())
                .paymentMetadata(doc.getPaymentMetadata())
                .amount(doc.getAmount())
                .paymentResult(pago)
                .status(doc.getStatus())
                .rejectionReason(doc.getRejectionReason())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
