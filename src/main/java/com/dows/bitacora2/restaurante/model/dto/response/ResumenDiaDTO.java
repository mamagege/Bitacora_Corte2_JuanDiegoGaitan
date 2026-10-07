package com.dows.bitacora2.restaurante.model.dto.response;

import java.util.Map;

public record ResumenDiaDTO(
    long totalPedidos,
    double ingresoTotal,
    Map<String, Long> platosMasPedidos
) {}