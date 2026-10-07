package com.dows.bitacora2.restaurante.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PlatoRequestDTO(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "MÃ¡ximo 100 caracteres")
    String nombre,

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    Double precio,

    @NotBlank(message = "La categorÃ­a es obligatoria")
    String categoria,

    String descripcion
) {}