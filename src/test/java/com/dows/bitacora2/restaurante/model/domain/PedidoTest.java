package com.dows.bitacora2.restaurante.model.domain;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    @Test
    void testPuedeModificarse_CuandoEsRecibido_RetornaTrue() {
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoPedido.RECIBIDO);
        assertTrue(pedido.puedeModificarse());
    }

    @Test
    void testPuedeModificarse_CuandoNoEsRecibido_RetornaFalse() {
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoPedido.EN_PREPARACION);
        assertFalse(pedido.puedeModificarse());
    }

    @Test
    void testGettersYSetters() {
        Pedido pedido = new Pedido();
        pedido.setId(1L);
        pedido.setIdMesa(2L);
        pedido.setItems(new ArrayList<>());
        
        assertEquals(1L, pedido.getId());
        assertEquals(2L, pedido.getIdMesa());
        assertNotNull(pedido.getItems());
    }
}
