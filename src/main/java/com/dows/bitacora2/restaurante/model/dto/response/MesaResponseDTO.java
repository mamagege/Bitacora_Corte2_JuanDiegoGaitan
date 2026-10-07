package com.dows.bitacora2.restaurante.model.dto.response;

public record MesaResponseDTO(
    Long id,
    Integer numero,
    Integer capacidad,
    String estado
) {}