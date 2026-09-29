package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.exception.RecursoNoEncontradoException;
import com.dows.bitacora2.restaurante.exception.ReglaDeNegocioException;
import com.dows.bitacora2.restaurante.mapper.CuentaEntityMapper;
import com.dows.bitacora2.restaurante.model.domain.Cuenta;
import com.dows.bitacora2.restaurante.model.domain.EstadoCuenta;
import com.dows.bitacora2.restaurante.model.domain.EstadoPedido;
import com.dows.bitacora2.restaurante.model.domain.ItemPedido;
import com.dows.bitacora2.restaurante.persistence.entity.CuentaEntity;
import com.dows.bitacora2.restaurante.persistence.entity.PedidoEntity;
import com.dows.bitacora2.restaurante.repository.CuentaRepository;
import com.dows.bitacora2.restaurante.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class CuentaServiceImpl implements ICuentaService {
    private static final Logger log = LoggerFactory.getLogger(CuentaServiceImpl.class);

    private final CuentaRepository cuentaRepository;
    private final CuentaEntityMapper entityMapper;
    private final PedidoRepository pedidoRepository;
    private final IMesaService mesaService;

    public CuentaServiceImpl(CuentaRepository cuentaRepository, CuentaEntityMapper entityMapper, PedidoRepository pedidoRepository, IMesaService mesaService) {
        this.cuentaRepository = cuentaRepository;
        this.entityMapper = entityMapper;
        this.pedidoRepository = pedidoRepository;
        this.mesaService = mesaService;
    }

    @Override
    public List<Cuenta> obtenerTodas() {
        return entityMapper.toDomainList(cuentaRepository.findAll());
    }

    @Override
    public Cuenta obtenerPorId(Long id) {
        return cuentaRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> {
                    log.warn("Cuenta no encontrada: id={}", id);
                    return new RecursoNoEncontradoException("Cuenta", id);
                });
    }

    @Override
    @Transactional
    public Cuenta crear(Cuenta cuenta) {
        mesaService.obtenerPorId(cuenta.getIdMesa());
        mesaService.abrirCuenta(cuenta.getIdMesa());
        cuenta.setEstado(EstadoCuenta.ABIERTA);
        cuenta.setTotal(0.0);
        CuentaEntity nueva = cuentaRepository.save(entityMapper.toEntity(cuenta));
        log.info("Cuenta creada para la mesa id={}", nueva.getIdMesa());
        return entityMapper.toDomain(nueva);
    }

    @Override
    @Transactional
    public Cuenta actualizarTotal(Long id) {
        Cuenta cuenta = obtenerPorId(id);
        if (cuenta.getEstado() == EstadoCuenta.CERRADA) {
            throw new ReglaDeNegocioException("La cuenta ya está pagada y no se puede actualizar.");
        }

        List<PedidoEntity> pedidos = pedidoRepository.findByIdMesa(cuenta.getIdMesa());
        double total = pedidos.stream()
                .filter(p -> p.getEstado() != EstadoPedido.CANCELADO)
                .flatMap(p -> p.getItems().stream())
                .mapToDouble(item -> item.getPrecioCongelado() * item.getCantidad())
                .sum();
        
        cuenta.setTotal(total);
        CuentaEntity entity = cuentaRepository.save(entityMapper.toEntity(cuenta));
        log.info("Total actualizado para cuenta id={}, nuevo total={}", id, total);
        return entityMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public Cuenta pagar(Long id) {
        Cuenta cuenta = obtenerPorId(id);
        if (cuenta.getEstado() == EstadoCuenta.CERRADA) {
            throw new ReglaDeNegocioException("La cuenta ya se encuentra pagada.");
        }
        
        actualizarTotal(id);
        cuenta = obtenerPorId(id); // recargar
        
        cuenta.setEstado(EstadoCuenta.CERRADA);
        mesaService.cerrarCuenta(cuenta.getIdMesa());
        CuentaEntity entity = cuentaRepository.save(entityMapper.toEntity(cuenta));
        log.info("Cuenta pagada: id={}, total={}", id, cuenta.getTotal());
        return entityMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public Cuenta asociarPedido(Long idCuenta, Long idPedido) {
        return actualizarTotal(idCuenta);
    }
}
