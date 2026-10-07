package com.dows.bitacora2.restaurante.model.dto.request;

import jakarta.validation.constraints.NotNull;

public record CuentaRequestDTO(
    @NotNull(message = "El id de la mesa es obligatorio")
    Long idMesa
) {}