package com.dows.bitacora2.restaurante.model.dto.response;
import java.util.List;
public record PedidoResponseDTO(
    Long id,
    Long idMesa,
    String estado,
    Double total,
    List<ItemPedidoResponseDTO> items
) {}