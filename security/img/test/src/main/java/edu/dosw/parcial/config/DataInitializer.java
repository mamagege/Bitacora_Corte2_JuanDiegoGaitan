package edu.dosw.parcial.config;

import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.models.Servicio;
import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.core.services.AuthService;
import edu.dosw.parcial.core.services.BarberRegistry;
import edu.dosw.parcial.core.services.ServiceCatalog;
import edu.dosw.parcial.persistence.mappers.CatalogEntityMapper;
import edu.dosw.parcial.persistence.repositories.BarberDocumentRepository;
import edu.dosw.parcial.persistence.repositories.ServiceDocumentRepository;
import edu.dosw.parcial.persistence.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos de prueba para asegurar que las colecciones iniciales
 * de MongoDB contengan los servicios, barberos y credenciales de prueba.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServiceDocumentRepository serviceRepository;
    private final BarberDocumentRepository barberRepository;
    private final AuthService authService;
    private final ServiceCatalog serviceCatalog;
    private final BarberRegistry barberRegistry;
    private final CatalogEntityMapper catalogEntityMapper;

    @Override
    public void run(String... args) {
        try {
            seedUsers();
            seedServices();
            seedBarbers();
        } catch (Exception e) {
            log.warn("Inicialización de datos en MongoDB omitida o no disponible: {}", e.getMessage());
        }
    }

    private void seedUsers() {
        if (!userRepository.existsByUsername("admin")) {
            authService.register(Usuario.builder()
                    .username("admin")
                    .password("admin123")
                    .email("admin@bobsbarber.com")
                    .role("ROLE_ADMIN")
                    .build());
            log.info("Usuario 'admin' inicializado.");
        }
        if (!userRepository.existsByUsername("carlos")) {
            authService.register(Usuario.builder()
                    .username("carlos")
                    .password("carlos123")
                    .email("carlos@gmail.com")
                    .role("ROLE_USER")
                    .build());
            log.info("Usuario 'carlos' inicializado.");
        }
    }

    private void seedServices() {
        if (serviceRepository.count() == 0) {
            for (Servicio s : serviceCatalog.getAllServices()) {
                serviceRepository.save(catalogEntityMapper.toDocument(s));
            }
            log.info("Servicios del catálogo persistidos en MongoDB.");
        }
    }

    private void seedBarbers() {
        if (barberRepository.count() == 0) {
            for (Barbero b : barberRegistry.getAllBarberos()) {
                barberRepository.save(catalogEntityMapper.toDocument(b));
            }
            log.info("Barberos persistidos en MongoDB.");
        }
    }
}
