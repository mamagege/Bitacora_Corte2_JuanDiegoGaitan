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
public class AuthResponse {

    @Schema(description = "Token de autenticación JWT para Bearer Authentication")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String type;

    @Schema(description = "Nombre del usuario autenticado", example = "admin")
    private String username;

    @Schema(description = "Rol del usuario en el sistema", example = "ROLE_ADMIN")
    private String role;

    @Schema(description = "Tiempo de validez del token en milisegundos", example = "3600000")
    private long expiresInMs;
}
