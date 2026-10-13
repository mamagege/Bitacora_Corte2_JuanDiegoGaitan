package edu.dosw.parcial.controller.mappers;

import edu.dosw.parcial.controller.dtos.request.CreateTurnoRequest;
import edu.dosw.parcial.controller.dtos.response.TurnoResponse;
import edu.dosw.parcial.core.models.Cliente;
import edu.dosw.parcial.core.models.PagoResultado;
import edu.dosw.parcial.core.models.Turno;
import org.springframework.stereotype.Component;

/**
 * Mapper IN: Transforma entre DTOs de presentación (Request / Response)
 * y el modelo de Dominio puro (Turno).
 * El Controller nunca ve un Document y el Service nunca ve un DTO.
 */
@Component
public class TurnoDtoMapper {

    public Turno toDomain(CreateTurnoRequest request) {
        if (request == null) {
            return null;
        }

        Cliente cliente = Cliente.builder()
                .name(request.getClientName())
                .phone(request.getClientPhone())
                .email(request.getClientEmail())
                .build();

        return Turno.builder()
                .turnoId(request.getTurnoId())
                .client(cliente)
                .serviceCode(request.getServiceCode())
                .timeSlot(request.getTimeSlot())
                .paymentMethod(request.getPaymentMethod())
                .paymentMetadata(request.getPaymentMetadata())
                .amount(request.getAmount())
                .build();
    }

    public TurnoResponse toResponse(Turno domain) {
        if (domain == null) {
            return null;
        }

        String clientName = domain.getClient() != null ? domain.getClient().getName() : null;
        String clientPhone = domain.getClient() != null ? domain.getClient().getPhone() : null;
        String clientEmail = domain.getClient() != null ? domain.getClient().getEmail() : null;

        String paymentId = null;
        String paymentStatus = null;
        String paymentMessage = null;

        PagoResultado pago = domain.getPaymentResult();
        if (pago != null) {
            paymentId = pago.getPaymentId();
            paymentStatus = pago.getStatus();
            paymentMessage = pago.getMessage();
        }

        return TurnoResponse.builder()
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
                .amount(domain.getAmount())
                .paymentId(paymentId)
                .paymentStatus(paymentStatus)
                .paymentMessage(paymentMessage)
                .status(domain.getStatus())
                .rejectionReason(domain.getRejectionReason())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
