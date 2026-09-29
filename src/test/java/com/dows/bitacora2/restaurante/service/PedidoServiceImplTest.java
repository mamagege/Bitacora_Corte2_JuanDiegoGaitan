package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.exception.ReglaDeNegocioException;
import com.dows.bitacora2.restaurante.exception.ToppingsExcedidosException;
import com.dows.bitacora2.restaurante.exception.TransicionEstadoInvalidaException;
import com.dows.bitacora2.restaurante.mapper.PedidoEntityMapper;
import com.dows.bitacora2.restaurante.model.domain.EstadoPedido;
import com.dows.bitacora2.restaurante.model.domain.ItemPedido;
import com.dows.bitacora2.restaurante.model.domain.Mesa;
import com.dows.bitacora2.restaurante.model.domain.Pedido;
import com.dows.bitacora2.restaurante.model.domain.Plato;
import com.dows.bitacora2.restaurante.persistence.document.EventoPedidoDocument;
import com.dows.bitacora2.restaurante.persistence.entity.PedidoEntity;
import com.dows.bitacora2.restaurante.repository.EventoPedidoMongoRepository;
import com.dows.bitacora2.restaurante.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoEntityMapper entityMapper;

    @Mock
    private IPlatoService platoService;

    @Mock
    private IMesaService mesaService;

    @Mock
    private EventoPedidoMongoRepository eventoMongoRepo;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Pedido pedidoBase;
    private Plato pizzaPlato;

    @BeforeEach
    void setUp() {
        // Arrange general
        ItemPedido item = new ItemPedido();
        item.setIdPlato(1L);
        item.setCantidad(2);
        item.setMasa("Fina");
        item.setSalsa("Tomate");

        pedidoBase = new Pedido();
        pedidoBase.setId(100L);
        pedidoBase.setIdMesa(1L);
        pedidoBase.setItems(List.of(item));
        pedidoBase.setEstado(EstadoPedido.RECIBIDO);

        pizzaPlato = new Plato();
        pizzaPlato.setId(1L);
        pizzaPlato.setNombre("Pizza Margarita");
        pizzaPlato.setCategoria("Pizza");
        pizzaPlato.setPrecio(15.0);
        pizzaPlato.setDisponible(true);
    }

    @Test
    void crear_ConMasaYSalsaEnPizza_DebeCrearPedidoCongelandoPrecio() {
        // Arrange
        when(mesaService.obtenerPorId(1L)).thenReturn(new Mesa());
        when(platoService.obtenerPorId(1L)).thenReturn(pizzaPlato);
        
        PedidoEntity entityMock = new PedidoEntity();
        entityMock.setId(100L);
        when(entityMapper.toEntity(any(Pedido.class))).thenReturn(entityMock);
        when(pedidoRepository.save(entityMock)).thenReturn(entityMock);
        when(entityMapper.toDomain(entityMock)).thenReturn(pedidoBase);

        // Act
        Pedido resultado = pedidoService.crear(pedidoBase);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoPedido.RECIBIDO, pedidoBase.getEstado());
        assertEquals(15.0, pedidoBase.getItems().get(0).getPrecioCongelado()); // RN-03 Precio Congelado
        assertEquals("Pizza Margarita", pedidoBase.getItems().get(0).getNombrePlato());
        verify(pedidoRepository, times(1)).save(any());
        // No verificamos el async acá directamente por complejidad de hilos en JUnit básico, pero validamos interacciones base.
    }

    @Test
    void crear_PizzaSinMasaNiSalsa_DebeLanzarReglaDeNegocioException() {
        // Arrange
        pedidoBase.getItems().get(0).setMasa(null);
        when(mesaService.obtenerPorId(1L)).thenReturn(new Mesa());
        when(platoService.obtenerPorId(1L)).thenReturn(pizzaPlato);

        // Act & Assert (RN-01)
        ReglaDeNegocioException exception = assertThrows(ReglaDeNegocioException.class, () -> {
            pedidoService.crear(pedidoBase);
        });
        assertTrue(exception.getMessage().contains("RN-01"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void crear_PizzaConMasDe5Toppings_DebeLanzarToppingsExcedidosException() {
        // Arrange
        pedidoBase.getItems().get(0).setToppings(Arrays.asList("T1", "T2", "T3", "T4", "T5", "T6"));
        when(mesaService.obtenerPorId(1L)).thenReturn(new Mesa());
        when(platoService.obtenerPorId(1L)).thenReturn(pizzaPlato);

        // Act & Assert (RN-02)
        ToppingsExcedidosException exception = assertThrows(ToppingsExcedidosException.class, () -> {
            pedidoService.crear(pedidoBase);
        });
        assertTrue(exception.getMessage().contains("RN-02"));
    }

    @Test
    void actualizar_PedidoEnPreparacion_DebeLanzarReglaDeNegocioException() {
        // Arrange
        pedidoBase.setEstado(EstadoPedido.EN_PREPARACION);
        
        PedidoEntity entityMock = new PedidoEntity();
        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(entityMock));
        when(entityMapper.toDomain(entityMock)).thenReturn(pedidoBase);

        // Act & Assert (RN-04)
        ReglaDeNegocioException exception = assertThrows(ReglaDeNegocioException.class, () -> {
            pedidoService.actualizar(100L, pedidoBase);
        });
        assertTrue(exception.getMessage().contains("RN-04"));
    }

    @Test
    void cambiarEstado_DeRecibidoAEnPreparacion_DebeActualizarEstadoExitosamente() {
        // Arrange
        pedidoBase.setEstado(EstadoPedido.RECIBIDO);
        PedidoEntity entityMock = new PedidoEntity();
        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(entityMock));
        when(entityMapper.toDomain(entityMock)).thenReturn(pedidoBase);
        when(entityMapper.toEntity(pedidoBase)).thenReturn(entityMock);
        when(pedidoRepository.save(entityMock)).thenReturn(entityMock);

        // Act
        Pedido resultado = pedidoService.cambiarEstado(100L, "EN_PREPARACION");

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoPedido.EN_PREPARACION, pedidoBase.getEstado());
        verify(pedidoRepository, times(1)).save(any());
    }

    @Test
    void cambiarEstado_DeRecibidoAListo_DebeLanzarTransicionEstadoInvalidaException() {
        // Arrange
        pedidoBase.setEstado(EstadoPedido.RECIBIDO);
        PedidoEntity entityMock = new PedidoEntity();
        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(entityMock));
        when(entityMapper.toDomain(entityMock)).thenReturn(pedidoBase);

        // Act & Assert
        assertThrows(TransicionEstadoInvalidaException.class, () -> {
            pedidoService.cambiarEstado(100L, "LISTO");
        });
    }
}
