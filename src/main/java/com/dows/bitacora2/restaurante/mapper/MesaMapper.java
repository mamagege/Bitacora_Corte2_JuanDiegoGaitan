package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.Mesa;
import com.dows.bitacora2.restaurante.model.dto.request.MesaRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.MesaResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MesaMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", expression = "java(com.dows.bitacora2.restaurante.model.domain.EstadoMesa.DISPONIBLE)")
    @Mapping(target = "cuentaAbierta", constant = "false")
    Mesa toDomain(MesaRequestDTO dto);

    MesaResponseDTO toResponse(Mesa mesa);
    List<MesaResponseDTO> toResponseList(List<Mesa> mesas);
}
