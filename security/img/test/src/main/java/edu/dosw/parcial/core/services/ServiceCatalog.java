package edu.dosw.parcial.core.services;

import edu.dosw.parcial.core.models.Servicio;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Catálogo de servicios disponibles y activos en Bob's Barbery.
 */
@Component
public class ServiceCatalog {

    private final Map<String, Servicio> services = new LinkedHashMap<>();

    public ServiceCatalog() {
        register(new Servicio("SER-001", "Corte clásico", 25000.0, "CORTE"));
        register(new Servicio("SER-002", "Corte + barba", 38000.0, "CORTE_BARBA"));
        register(new Servicio("SER-003", "Diseño de cejas", 15000.0, "CEJAS"));
        register(new Servicio("SER-004", "Afeitado con navaja", 20000.0, "BARBA"));
        register(new Servicio("SER-005", "Tratamiento capilar", 45000.0, "CAPILAR"));
    }

    public void register(Servicio servicio) {
        services.put(servicio.getCode(), servicio);
    }

    public boolean isValid(String code) {
        return code != null && services.containsKey(code);
    }

    public Servicio get(String code) {
        return code != null ? services.get(code) : null;
    }

    public Collection<Servicio> getAllServices() {
        return services.values();
    }
}
