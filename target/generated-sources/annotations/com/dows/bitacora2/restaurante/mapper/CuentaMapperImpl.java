package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.Cuenta;
import com.dows.bitacora2.restaurante.model.dto.request.CuentaRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.CuentaResponseDTO;
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
public class CuentaMapperImpl implements CuentaMapper {

    @Override
    public Cuenta toDomain(CuentaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Cuenta cuenta = new Cuenta();

        cuenta.setIdMesa( dto.idMesa() );

        cuenta.setTotal( (double) 0.0 );
        cuenta.setEstado( com.dows.bitacora2.restaurante.model.domain.EstadoCuenta.ABIERTA );
        cuenta.setFechaApertura( java.time.LocalDateTime.now() );

        return cuenta;
    }

    @Override
    public CuentaResponseDTO toResponse(Cuenta cuenta) {
        if ( cuenta == null ) {
            return null;
        }

        Long id = null;
        Long idMesa = null;
        Double total = null;
        String estado = null;

        id = cuenta.getId();
        idMesa = cuenta.getIdMesa();
        total = cuenta.getTotal();
        if ( cuenta.getEstado() != null ) {
            estado = cuenta.getEstado().name();
        }

        CuentaResponseDTO cuentaResponseDTO = new CuentaResponseDTO( id, idMesa, total, estado );

        return cuentaResponseDTO;
    }

    @Override
    public List<CuentaResponseDTO> toResponseList(List<Cuenta> cuentas) {
        if ( cuentas == null ) {
            return null;
        }

        List<CuentaResponseDTO> list = new ArrayList<CuentaResponseDTO>( cuentas.size() );
        for ( Cuenta cuenta : cuentas ) {
            list.add( toResponse( cuenta ) );
        }

        return list;
    }
}
