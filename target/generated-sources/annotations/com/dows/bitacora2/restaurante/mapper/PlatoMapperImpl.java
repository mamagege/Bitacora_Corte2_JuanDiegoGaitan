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
    date = "2026-09-22T00:52:15-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class PlatoMapperImpl implements PlatoMapper {

    @Override
    public Plato toDomain(PlatoRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Plato plato = new Plato();

        plato.setNombre( dto.getNombre() );
        plato.setPrecio( dto.getPrecio() );
        plato.setCategoria( dto.getCategoria() );
        plato.setDescripcion( dto.getDescripcion() );

        plato.setDisponible( true );

        return plato;
    }

    @Override
    public PlatoResponseDTO toResponse(Plato plato) {
        if ( plato == null ) {
            return null;
        }

        PlatoResponseDTO platoResponseDTO = new PlatoResponseDTO();

        platoResponseDTO.setId( plato.getId() );
        platoResponseDTO.setNombre( plato.getNombre() );
        platoResponseDTO.setPrecio( plato.getPrecio() );
        platoResponseDTO.setCategoria( plato.getCategoria() );
        platoResponseDTO.setDisponible( plato.getDisponible() );

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
