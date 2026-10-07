package com.dows.bitacora2.restaurante.model.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ItemPedidoRequestDTO(
    @NotNull(message = "El id del plato es obligatorio")
    Long idPlato,

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    Integer cantidad,

    @Size(max = 200, message = "Las notas no pueden exceder 200 caracteres")
    String notas
) {}