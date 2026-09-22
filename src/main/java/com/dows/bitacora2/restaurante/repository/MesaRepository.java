package com.dows.bitacora2.restaurante.repository;

import com.dows.bitacora2.restaurante.model.domain.Mesa;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MesaRepository {
    private final Map<Long, Mesa> mesas = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    public List<Mesa> findAll() {
        return mesas.values().stream().toList();
    }

    public Optional<Mesa> findById(Long id) {
        return Optional.ofNullable(mesas.get(id));
    }

    public Mesa save(Mesa mesa) {
        if (mesa.getId() == null) {
            mesa.setId(contador.getAndIncrement());
        }
        mesas.put(mesa.getId(), mesa);
        return mesa;
    }
}
