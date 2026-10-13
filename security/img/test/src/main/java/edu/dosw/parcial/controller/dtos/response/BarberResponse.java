package edu.dosw.parcial.controller.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BarberResponse {

    @Schema(description = "Nombre del barbero", example = "Miguel")
    private String name;

    @Schema(description = "Especialidades del barbero", example = "[\"CORTE\", \"CORTE_BARBA\"]")
    private List<String> specialties;

    @Schema(description = "Días de atención", example = "[\"Lunes\", \"Martes\", \"Miércoles\", \"Jueves\"]")
    private List<String> days;

    @Schema(description = "Horario de inicio (formato 24h)", example = "10")
    private int startHour;

    @Schema(description = "Horario de fin (formato 24h)", example = "17")
    private int endHour;
}
