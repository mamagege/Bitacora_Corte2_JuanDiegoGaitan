package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.ItemPedido;
import com.dows.bitacora2.restaurante.model.domain.Pedido;
import com.dows.bitacora2.restaurante.persistence.entity.ItemPedidoEntity;
import com.dows.bitacora2.restaurante.persistence.entity.PedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PedidoEntityMapper {
    PedidoEntity toEntity(Pedido pedido);
    Pedido toDomain(PedidoEntity entity);
    List<Pedido> toDomainList(List<PedidoEntity> entities);
    
    @org.mapstruct.Mapping(target = "id", ignore = true)
    ItemPedidoEntity toItemEntity(ItemPedido item);
    ItemPedido toItemDomain(ItemPedidoEntity entity);
}
