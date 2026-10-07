package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.Plato;
import com.dows.bitacora2.restaurante.persistence.entity.PlatoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PlatoEntityMapper {
    @org.mapstruct.Mapping(target = "creadoEn", ignore = true)
    PlatoEntity toEntity(Plato plato);
    Plato toDomain(PlatoEntity entity);
    List<Plato> toDomainList(List<PlatoEntity> entities);
}
