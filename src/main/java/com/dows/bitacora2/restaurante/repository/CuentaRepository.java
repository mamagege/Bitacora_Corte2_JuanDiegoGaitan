package com.dows.bitacora2.restaurante.repository;

import com.dows.bitacora2.restaurante.model.domain.Cuenta;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class CuentaRepository {
    private final Map<Long, Cuenta> cuentas = new ConcurrentHashMap<>();
    private final AtomicLong contador = new AtomicLong(1);

    public List<Cuenta> findAll() {
        return cuentas.values().stream().toList();
    }

    public Optional<Cuenta> findById(Long id) {
        return Optional.ofNullable(cuentas.get(id));
    }

    public Cuenta save(Cuenta cuenta) {
        if (cuenta.getId() == null) {
            cuenta.setId(contador.getAndIncrement());
        }
        cuentas.put(cuenta.getId(), cuenta);
        return cuenta;
    }
}
