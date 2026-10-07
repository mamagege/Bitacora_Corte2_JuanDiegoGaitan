package com.dows.bitacora2.restaurante.mapper;

import com.dows.bitacora2.restaurante.model.domain.ItemPedido;
import com.dows.bitacora2.restaurante.model.domain.Pedido;
import com.dows.bitacora2.restaurante.persistence.entity.ItemPedidoEntity;
import com.dows.bitacora2.restaurante.persistence.entity.PedidoEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-07T02:20:42-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.12.1 (Eclipse Adoptium)"
)
@Component
public class PedidoEntityMapperImpl implements PedidoEntityMapper {

    @Override
    public PedidoEntity toEntity(Pedido pedido) {
        if ( pedido == null ) {
            return null;
        }

        PedidoEntity pedidoEntity = new PedidoEntity();

        pedidoEntity.setId( pedido.getId() );
        pedidoEntity.setIdMesa( pedido.getIdMesa() );
        pedidoEntity.setEstado( pedido.getEstado() );
        pedidoEntity.setTimestamp( pedido.getTimestamp() );
        pedidoEntity.setItems( itemPedidoListToItemPedidoEntityList( pedido.getItems() ) );

        return pedidoEntity;
    }

    @Override
    public Pedido toDomain(PedidoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Pedido pedido = new Pedido();

        pedido.setId( entity.getId() );
        pedido.setIdMesa( entity.getIdMesa() );
        pedido.setItems( itemPedidoEntityListToItemPedidoList( entity.getItems() ) );
        pedido.setEstado( entity.getEstado() );
        pedido.setTimestamp( entity.getTimestamp() );

        return pedido;
    }

    @Override
    public List<Pedido> toDomainList(List<PedidoEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<Pedido> list = new ArrayList<Pedido>( entities.size() );
        for ( PedidoEntity pedidoEntity : entities ) {
            list.add( toDomain( pedidoEntity ) );
        }

        return list;
    }

    @Override
    public ItemPedidoEntity toItemEntity(ItemPedido item) {
        if ( item == null ) {
            return null;
        }

        ItemPedidoEntity itemPedidoEntity = new ItemPedidoEntity();

        itemPedidoEntity.setIdPlato( item.getIdPlato() );
        itemPedidoEntity.setNombrePlato( item.getNombrePlato() );
        itemPedidoEntity.setPrecioCongelado( item.getPrecioCongelado() );
        itemPedidoEntity.setCantidad( item.getCantidad() );
        itemPedidoEntity.setMasa( item.getMasa() );
        itemPedidoEntity.setSalsa( item.getSalsa() );
        List<String> list = item.getToppings();
        if ( list != null ) {
            itemPedidoEntity.setToppings( new ArrayList<String>( list ) );
        }

        return itemPedidoEntity;
    }

    @Override
    public ItemPedido toItemDomain(ItemPedidoEntity entity) {
        if ( entity == null ) {
            return null;
        }

        ItemPedido itemPedido = new ItemPedido();

        itemPedido.setIdPlato( entity.getIdPlato() );
        itemPedido.setNombrePlato( entity.getNombrePlato() );
        itemPedido.setPrecioCongelado( entity.getPrecioCongelado() );
        itemPedido.setCantidad( entity.getCantidad() );
        itemPedido.setMasa( entity.getMasa() );
        itemPedido.setSalsa( entity.getSalsa() );
        List<String> list = entity.getToppings();
        if ( list != null ) {
            itemPedido.setToppings( new ArrayList<String>( list ) );
        }

        return itemPedido;
    }

    protected List<ItemPedidoEntity> itemPedidoListToItemPedidoEntityList(List<ItemPedido> list) {
        if ( list == null ) {
            return null;
        }

        List<ItemPedidoEntity> list1 = new ArrayList<ItemPedidoEntity>( list.size() );
        for ( ItemPedido itemPedido : list ) {
            list1.add( toItemEntity( itemPedido ) );
        }

        return list1;
    }

    protected List<ItemPedido> itemPedidoEntityListToItemPedidoList(List<ItemPedidoEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<ItemPedido> list1 = new ArrayList<ItemPedido>( list.size() );
        for ( ItemPedidoEntity itemPedidoEntity : list ) {
            list1.add( toItemDomain( itemPedidoEntity ) );
        }

        return list1;
    }
}
