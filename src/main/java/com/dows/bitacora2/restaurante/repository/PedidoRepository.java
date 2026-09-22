package com.dows.bitacora2.restaurante.repository;

import com.dows.bitacora2.restaurante.model.domain.Pedido;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PedidoRepository {
    private final Map<Long, Pedido> pedidos = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    public List<Pedido> findAll() {
        return pedidos.values().stream().toList();
    }

    public Optional<Pedido> findById(Long id) {
        return Optional.ofNullable(pedidos.get(id));
    }

    public List<Pedido> findByMesaId(Long idMesa) {
        return pedidos.values().stream()
                .filter(p -> p.getIdMesa().equals(idMesa))
                .toList();
    }

    public Pedido save(Pedido pedido) {
        if (pedido.getId() == null) {
            pedido.setId(contador.getAndIncrement());
        }
        pedidos.put(pedido.getId(), pedido);
        return pedido;
    }
}
