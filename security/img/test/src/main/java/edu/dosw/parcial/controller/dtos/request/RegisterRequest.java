package edu.dosw.parcial.controller.dtos.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    @Schema(description = "Nombre de usuario", example = "juandiego")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Schema(description = "Contraseña segura", example = "secret123")
    private String password;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Formato de correo no válido")
    @Schema(description = "Correo del usuario", example = "juan@bobsbarber.com")
    private String email;

    @Schema(description = "Rol asignado (USER, ADMIN, BARBER)", example = "USER")
    private String role;
}
