package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.Cuenta;
import com.dows.bitacora2.restaurante.persistence.entity.CuentaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CuentaEntityMapper {
    CuentaEntity toEntity(Cuenta cuenta);
    Cuenta toDomain(CuentaEntity entity);
    List<Cuenta> toDomainList(List<CuentaEntity> entities);
}
