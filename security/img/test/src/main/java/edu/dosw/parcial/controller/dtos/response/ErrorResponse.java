package edu.dosw.parcial.controller.dtos.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "Marca de tiempo del error")
    private LocalDateTime timestamp;

    @Schema(description = "Código de estado HTTP", example = "400")
    private int status;

    @Schema(description = "Razón HTTP", example = "Bad Request")
    private String error;

    @Schema(description = "Mensaje detallado del error o motivo de rechazo", example = "Error: teléfono debe tener 10 dígitos")
    private String message;

    @Schema(description = "Ruta de la solicitud", example = "/api/v1/turnos")
    private String path;

    @Schema(description = "Detalle de errores de validación de campos")
    private Map<String, String> fieldErrors;
}
