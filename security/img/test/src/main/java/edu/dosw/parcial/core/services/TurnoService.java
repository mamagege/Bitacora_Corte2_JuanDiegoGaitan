package edu.dosw.parcial.core.services;

import edu.dosw.parcial.core.models.PagoResultado;
import edu.dosw.parcial.core.models.ResourceNotFoundException;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.core.models.TurnoValidationException;
import edu.dosw.parcial.core.validators.ValidationChainFactory;
import edu.dosw.parcial.core.validators.ValidationHandler;
import edu.dosw.parcial.core.validators.ValidationResult;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.mappers.TurnoEntityMapper;
import edu.dosw.parcial.persistence.repositories.TurnoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Servicio de lógica de negocio para la gestión y procesamiento de turnos.
 * Ejecuta el procesamiento a través de la cadena de validación (Chain of Responsibility)
 * y persiste el resultado en MongoDB a través del Mapper OUT y Repositorio.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final TurnoEntityMapper turnoEntityMapper;
    private final ValidationChainFactory validationChainFactory;
    private final AtomicLong turnoCounter = new AtomicLong(1);

    /**
     * Procesa la creación de un nuevo turno pasando por las 5 validaciones ordenadas:
     * 1. Franja horaria disponible
     * 2. Barbero disponible y asignación
     * 3. Datos del cliente
     * 4. Servicio en catálogo
     * 5. Pasarela de pago activa y resultado de cobro
     */
    public Turno createTurno(Turno turno) {
        if (turno.getTurnoId() == null || turno.getTurnoId().isBlank()) {
            turno.setTurnoId(String.format("TURNO #%03d", turnoCounter.getAndIncrement()));
        }
        if (turno.getCreatedAt() == null) {
            turno.setCreatedAt(LocalDateTime.now());
        }

        log.info("Iniciando procesamiento de {}", turno.getTurnoId());

        ValidationHandler chain = validationChainFactory.buildChain(this::countConfirmedBySlot);
        ValidationResult result = chain.handle(turno);

        if (!result.isValid()) {
            turno.setStatus("RECHAZADO");
            turno.setRejectionReason(result.getReason());
            if (result.getPaymentResult() != null) {
                turno.setPaymentResult(result.getPaymentResult());
            }

            // Persistir registro del turno rechazado para trazabilidad y auditoría
            saveTurnoQuietly(turno);

            log.warn("Procesamiento finalizado con rechazo para {}: {}", turno.getTurnoId(), result.getReason());
            throw new TurnoValidationException(result.getReason(), result.getPaymentResult());
        }

        // Si fue exitoso, determinar si queda CONFIRMADO o PENDIENTE (e.g., PSE Bancolombia)
        PagoResultado pago = result.getPaymentResult();
        if (pago != null && "PENDING".equalsIgnoreCase(pago.getStatus())) {
            turno.setStatus("PENDIENTE");
        } else {
            turno.setStatus("CONFIRMADO");
        }
        turno.setPaymentResult(pago);
        if (result.getAssignedBarber() != null) {
            turno.setAssignedBarber(result.getAssignedBarber());
        }

        TurnoDocument doc = turnoEntityMapper.toDocument(turno);
        TurnoDocument saved = turnoRepository.save(doc);
        Turno savedDomain = turnoEntityMapper.toDomain(saved);

        log.info("Turno {} procesado con éxito. Estado: {}, Barbero: {}, Total: ${}",
                savedDomain.getTurnoId(), savedDomain.getStatus(),
                savedDomain.getAssignedBarber(), savedDomain.getAmount());

        return savedDomain;
    }

    public List<Turno> getAllTurnos() {
        return turnoRepository.findAll().stream()
                .map(turnoEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    public Turno getTurnoById(String id) {
        return turnoRepository.findById(id)
                .map(turnoEntityMapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con id: " + id));
    }

    public Turno getTurnoByTurnoId(String turnoId) {
        return turnoRepository.findByTurnoId(turnoId)
                .map(turnoEntityMapper::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException("Turno no encontrado con identificador: " + turnoId));
    }

    public long countConfirmedBySlot(String slot) {
        try {
            return turnoRepository.countByTimeSlotAndStatus(slot, "CONFIRMADO");
        } catch (Exception e) {
            log.warn("No se pudo consultar ocupación en MongoDB para franja {}: {}", slot, e.getMessage());
            return 0;
        }
    }

    private void saveTurnoQuietly(Turno turno) {
        try {
            TurnoDocument doc = turnoEntityMapper.toDocument(turno);
            turnoRepository.save(doc);
        } catch (Exception e) {
            log.warn("No se pudo registrar turno rechazado en MongoDB: {}", e.getMessage());
        }
    }
}
