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
    date = "2026-09-29T00:08:49-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.2 (Oracle Corporation)"
)
@Component
public class CuentaMapperImpl implements CuentaMapper {

    @Override
    public Cuenta toDomain(CuentaRequestDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Cuenta cuenta = new Cuenta();

        cuenta.setIdMesa( dto.getIdMesa() );

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

        CuentaResponseDTO cuentaResponseDTO = new CuentaResponseDTO();

        cuentaResponseDTO.setId( cuenta.getId() );
        cuentaResponseDTO.setIdMesa( cuenta.getIdMesa() );
        cuentaResponseDTO.setTotal( cuenta.getTotal() );
        if ( cuenta.getEstado() != null ) {
            cuentaResponseDTO.setEstado( cuenta.getEstado().name() );
        }
        cuentaResponseDTO.setFechaApertura( cuenta.getFechaApertura() );

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
