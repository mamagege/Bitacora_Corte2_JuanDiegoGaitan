package edu.dosw.parcial.controller.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponse {

    @Schema(description = "Código del servicio", example = "SER-001")
    private String code;

    @Schema(description = "Nombre del servicio", example = "Corte clásico")
    private String name;

    @Schema(description = "Precio del servicio en COP", example = "25000")
    private double price;

    @Schema(description = "Especialidad requerida", example = "CORTE")
    private String specialty;
}
