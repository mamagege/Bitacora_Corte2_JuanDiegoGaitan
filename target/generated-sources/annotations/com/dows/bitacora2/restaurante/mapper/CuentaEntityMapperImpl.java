package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.Cuenta;
import com.dows.bitacora2.restaurante.persistence.entity.CuentaEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-29T00:08:49-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class CuentaEntityMapperImpl implements CuentaEntityMapper {

    @Override
    public CuentaEntity toEntity(Cuenta cuenta) {
        if ( cuenta == null ) {
            return null;
        }

        CuentaEntity cuentaEntity = new CuentaEntity();

        cuentaEntity.setId( cuenta.getId() );
        cuentaEntity.setIdMesa( cuenta.getIdMesa() );
        cuentaEntity.setTotal( cuenta.getTotal() );
        cuentaEntity.setEstado( cuenta.getEstado() );
        cuentaEntity.setFechaApertura( cuenta.getFechaApertura() );

        return cuentaEntity;
    }

    @Override
    public Cuenta toDomain(CuentaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Cuenta cuenta = new Cuenta();

        cuenta.setId( entity.getId() );
        cuenta.setIdMesa( entity.getIdMesa() );
        cuenta.setTotal( entity.getTotal() );
        cuenta.setEstado( entity.getEstado() );
        cuenta.setFechaApertura( entity.getFechaApertura() );

        return cuenta;
    }

    @Override
    public List<Cuenta> toDomainList(List<CuentaEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<Cuenta> list = new ArrayList<Cuenta>( entities.size() );
        for ( CuentaEntity cuentaEntity : entities ) {
            list.add( toDomain( cuentaEntity ) );
        }

        return list;
    }
}
