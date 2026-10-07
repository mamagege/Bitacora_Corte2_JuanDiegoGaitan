package com.dows.bitacora2.restaurante.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @NotBlank(message = "El email no puede estar vacío")
        @Email(message = "Debe ser un email válido")
        String email,
        
        @NotBlank(message = "El password no puede estar vacío")
        @Size(min = 8, message = "El password debe tener al menos 8 caracteres")
        String password
) {
}
