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



import java.util.List;

@Service
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class CuentaServiceImpl implements ICuentaService {
    

    private final CuentaRepository cuentaRepository;
    private final CuentaEntityMapper entityMapper;
    private final PedidoRepository pedidoRepository;
    private final IMesaService mesaService;

    @Override
    public List<Cuenta> obtenerTodas() {
        return entityMapper.toDomainList(cuentaRepository.findAll());
    }

    @Override
    public Cuenta obtenerPorId(Long id) {
        log.debug("Buscando con id={}", id);
        return cuentaRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> {
                    log.error("Cuenta con id={} no encontrad@ (o error de flujo)", id);
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
        
        List<PedidoEntity> pedidos = pedidoRepository.findByIdMesa(cuenta.getIdMesa());
        boolean hayPedidosPendientes = pedidos.stream()
                .anyMatch(p -> p.getEstado() != EstadoPedido.ENTREGADO && p.getEstado() != EstadoPedido.CANCELADO);
        
        if (hayPedidosPendientes) {
            throw new ReglaDeNegocioException("No se puede pagar la cuenta. Hay pedidos aún en preparación o sin entregar.");
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

