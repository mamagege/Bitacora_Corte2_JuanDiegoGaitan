package edu.dosw.parcial.core.services;

import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.models.Servicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para consulta de catálogo de servicios y equipo de barberos disponibles.
 */
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final ServiceCatalog serviceCatalog;
    private final BarberRegistry barberRegistry;

    public List<Servicio> getServices() {
        return new ArrayList<>(serviceCatalog.getAllServices());
    }

    public List<Barbero> getBarbers() {
        return barberRegistry.getAllBarberos();
    }

    public Servicio getServiceByCode(String code) {
        return serviceCatalog.get(code);
    }
}
