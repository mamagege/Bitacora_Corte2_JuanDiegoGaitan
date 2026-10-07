package com.dows.bitacora2.restaurante.model.dto.response;

public record CuentaResponseDTO(
    Long id,
    Long idMesa,
    Double total,
    String estado
) {}