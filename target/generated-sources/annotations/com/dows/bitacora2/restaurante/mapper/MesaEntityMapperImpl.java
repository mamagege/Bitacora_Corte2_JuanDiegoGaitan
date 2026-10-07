package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.Mesa;
import com.dows.bitacora2.restaurante.persistence.entity.MesaEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-07T02:20:42-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class MesaEntityMapperImpl implements MesaEntityMapper {

    @Override
    public MesaEntity toEntity(Mesa mesa) {
        if ( mesa == null ) {
            return null;
        }

        MesaEntity mesaEntity = new MesaEntity();

        mesaEntity.setId( mesa.getId() );
        mesaEntity.setNumero( mesa.getNumero() );
        mesaEntity.setCapacidad( mesa.getCapacidad() );
        mesaEntity.setEstado( mesa.getEstado() );
        mesaEntity.setCuentaAbierta( mesa.getCuentaAbierta() );

        return mesaEntity;
    }

    @Override
    public Mesa toDomain(MesaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Mesa mesa = new Mesa();

        mesa.setId( entity.getId() );
        mesa.setNumero( entity.getNumero() );
        mesa.setCapacidad( entity.getCapacidad() );
        mesa.setEstado( entity.getEstado() );
        mesa.setCuentaAbierta( entity.getCuentaAbierta() );

        return mesa;
    }

    @Override
    public List<Mesa> toDomainList(List<MesaEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<Mesa> list = new ArrayList<Mesa>( entities.size() );
        for ( MesaEntity mesaEntity : entities ) {
            list.add( toDomain( mesaEntity ) );
        }

        return list;
    }
}
