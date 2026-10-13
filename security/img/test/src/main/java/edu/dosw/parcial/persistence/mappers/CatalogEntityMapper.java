package edu.dosw.parcial.persistence.mappers;

import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.models.Servicio;
import edu.dosw.parcial.persistence.entities.BarberDocument;
import edu.dosw.parcial.persistence.entities.ServiceDocument;
import org.springframework.stereotype.Component;

/**
 * Mapper OUT: Transformación de Servicios y Barberos entre Dominio y MongoDB Documents.
 */
@Component
public class CatalogEntityMapper {

    public ServiceDocument toDocument(Servicio domain) {
        if (domain == null) return null;
        return ServiceDocument.builder()
                .code(domain.getCode())
                .name(domain.getName())
                .price(domain.getPrice())
                .specialty(domain.getSpecialty())
                .build();
    }

    public Servicio toDomain(ServiceDocument doc) {
        if (doc == null) return null;
        return Servicio.builder()
                .code(doc.getCode())
                .name(doc.getName())
                .price(doc.getPrice())
                .specialty(doc.getSpecialty())
                .build();
    }

    public BarberDocument toDocument(Barbero domain) {
        if (domain == null) return null;
        return BarberDocument.builder()
                .name(domain.getName())
                .specialties(domain.getSpecialties())
                .days(domain.getDays())
                .startHour(domain.getStartHour())
                .endHour(domain.getEndHour())
                .build();
    }

    public Barbero toDomain(BarberDocument doc) {
        if (doc == null) return null;
        return Barbero.builder()
                .name(doc.getName())
                .specialties(doc.getSpecialties())
                .days(doc.getDays())
                .startHour(doc.getStartHour())
                .endHour(doc.getEndHour())
                .build();
    }
}
