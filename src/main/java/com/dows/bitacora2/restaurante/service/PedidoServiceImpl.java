package com.dows.bitacora2.restaurante.service;

import com.dows.bitacora2.restaurante.exception.EstadoInvalidoException;
import com.dows.bitacora2.restaurante.exception.RecursoNoEncontradoException;
import com.dows.bitacora2.restaurante.exception.ReglaDeNegocioException;
import com.dows.bitacora2.restaurante.exception.ToppingsExcedidosException;
import com.dows.bitacora2.restaurante.exception.TransicionEstadoInvalidaException;
import com.dows.bitacora2.restaurante.mapper.PedidoEntityMapper;
import com.dows.bitacora2.restaurante.model.domain.EstadoPedido;
import com.dows.bitacora2.restaurante.model.domain.ItemPedido;
import com.dows.bitacora2.restaurante.model.domain.Pedido;
import com.dows.bitacora2.restaurante.model.domain.Plato;
import com.dows.bitacora2.restaurante.persistence.entity.PedidoEntity;
import com.dows.bitacora2.restaurante.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.util.List;

@Service
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class PedidoServiceImpl implements IPedidoService {
    

    private final PedidoRepository pedidoRepository;
    private final PedidoEntityMapper entityMapper;
    private final IPlatoService platoService;
    private final IMesaService mesaService;
    private final com.dows.bitacora2.restaurante.repository.EventoPedidoMongoRepository eventoMongoRepo;

    @Override
    public List<Pedido> obtenerTodos() {
        log.info("Obteniendo todos los pedidos");
        return entityMapper.toDomainList(pedidoRepository.findAll());
    }

    @Override
    public Pedido obtenerPorId(Long id) {
        log.debug("Buscando con id={}", id);
        return pedidoRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElseThrow(() -> {
                    log.error("Pedido con id={} no encontrad@ (o error de flujo)", id);
                    return new RecursoNoEncontradoException("Pedido", id);
                });
    }

    @Override
    @Transactional
    public Pedido crear(Pedido pedido) {
        mesaService.obtenerPorId(pedido.getIdMesa());

        validarYCompletarItems(pedido.getItems());
        
        pedido.setEstado(EstadoPedido.RECIBIDO);
        PedidoEntity nuevo = pedidoRepository.save(entityMapper.toEntity(pedido));
        log.info("Pedido creado: id={}, mesa={}", nuevo.getId(), nuevo.getIdMesa());

        java.util.concurrent.CompletableFuture.runAsync(() -> {
            eventoMongoRepo.save(new com.dows.bitacora2.restaurante.persistence.document.EventoPedidoDocument(
                nuevo.getId(), null, "RECIBIDO", java.time.LocalDateTime.now(), "Pedido inicializado"
            ));
        });

        return entityMapper.toDomain(nuevo);
    }

    @Override
    @Transactional
    public Pedido actualizar(Long id, Pedido nuevosDatos) {
        Pedido existente = obtenerPorId(id);
        
        if (!existente.puedeModificarse()) {
            throw new ReglaDeNegocioException("RN-04: El pedido solo puede modificarse en estado RECIBIDO.");
        }

        validarYCompletarItems(nuevosDatos.getItems());
        existente.setItems(nuevosDatos.getItems());
        
        PedidoEntity entity = pedidoRepository.save(entityMapper.toEntity(existente));
        log.info("Pedido actualizado: id={}", id);
        return entityMapper.toDomain(entity);
    }

    @Override
    @Transactional
    public Pedido cambiarEstado(Long id, String estadoStr) {
        Pedido pedido = obtenerPorId(id);
        try {
            EstadoPedido nuevoEstado = EstadoPedido.valueOf(estadoStr.toUpperCase());
            
            if (!pedido.getEstado().puedeTransicionarA(nuevoEstado)) {
                throw new TransicionEstadoInvalidaException("No se puede pasar de " + pedido.getEstado() + " a " + nuevoEstado);
            }
            
            String estadoAnterior = pedido.getEstado().name();
            pedido.setEstado(nuevoEstado);
            PedidoEntity entity = pedidoRepository.save(entityMapper.toEntity(pedido));
            log.info("Estado de pedido id={} cambiado a {}", id, nuevoEstado);

            java.util.concurrent.CompletableFuture.runAsync(() -> {
                eventoMongoRepo.save(new com.dows.bitacora2.restaurante.persistence.document.EventoPedidoDocument(
                    id, estadoAnterior, nuevoEstado.name(), java.time.LocalDateTime.now(), "Cambio de estado"
                ));
            });

            return entityMapper.toDomain(entity);
        } catch (IllegalArgumentException e) {
            throw new EstadoInvalidoException("Estado de pedido invÃƒÂ¡lido: " + estadoStr);
        }
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Pedido pedido = obtenerPorId(id);
        if (!pedido.puedeModificarse()) {
            throw new ReglaDeNegocioException("El pedido no se puede eliminar porque ya estÃƒÂ¡ en preparaciÃƒÂ³n o listo.");
        }
        String estadoAnterior = pedido.getEstado().name();
        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(entityMapper.toEntity(pedido));
        log.info("Pedido cancelado: id={}", id);

        java.util.concurrent.CompletableFuture.runAsync(() -> {
            eventoMongoRepo.save(new com.dows.bitacora2.restaurante.persistence.document.EventoPedidoDocument(
                id, estadoAnterior, "CANCELADO", java.time.LocalDateTime.now(), "Pedido cancelado"
            ));
        });
    }

    private void validarYCompletarItems(List<ItemPedido> items) {
        for (ItemPedido item : items) {
            Plato plato = platoService.obtenerPorId(item.getIdPlato());
            
            if (!plato.estaDisponible()) {
                throw new ReglaDeNegocioException("El plato " + plato.getNombre() + " no estÃƒÂ¡ disponible.");
            }

            item.setNombrePlato(plato.getNombre());
            item.setPrecioCongelado(plato.getPrecio());

            boolean esPizza = "Pizza".equalsIgnoreCase(plato.getCategoria());
            boolean esPasta = "Pasta".equalsIgnoreCase(plato.getCategoria());

            if (esPizza || esPasta) {
                if (item.getMasa() == null || item.getMasa().trim().isEmpty() ||
                    item.getSalsa() == null || item.getSalsa().trim().isEmpty()) {
                    throw new ReglaDeNegocioException("RN-01: Todo plato personalizable debe tener masa base y salsa primaria.");
                }
            }

            if (esPizza) {
                if (item.getToppings() != null && item.getToppings().size() > 5) {
                    throw new ToppingsExcedidosException("RN-02: Una pizza no puede llevar mÃƒÂ¡s de 5 toppings.");
                }
            }
        }
    }

    @Override
    public com.dows.bitacora2.restaurante.model.dto.response.ResumenDiaDTO resumenDelDia() {
        java.util.List<com.dows.bitacora2.restaurante.persistence.entity.PedidoEntity> pedidos = pedidoRepository.findAll();

        long totalPedidos = pedidos.stream().count();

        double ingresoTotal = pedidos.stream()
                .flatMap(p -> p.getItems().stream())
                .mapToDouble(item -> item.getPrecioCongelado() * item.getCantidad())
                .sum();

        java.util.Map<String, Long> platosMasPedidos = pedidos.stream()
                .flatMap(p -> p.getItems().stream())
                .collect(java.util.stream.Collectors.groupingBy(
                        item -> item.getNombrePlato(),
                        java.util.stream.Collectors.counting()
                ));

        return new com.dows.bitacora2.restaurante.model.dto.response.ResumenDiaDTO(totalPedidos, ingresoTotal, platosMasPedidos);
    }
}