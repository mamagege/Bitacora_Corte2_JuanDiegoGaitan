package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.exception.RecursoNoEncontradoException;
import com.dows.bitacora2.restaurante.mapper.PlatoEntityMapper;
import com.dows.bitacora2.restaurante.model.domain.Plato;
import com.dows.bitacora2.restaurante.persistence.entity.PlatoEntity;
import com.dows.bitacora2.restaurante.repository.PlatoRepository;
import com.dows.bitacora2.restaurante.validator.IPlatoValidator;
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
class PlatoServiceImplTest {

    @Mock
    private PlatoRepository platoRepository;

    @Mock
    private PlatoEntityMapper entityMapper;

    @Mock
    private IPlatoValidator validator;

    @InjectMocks
    private PlatoServiceImpl platoService;

    private Plato plato;
    private PlatoEntity entity;

    @BeforeEach
    void setUp() {
        plato = new Plato();
        plato.setId(1L);
        plato.setNombre("Pasta");
        plato.setDisponible(true);

        entity = new PlatoEntity();
    }

    @Test
    void obtenerPorId_PlatoExiste_RetornaPlato() {
        when(platoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(entity)).thenReturn(plato);

        Plato result = platoService.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals("Pasta", result.getNombre());
    }

    @Test
    void crear_PlatoNuevo_RetornaPlatoCreado() {
        when(platoRepository.existsByNombreIgnoreCase("Pasta")).thenReturn(false);
        when(entityMapper.toEntity(plato)).thenReturn(entity);
        when(platoRepository.save(entity)).thenReturn(entity);
        when(entityMapper.toDomain(entity)).thenReturn(plato);

        Plato result = platoService.crear(plato);

        assertNotNull(result);
        verify(platoRepository).save(any());
    }

    @Test
    void cambiarDisponibilidad_PlatoExiste_CambiaEstado() {
        when(platoRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(entityMapper.toDomain(any())).thenReturn(plato);
        when(platoRepository.save(any())).thenReturn(entity);

        Plato result = platoService.cambiarDisponibilidad(1L, false);

        assertNotNull(result);
        verify(platoRepository).save(entity);
    }

    @Test
    void crear_nombreDuplicado_debeLanzarExcepcion() {
        when(platoRepository.existsByNombreIgnoreCase("Pasta")).thenReturn(true);

        com.dows.bitacora2.restaurante.exception.ConflictoException ex = assertThrows(
            com.dows.bitacora2.restaurante.exception.ConflictoException.class,
            () -> platoService.crear(plato)
        );

        assertTrue(ex.getMessage().contains("Ya existe"));
        verify(platoRepository, never()).save(any());
    }

    @Test
    void buscarPorId_idInexistente_debeLanzarExcepcion() {
        when(platoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(
            RecursoNoEncontradoException.class,
            () -> platoService.obtenerPorId(999L)
        );

        verifyNoInteractions(entityMapper);
    }

    @Test
    void obtenerTodos_listaVacia_devuelveListaVacia() {
        when(platoRepository.findAll()).thenReturn(java.util.Collections.emptyList());
        when(entityMapper.toDomainList(java.util.Collections.emptyList())).thenReturn(java.util.Collections.emptyList());

        java.util.List<Plato> resultado = platoService.obtenerTodos();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}