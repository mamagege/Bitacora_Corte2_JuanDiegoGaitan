package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.EventoRestaurante;
import com.dows.bitacora2.restaurante.persistence.document.EventoRestauranteDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EventoMapper {

    @Mapping(target = "id", ignore = true)
    EventoRestauranteDocument toDocument(EventoRestaurante evento);

    EventoRestaurante toDomain(EventoRestauranteDocument doc);
}
