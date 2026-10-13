package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.core.models.Cliente;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.core.services.BarberRegistry;
import edu.dosw.parcial.core.services.ServiceCatalog;
import edu.dosw.parcial.core.services.adapters.PaymentAdapterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ValidationHandlersTest {

    private BarberRegistry barberRegistry;
    private ServiceCatalog serviceCatalog;
    private PaymentAdapterFactory paymentAdapterFactory;
    private ValidationChainFactory chainFactory;

    @BeforeEach
    void setUp() {
        barberRegistry = new BarberRegistry();
        serviceCatalog = new ServiceCatalog();
        paymentAdapterFactory = new PaymentAdapterFactory();
        chainFactory = new ValidationChainFactory(barberRegistry, serviceCatalog, paymentAdapterFactory);
    }

    @Test
    @DisplayName("Escenario #1: Turno confirmado exitosamente con Nequi")
    void testEscenario1TurnoAprobadoNequi() {
        ValidationHandler chain = chainFactory.buildChain(slot -> 0L);

        Turno turno = Turno.builder()
                .turnoId("TURNO #001")
                .client(new Cliente("Carlos Ruiz", "3208872165", "carlos@gmail.com"))
                .serviceCode("SER-002")
                .timeSlot("Martes 10:00 AM")
                .paymentMethod("NEQUI")
                .paymentMetadata(Map.of("phone", "3208872165"))
                .amount(38000)
                .build();

        ValidationResult result = chain.handle(turno);

        assertTrue(result.isValid());
        assertEquals("Miguel", turno.getAssignedBarber());
        assertEquals("Corte + barba", turno.getServiceName());
        assertNotNull(result.getPaymentResult());
        assertEquals("APROBADO", result.getPaymentResult().getStatus());
        assertTrue(result.getPaymentResult().getPaymentId().startsWith("NQ-"));
    }

    @Test
    @DisplayName("Escenario #2: Rechazo por teléfono de cliente inválido (menos de 10 dígitos)")
    void testEscenario2RechazoTelefonoInvalido() {
        ValidationHandler chain = chainFactory.buildChain(slot -> 0L);

        Turno turno = Turno.builder()
                .turnoId("TURNO #002")
                .client(new Cliente("Ana Lopez", "312345", "ana@gmail.com"))
                .serviceCode("SER-005")
                .timeSlot("Lunes 10:00 AM")
                .paymentMethod("PSE")
                .amount(45000)
                .build();

        ValidationResult result = chain.handle(turno);

        assertFalse(result.isValid());
        assertTrue(result.getReason().contains("10 dígitos"));
    }

    @Test
    @DisplayName("Escenario #3: Turno procesado con PSE Bancolombia (código 10 PENDING)")
    void testEscenario3PagoPseBancolombiaPending() {
        ValidationHandler chain = chainFactory.buildChain(slot -> 0L);

        Turno turno = Turno.builder()
                .turnoId("TURNO #003")
                .client(new Cliente("Julián Mesa", "3112145678", "julian@gmail.com"))
                .serviceCode("SER-001")
                .timeSlot("Sábado 1:00 PM")
                .paymentMethod("PSE")
                .paymentMetadata(Map.of("bank", "BANCOLOMBIA", "accountNumber", "123456789"))
                .amount(45000)
                .build();

        ValidationResult result = chain.handle(turno);

        assertTrue(result.isValid());
        assertEquals("Andres", turno.getAssignedBarber());
        assertNotNull(result.getPaymentResult());
        assertEquals("PENDING", result.getPaymentResult().getStatus());
    }

    @Test
    @DisplayName("Validación 1: Debe rechazar si la franja horaria tiene 3 o más turnos confirmados")
    void testRechazoFranjaHorariaLlena() {
        // Proveedor que simula franja con 3 turnos ya ocupados
        ValidationHandler chain = chainFactory.buildChain(slot -> 3L);

        Turno turno = Turno.builder()
                .turnoId("TURNO #004")
                .client(new Cliente("Pedro Gomez", "3001234567", "pedro@gmail.com"))
                .serviceCode("SER-001")
                .timeSlot("Martes 10:00 AM")
                .paymentMethod("EFECTIVO")
                .amount(25000)
                .build();

        ValidationResult result = chain.handle(turno);

        assertFalse(result.isValid());
        assertTrue(result.getReason().contains("Franja horaria llena"));
    }

    @Test
    @DisplayName("Validación 2: Debe rechazar si ningún barbero con la especialidad requerida está libre")
    void testRechazoBarberoNoDisponible() {
        ValidationHandler chain = chainFactory.buildChain(slot -> 0L);

        // Laura hace CAPILAR y atiende Lunes a Viernes 10am-5pm. Domingo no atiende nadie CAPILAR.
        Turno turno = Turno.builder()
                .turnoId("TURNO #005")
                .client(new Cliente("Maria Paz", "3001234567", "maria@gmail.com"))
                .serviceCode("SER-005") // CAPILAR
                .timeSlot("Domingo 11:00 AM") // Domingo Laura no trabaja
                .paymentMethod("EFECTIVO")
                .amount(45000)
                .build();

        ValidationResult result = chain.handle(turno);

        assertFalse(result.isValid());
        assertTrue(result.getReason().contains("Ningún barbero disponible"));
    }

    @Test
    @DisplayName("Validación 3: Debe rechazar si correo no termina en @gmail.com")
    void testRechazoCorreoInvalido() {
        ValidationHandler chain = chainFactory.buildChain(slot -> 0L);

        Turno turno = Turno.builder()
                .turnoId("TURNO #006")
                .client(new Cliente("Carlos Ruiz", "3208872165", "carlos@hotmail.com"))
                .serviceCode("SER-001")
                .timeSlot("Martes 10:00 AM")
                .paymentMethod("EFECTIVO")
                .amount(25000)
                .build();

        ValidationResult result = chain.handle(turno);

        assertFalse(result.isValid());
        assertTrue(result.getReason().contains("@gmail.com"));
    }

    @Test
    @DisplayName("Validación 4: Debe rechazar si el servicio no está en el catálogo activo")
    void testRechazoServicioInexistente() {
        ValidationHandler chain = chainFactory.buildChain(slot -> 0L);

        Turno turno = Turno.builder()
                .turnoId("TURNO #007")
                .client(new Cliente("Carlos Ruiz", "3208872165", "carlos@gmail.com"))
                .serviceCode("SER-999")
                .timeSlot("Martes 10:00 AM")
                .paymentMethod("EFECTIVO")
                .amount(25000)
                .build();

        ValidationResult result = chain.handle(turno);

        assertFalse(result.isValid());
        assertTrue(result.getReason().contains("catálogo"));
    }
}
