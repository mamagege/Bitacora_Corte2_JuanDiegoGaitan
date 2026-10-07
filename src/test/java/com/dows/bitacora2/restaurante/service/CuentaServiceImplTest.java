package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.exception.ReglaDeNegocioException;
import com.dows.bitacora2.restaurante.mapper.CuentaEntityMapper;
import com.dows.bitacora2.restaurante.model.domain.Cuenta;
import com.dows.bitacora2.restaurante.model.domain.EstadoCuenta;
import com.dows.bitacora2.restaurante.model.domain.Mesa;
import com.dows.bitacora2.restaurante.persistence.entity.CuentaEntity;
import com.dows.bitacora2.restaurante.persistence.entity.PedidoEntity;
import com.dows.bitacora2.restaurante.repository.CuentaRepository;
import com.dows.bitacora2.restaurante.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CuentaServiceImplTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @Mock
    private CuentaEntityMapper entityMapper;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private IMesaService mesaService;

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    private Cuenta cuenta;
    private CuentaEntity cuentaEntity;

    @BeforeEach
    void setUp() {
        cuenta = new Cuenta();
        cuenta.setId(1L);
        cuenta.setIdMesa(2L);
        cuenta.setEstado(EstadoCuenta.ABIERTA);

        cuentaEntity = new CuentaEntity();
        cuentaEntity.setId(1L);
        cuentaEntity.setIdMesa(2L);
    }

    @Test
    void crear_MesaValida_DebeAbrirCuenta() {
        Mesa mesa = new Mesa();
        mesa.setId(2L);
        mesa.setCuentaAbierta(false);

        when(mesaService.obtenerPorId(2L)).thenReturn(mesa);
        when(entityMapper.toEntity(cuenta)).thenReturn(cuentaEntity);
        when(cuentaRepository.save(cuentaEntity)).thenReturn(cuentaEntity);
        when(entityMapper.toDomain(cuentaEntity)).thenReturn(cuenta);

        Cuenta result = cuentaService.crear(cuenta);

        assertNotNull(result);
        assertEquals(EstadoCuenta.ABIERTA, result.getEstado());
        verify(mesaService).abrirCuenta(2L);
        verify(cuentaRepository).save(any());
    }

    @Test
    void pagar_CuentaAbiertaYPedidosEntregados_DebePagarYLiberarMesa() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaEntity));
        when(entityMapper.toDomain(cuentaEntity)).thenReturn(cuenta);
        when(pedidoRepository.findByIdMesa(2L)).thenReturn(java.util.Collections.emptyList());
        when(entityMapper.toEntity(cuenta)).thenReturn(cuentaEntity);
        when(cuentaRepository.save(cuentaEntity)).thenReturn(cuentaEntity);

        Cuenta result = cuentaService.pagar(1L);

        assertEquals(EstadoCuenta.CERRADA, cuenta.getEstado());
        verify(mesaService).cerrarCuenta(2L);
    }

    @Test
    void pagar_CuentaYaCerrada_DebeLanzarExcepcion() {
        cuenta.setEstado(EstadoCuenta.CERRADA);
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaEntity));
        when(entityMapper.toDomain(cuentaEntity)).thenReturn(cuenta);

        ReglaDeNegocioException exception = assertThrows(ReglaDeNegocioException.class, () -> {
            cuentaService.pagar(1L);
        });

        assertTrue(exception.getMessage().contains("pagada"));
    }

    @Test
    void pagar_CuentaConPedidosPendientes_DebeLanzarExcepcion() {
        when(cuentaRepository.findById(1L)).thenReturn(Optional.of(cuentaEntity));
        when(entityMapper.toDomain(cuentaEntity)).thenReturn(cuenta);
        
        PedidoEntity pedidoPendiente = new PedidoEntity();
        pedidoPendiente.setEstado(com.dows.bitacora2.restaurante.model.domain.EstadoPedido.EN_PREPARACION);
        when(pedidoRepository.findByIdMesa(2L)).thenReturn(java.util.Collections.singletonList(pedidoPendiente));

        ReglaDeNegocioException exception = assertThrows(ReglaDeNegocioException.class, () -> {
            cuentaService.pagar(1L);
        });

        assertTrue(exception.getMessage().contains("preparación"));
        verify(mesaService, never()).cerrarCuenta(any());
    }
}
