package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.ItemPedido;
import com.dows.bitacora2.restaurante.model.domain.Pedido;
import com.dows.bitacora2.restaurante.model.dto.request.ItemPedidoRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.request.PedidoRequestDTO;
import com.dows.bitacora2.restaurante.model.dto.response.ItemPedidoResponseDTO;
import com.dows.bitacora2.restaurante.model.dto.response.PedidoResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estado", expression = "java(com.dows.bitacora2.restaurante.model.domain.EstadoPedido.RECIBIDO)")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    Pedido toDomain(PedidoRequestDTO dto);

    @Mapping(target = "nombrePlato", ignore = true)
    @Mapping(target = "precioCongelado", ignore = true)
    ItemPedido toDomain(ItemPedidoRequestDTO dto);

    @Mapping(target = "totalPedido", expression = "java(calcularTotalPedido(pedido))")
    PedidoResponseDTO toResponse(Pedido pedido);

    List<PedidoResponseDTO> toResponseList(List<Pedido> pedidos);

    @Mapping(target = "subtotal", expression = "java(item.subtotal())")
    ItemPedidoResponseDTO toResponse(ItemPedido item);

    default Double calcularTotalPedido(Pedido pedido) {
        if (pedido == null || pedido.getItems() == null) return 0.0;
        return pedido.getItems().stream()
                .mapToDouble(ItemPedido::subtotal)
                .sum();
    }
}
