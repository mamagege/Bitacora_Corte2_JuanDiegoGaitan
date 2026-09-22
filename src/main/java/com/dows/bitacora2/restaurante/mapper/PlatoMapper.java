package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.Plato;
import com.dows.bitacora2.restaurante.model.dto.request.PlatoRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.PlatoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PlatoMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "disponible", constant = "true")
    Plato toDomain(PlatoRequestDTO dto);

    PlatoResponseDTO toResponse(Plato plato);
    List<PlatoResponseDTO> toResponseList(List<Plato> platos);
}
