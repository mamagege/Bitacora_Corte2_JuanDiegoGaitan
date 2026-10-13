package edu.dosw.parcial.controller.mappers;

import edu.dosw.parcial.controller.dtos.response.BarberResponse;
import edu.dosw.parcial.controller.dtos.response.ServiceResponse;
import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.models.Servicio;
import org.springframework.stereotype.Component;

/**
 * Mapper IN: Transforma modelos de dominio de Servicios y Barberos a DTOs de respuesta.
 */
@Component
public class CatalogDtoMapper {

    public ServiceResponse toResponse(Servicio domain) {
        if (domain == null) return null;
        return ServiceResponse.builder()
                .code(domain.getCode())
                .name(domain.getName())
                .price(domain.getPrice())
                .specialty(domain.getSpecialty())
                .build();
    }

    public BarberResponse toResponse(Barbero domain) {
        if (domain == null) return null;
        return BarberResponse.builder()
                .name(domain.getName())
                .specialties(domain.getSpecialties())
                .days(domain.getDays())
                .startHour(domain.getStartHour())
                .endHour(domain.getEndHour())
                .build();
    }
}
