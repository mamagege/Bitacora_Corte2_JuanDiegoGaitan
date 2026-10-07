package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.Mesa;
import com.dows.bitacora2.restaurante.model.dto.request.MesaRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.MesaResponseDTO;
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
public class MesaMapperImpl implements MesaMapper {

    @Override
    public Mesa toDomain(MesaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Mesa mesa = new Mesa();

        mesa.setNumero( dto.getNumero() );
        mesa.setCapacidad( dto.getCapacidad() );

        mesa.setEstado( com.dows.bitacora2.restaurante.model.domain.EstadoMesa.DISPONIBLE );
        mesa.setCuentaAbierta( false );

        return mesa;
    }

    @Override
    public MesaResponseDTO toResponse(Mesa mesa) {
        if ( mesa == null ) {
            return null;
        }

        MesaResponseDTO mesaResponseDTO = new MesaResponseDTO();

        mesaResponseDTO.setId( mesa.getId() );
        mesaResponseDTO.setNumero( mesa.getNumero() );
        mesaResponseDTO.setCapacidad( mesa.getCapacidad() );
        mesaResponseDTO.setEstado( mesa.getEstado() );
        mesaResponseDTO.setCuentaAbierta( mesa.getCuentaAbierta() );

        return mesaResponseDTO;
    }

    @Override
    public List<MesaResponseDTO> toResponseList(List<Mesa> mesas) {
        if ( mesas == null ) {
            return null;
        }

        List<MesaResponseDTO> list = new ArrayList<MesaResponseDTO>( mesas.size() );
        for ( Mesa mesa : mesas ) {
            list.add( toResponse( mesa ) );
        }

        return list;
    }
}
