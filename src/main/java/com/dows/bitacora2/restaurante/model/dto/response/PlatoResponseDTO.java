package com.dows.bitacora2.restaurante.model.dto.response;

public record PlatoResponseDTO(
    Long id,
    String nombre,
    Double precio,
    String categoria,
    String descripcion,
    Boolean disponible
) {}