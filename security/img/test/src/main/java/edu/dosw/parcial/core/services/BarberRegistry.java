package edu.dosw.parcial.core.services;

import edu.dosw.parcial.core.models.Barbero;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Registro de barberos y consulta de disponibilidad según especialidad, día y franja horaria.
 */
@Component
public class BarberRegistry {

    private final List<Barbero> barberos = List.of(
            new Barbero("Miguel",
                    List.of("CORTE", "CORTE_BARBA"),
                    List.of("Lunes", "Martes", "Miércoles", "Jueves"),
                    10, 17),
            new Barbero("Laura",
                    List.of("CAPILAR", "CEJAS"),
                    List.of("Lunes", "Martes", "Miércoles", "Jueves", "Viernes"),
                    10, 17),
            new Barbero("Andres",
                    List.of("CORTE", "BARBA"),
                    List.of("Martes", "Jueves", "Sábado", "Domingo"),
                    10, 14)
    );

    public List<Barbero> getAllBarberos() {
        return barberos;
    }

    public Barbero findAvailable(String specialty, String day, int hour24) {
        if (specialty == null || day == null) {
            return null;
        }
        for (Barbero barbero : barberos) {
            if (barbero.isAvailableFor(specialty, day, hour24)) {
                return barbero;
            }
        }
        return null;
    }
}
