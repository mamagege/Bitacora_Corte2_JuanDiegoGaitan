package edu.dosw.parcial.persistence;

import edu.dosw.parcial.core.models.*;
import edu.dosw.parcial.persistence.entities.BarberDocument;
import edu.dosw.parcial.persistence.entities.ServiceDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.entities.UserDocument;
import edu.dosw.parcial.persistence.mappers.CatalogEntityMapper;
import edu.dosw.parcial.persistence.mappers.TurnoEntityMapper;
import edu.dosw.parcial.persistence.mappers.UserEntityMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EntityMappersTest {

    @Test
    @DisplayName("TurnoEntityMapper: debe convertir bidireccionalmente entre Dominio y Documento")
    void testTurnoEntityMapper() {
        TurnoEntityMapper mapper = new TurnoEntityMapper();

        Turno turno = Turno.builder()
                .id("id1")
                .turnoId("TURNO #001")
                .client(new Cliente("Carlos", "3208872165", "carlos@gmail.com"))
                .serviceCode("SER-002")
                .serviceName("Corte + barba")
                .timeSlot("Martes 10:00 AM")
                .assignedBarber("Miguel")
                .paymentMethod("NEQUI")
                .amount(38000)
                .paymentResult(new PagoResultado("NQ-1001", "APROBADO", "Pago ok"))
                .status("CONFIRMADO")
                .build();

        TurnoDocument doc = mapper.toDocument(turno);
        assertNotNull(doc);
        assertEquals("TURNO #001", doc.getTurnoId());
        assertEquals("Carlos", doc.getClientName());
        assertEquals("NQ-1001", doc.getPaymentId());

        Turno recovered = mapper.toDomain(doc);
        assertNotNull(recovered);
        assertEquals("TURNO #001", recovered.getTurnoId());
        assertEquals("Carlos", recovered.getClient().getName());
        assertEquals("NQ-1001", recovered.getPaymentResult().getPaymentId());
    }

    @Test
    @DisplayName("UserEntityMapper: debe convertir bidireccionalmente entre Usuario y UserDocument")
    void testUserEntityMapper() {
        UserEntityMapper mapper = new UserEntityMapper();

        Usuario user = Usuario.builder()
                .username("admin")
                .password("encodedSecret")
                .email("admin@bobsbarber.com")
                .role("ROLE_ADMIN")
                .build();

        UserDocument doc = mapper.toDocument(user);
        assertEquals("admin", doc.getUsername());

        Usuario recovered = mapper.toDomain(doc);
        assertEquals("admin", recovered.getUsername());
        assertEquals("ROLE_ADMIN", recovered.getRole());
    }

    @Test
    @DisplayName("CatalogEntityMapper: debe convertir Servicios y Barberos")
    void testCatalogEntityMapper() {
        CatalogEntityMapper mapper = new CatalogEntityMapper();

        Servicio s = new Servicio("SER-001", "Corte clásico", 25000, "CORTE");
        ServiceDocument sDoc = mapper.toDocument(s);
        assertEquals("SER-001", sDoc.getCode());
        Servicio sRecovered = mapper.toDomain(sDoc);
        assertEquals("SER-001", sRecovered.getCode());

        Barbero b = new Barbero("Miguel", List.of("CORTE"), List.of("Lunes"), 10, 17);
        BarberDocument bDoc = mapper.toDocument(b);
        assertEquals("Miguel", bDoc.getName());
        Barbero bRecovered = mapper.toDomain(bDoc);
        assertEquals("Miguel", bRecovered.getName());
    }
}
