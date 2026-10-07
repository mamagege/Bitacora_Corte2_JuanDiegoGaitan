package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.Plato;
import com.dows.bitacora2.restaurante.persistence.entity.PlatoEntity;
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
public class PlatoEntityMapperImpl implements PlatoEntityMapper {

    @Override
    public PlatoEntity toEntity(Plato plato) {
        if ( plato == null ) {
            return null;
        }

        PlatoEntity platoEntity = new PlatoEntity();

        platoEntity.setId( plato.getId() );
        platoEntity.setNombre( plato.getNombre() );
        platoEntity.setPrecio( plato.getPrecio() );
        platoEntity.setCategoria( plato.getCategoria() );
        platoEntity.setDescripcion( plato.getDescripcion() );
        platoEntity.setDisponible( plato.getDisponible() );

        return platoEntity;
    }

    @Override
    public Plato toDomain(PlatoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Plato plato = new Plato();

        plato.setId( entity.getId() );
        plato.setNombre( entity.getNombre() );
        plato.setPrecio( entity.getPrecio() );
        plato.setCategoria( entity.getCategoria() );
        plato.setDisponible( entity.getDisponible() );
        plato.setDescripcion( entity.getDescripcion() );

        return plato;
    }

    @Override
    public List<Plato> toDomainList(List<PlatoEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<Plato> list = new ArrayList<Plato>( entities.size() );
        for ( PlatoEntity platoEntity : entities ) {
            list.add( toDomain( platoEntity ) );
        }

        return list;
    }
}
