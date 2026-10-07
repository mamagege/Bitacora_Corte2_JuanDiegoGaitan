package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.Plato;
import com.dows.bitacora2.restaurante.model.dto.request.PlatoRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.PlatoResponseDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-07T17:30:31-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class PlatoMapperImpl implements PlatoMapper {

    @Override
    public Plato toDomain(PlatoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Plato plato = new Plato();

        plato.setNombre( dto.nombre() );
        plato.setPrecio( dto.precio() );
        plato.setCategoria( dto.categoria() );
        plato.setDescripcion( dto.descripcion() );

        plato.setDisponible( true );

        return plato;
    }

    @Override
    public PlatoResponseDTO toResponse(Plato plato) {
        if ( plato == null ) {
            return null;
        }

        Long id = null;
        String nombre = null;
        Double precio = null;
        String categoria = null;
        String descripcion = null;
        Boolean disponible = null;

        id = plato.getId();
        nombre = plato.getNombre();
        precio = plato.getPrecio();
        categoria = plato.getCategoria();
        descripcion = plato.getDescripcion();
        disponible = plato.getDisponible();

        PlatoResponseDTO platoResponseDTO = new PlatoResponseDTO( id, nombre, precio, categoria, descripcion, disponible );

        return platoResponseDTO;
    }

    @Override
    public List<PlatoResponseDTO> toResponseList(List<Plato> platos) {
        if ( platos == null ) {
            return null;
        }

        List<PlatoResponseDTO> list = new ArrayList<PlatoResponseDTO>( platos.size() );
        for ( Plato plato : platos ) {
            list.add( toResponse( plato ) );
        }

        return list;
    }
}
