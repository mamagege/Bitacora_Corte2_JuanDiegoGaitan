package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.exception.RecursoNoEncontradoException;
import com.dows.bitacora2.restaurante.mapper.MesaEntityMapper;
import com.dows.bitacora2.restaurante.model.domain.EstadoMesa;
import com.dows.bitacora2.restaurante.model.domain.Mesa;
import com.dows.bitacora2.restaurante.persistence.entity.MesaEntity;
import com.dows.bitacora2.restaurante.repository.MesaRepository;
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
class MesaServiceImplTest {

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private MesaEntityMapper entityMapper;

    @InjectMocks
    private MesaServiceImpl mesaService;

    private Mesa mesa;
    private MesaEntity mesaEntity;

    @BeforeEach
    void setUp() {
        mesa = new Mesa();
        mesa.setId(1L);
        mesa.setNumero(5);
        mesa.setCapacidad(4);
        mesa.setEstado(EstadoMesa.DISPONIBLE);
        mesa.setCuentaAbierta(false);

        mesaEntity = new MesaEntity();
        mesaEntity.setId(1L);
    }

    @Test
    void obtenerPorId_MesaExiste_DebeRetornarMesa() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.of(mesaEntity));
        when(entityMapper.toDomain(mesaEntity)).thenReturn(mesa);

        Mesa result = mesaService.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals(5, result.getNumero());
    }

    @Test
    void obtenerPorId_MesaNoExiste_DebeLanzarExcepcion() {
        when(mesaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> {
            mesaService.obtenerPorId(1L);
        });
    }

    @Test
    void crear_MesaValida_DebeGuardarYRetornar() {
        when(entityMapper.toEntity(mesa)).thenReturn(mesaEntity);
        when(mesaRepository.save(mesaEntity)).thenReturn(mesaEntity);
        when(entityMapper.toDomain(mesaEntity)).thenReturn(mesa);

        Mesa result = mesaService.crear(mesa);

        assertNotNull(result);
        assertEquals(EstadoMesa.DISPONIBLE, result.getEstado());
        verify(mesaRepository).save(any());
    }
}
