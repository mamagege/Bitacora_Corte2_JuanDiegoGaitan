package com.dows.bitacora2.restaurante.model.dto.response;

public record ItemPedidoResponseDTO(
    Long idPlato,
    String nombrePlato,
    Integer cantidad,
    Double subtotal,
    String notas
) {}