package com.dows.bitacora2.restaurante.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PedidoRequestDTO(
    @NotNull(message = "El id de la mesa es obligatorio")
    Long idMesa,

    @NotEmpty(message = "El pedido debe tener al menos un item")
    @Valid
    List<ItemPedidoRequestDTO> items
) {}