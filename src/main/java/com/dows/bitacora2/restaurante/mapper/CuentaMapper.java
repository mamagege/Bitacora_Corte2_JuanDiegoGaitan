package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.Cuenta;
import com.dows.bitacora2.restaurante.model.dto.request.CuentaRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.CuentaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CuentaMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "total", constant = "0.0")
    @Mapping(target = "estado", expression = "java(com.dows.bitacora2.restaurante.model.domain.EstadoCuenta.ABIERTA)")
    @Mapping(target = "fechaApertura", expression = "java(java.time.LocalDateTime.now())")
    Cuenta toDomain(CuentaRequestDTO dto);

    CuentaResponseDTO toResponse(Cuenta cuenta);
    List<CuentaResponseDTO> toResponseList(List<Cuenta> cuentas);
}
